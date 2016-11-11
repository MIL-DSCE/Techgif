package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.exoplayer.C0747C;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.extractor.ts.PsExtractor;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PopupAudioView;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TypingDotsDrawable;
import org.telegram.ui.Components.VideoPlayer;

public class PopupNotificationActivity extends Activity implements NotificationCenterDelegate {
    private ActionBar actionBar;
    private boolean animationInProgress;
    private long animationStartTime;
    private ArrayList<ViewGroup> audioViews;
    private FrameLayout avatarContainer;
    private BackupImageView avatarImageView;
    private ViewGroup centerView;
    private ChatActivityEnterView chatActivityEnterView;
    private int classGuid;
    private TextView countText;
    private Chat currentChat;
    private int currentMessageNum;
    private MessageObject currentMessageObject;
    private User currentUser;
    private boolean finished;
    private ArrayList<ViewGroup> imageViews;
    private CharSequence lastPrintString;
    private ViewGroup leftView;
    private ViewGroup messageContainer;
    private float moveStartX;
    private TextView nameTextView;
    private Runnable onAnimationEndRunnable;
    private TextView onlineTextView;
    private RelativeLayout popupContainer;
    private RecordStatusDrawable recordStatusDrawable;
    private ViewGroup rightView;
    private boolean startedMoving;
    private ArrayList<ViewGroup> textViews;
    private TypingDotsDrawable typingDotsDrawable;
    private VelocityTracker velocityTracker;
    private WakeLock wakeLock;

    /* renamed from: org.telegram.ui.PopupNotificationActivity.4 */
    class C13774 implements OnClickListener {
        C13774() {
        }

        @TargetApi(9)
        public void onClick(DialogInterface dialog, int which) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                PopupNotificationActivity.this.startActivity(intent);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity.5 */
    class C13785 implements Runnable {
        C13785() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.switchToPreviousMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity.6 */
    class C13796 implements Runnable {
        C13796() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.switchToNextMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity.7 */
    class C13807 implements Runnable {
        C13807() {
        }

        public void run() {
            PopupNotificationActivity.this.animationInProgress = false;
            PopupNotificationActivity.this.applyViewsLayoutParams(0);
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity.8 */
    class C13818 implements View.OnClickListener {
        C13818() {
        }

        public void onClick(View v) {
            PopupNotificationActivity.this.openCurrentMessage();
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity.9 */
    class C13829 implements View.OnClickListener {
        C13829() {
        }

        public void onClick(View v) {
            PopupNotificationActivity.this.openCurrentMessage();
        }
    }

    public class FrameLayoutAnimationListener extends FrameLayout {
        public FrameLayoutAnimationListener(Context context) {
            super(context);
        }

        public FrameLayoutAnimationListener(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FrameLayoutAnimationListener(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        protected void onAnimationEnd() {
            super.onAnimationEnd();
            if (PopupNotificationActivity.this.onAnimationEndRunnable != null) {
                PopupNotificationActivity.this.onAnimationEndRunnable.run();
                PopupNotificationActivity.this.onAnimationEndRunnable = null;
            }
        }
    }

    private class FrameLayoutTouch extends FrameLayout {
        public FrameLayoutTouch(Context context) {
            super(context);
        }

        public FrameLayoutTouch(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public FrameLayoutTouch(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(ev);
        }

        public boolean onTouchEvent(MotionEvent ev) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(ev);
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            ((PopupNotificationActivity) getContext()).onTouchEventMy(null);
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity.1 */
    class C19161 extends SizeNotifierFrameLayout {
        C19161(Context x0) {
            super(x0);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                heightSize -= PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(child)) {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C0747C.ENCODING_PCM_32BIT));
                    } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(child)) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    } else {
                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, C0747C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + heightSize), C0747C.ENCODING_PCM_32BIT));
                    }
                }
            }
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int count = getChildCount();
            int paddingBottom = getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding() : 0;
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    int childLeft;
                    int childTop;
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int verticalGravity = gravity & 112;
                    switch ((gravity & 7) & 7) {
                        case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                            childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case VideoPlayer.STATE_ENDED /*5*/:
                            childLeft = (r - width) - lp.rightMargin;
                            break;
                        default:
                            childLeft = lp.leftMargin;
                            break;
                    }
                    switch (verticalGravity) {
                        case ItemTouchHelper.START /*16*/:
                            childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            childTop = lp.topMargin;
                            break;
                        case 80:
                            childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                            break;
                    }
                    if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(child)) {
                        childTop = paddingBottom != 0 ? getMeasuredHeight() - paddingBottom : getMeasuredHeight();
                    } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(child)) {
                        childTop = ((PopupNotificationActivity.this.popupContainer.getTop() + PopupNotificationActivity.this.popupContainer.getMeasuredHeight()) - child.getMeasuredHeight()) - lp.bottomMargin;
                        childLeft = ((PopupNotificationActivity.this.popupContainer.getLeft() + PopupNotificationActivity.this.popupContainer.getMeasuredWidth()) - child.getMeasuredWidth()) - lp.rightMargin;
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
            }
            notifyHeightChanged();
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity.2 */
    class C19172 implements ChatActivityEnterViewDelegate {
        C19172() {
        }

        public void onMessageSend(CharSequence message) {
            if (PopupNotificationActivity.this.currentMessageObject != null) {
                if (PopupNotificationActivity.this.currentMessageNum >= 0 && PopupNotificationActivity.this.currentMessageNum < NotificationsController.getInstance().popupMessages.size()) {
                    NotificationsController.getInstance().popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
                }
                MessagesController.getInstance().markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, true);
                PopupNotificationActivity.this.currentMessageObject = null;
                PopupNotificationActivity.this.getNewMessage();
            }
        }

        public void onTextChanged(CharSequence text, boolean big) {
        }

        public void onMessageEditEnd(boolean loading) {
        }

        public void needSendTyping() {
            if (PopupNotificationActivity.this.currentMessageObject != null) {
                MessagesController.getInstance().sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, PopupNotificationActivity.this.classGuid);
            }
        }

        public void onAttachButtonHidden() {
        }

        public void onAttachButtonShow() {
        }

        public void onWindowSizeChanged(int size) {
        }

        public void onStickersTab(boolean opened) {
        }
    }

    /* renamed from: org.telegram.ui.PopupNotificationActivity.3 */
    class C19183 extends ActionBarMenuOnItemClick {
        C19183() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                PopupNotificationActivity.this.onFinish();
                PopupNotificationActivity.this.finish();
            } else if (id == 1) {
                PopupNotificationActivity.this.openCurrentMessage();
            } else if (id == 2) {
                PopupNotificationActivity.this.switchToNextMessage();
            }
        }
    }

    public PopupNotificationActivity() {
        this.textViews = new ArrayList();
        this.imageViews = new ArrayList();
        this.audioViews = new ArrayList();
        this.velocityTracker = null;
        this.finished = false;
        this.currentMessageObject = null;
        this.currentMessageNum = 0;
        this.wakeLock = null;
        this.animationInProgress = false;
        this.animationStartTime = 0;
        this.moveStartX = GroundOverlayOptions.NO_DIMENSION;
        this.startedMoving = false;
        this.onAnimationEndRunnable = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.loadRecources(this);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        this.typingDotsDrawable = new TypingDotsDrawable();
        this.recordStatusDrawable = new RecordStatusDrawable();
        SizeNotifierFrameLayout contentView = new C19161(this);
        setContentView(contentView);
        contentView.setBackgroundColor(-1728053248);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        contentView.addView(relativeLayout, LayoutHelper.createFrame(-1, GroundOverlayOptions.NO_DIMENSION));
        this.popupContainer = new RelativeLayout(this);
        this.popupContainer.setBackgroundColor(-1);
        View view = this.popupContainer;
        relativeLayout.addView(r17, LayoutHelper.createRelative(-1, PsExtractor.VIDEO_STREAM_MASK, 12, 0, 12, 0, 13));
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.onDestroy();
        }
        this.chatActivityEnterView = new ChatActivityEnterView(this, contentView, null, false);
        this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        this.chatActivityEnterView.setDelegate(new C19172());
        this.messageContainer = new FrameLayoutTouch(this);
        this.popupContainer.addView(this.messageContainer, 0);
        this.actionBar = new ActionBar(this);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_COLOR);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_SELECTOR_COLOR);
        this.popupContainer.addView(this.actionBar);
        ViewGroup.LayoutParams layoutParams = this.actionBar.getLayoutParams();
        layoutParams.width = -1;
        this.actionBar.setLayoutParams(layoutParams);
        this.countText = (TextView) this.actionBar.createMenu().addItemResource(2, C0691R.layout.popup_count_layout).findViewById(C0691R.id.count_text);
        this.avatarContainer = new FrameLayout(this);
        this.avatarContainer.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.actionBar.addView(this.avatarContainer);
        LayoutParams layoutParams2 = (LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams2.height = -1;
        layoutParams2.width = -2;
        layoutParams2.rightMargin = AndroidUtilities.dp(48.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        layoutParams2.gravity = 51;
        this.avatarContainer.setLayoutParams(layoutParams2);
        this.avatarImageView = new BackupImageView(this);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarContainer.addView(this.avatarImageView);
        layoutParams2 = (LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams2.width = AndroidUtilities.dp(42.0f);
        layoutParams2.height = AndroidUtilities.dp(42.0f);
        layoutParams2.topMargin = AndroidUtilities.dp(3.0f);
        this.avatarImageView.setLayoutParams(layoutParams2);
        this.nameTextView = new TextView(this);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.avatarContainer.addView(this.nameTextView);
        layoutParams2 = (LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams2.bottomMargin = AndroidUtilities.dp(22.0f);
        layoutParams2.gravity = 80;
        this.nameTextView.setLayoutParams(layoutParams2);
        this.onlineTextView = new TextView(this);
        this.onlineTextView.setTextColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(3);
        this.avatarContainer.addView(this.onlineTextView);
        layoutParams2 = (LayoutParams) this.onlineTextView.getLayoutParams();
        layoutParams2.width = -2;
        layoutParams2.height = -2;
        layoutParams2.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams2.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams2.gravity = 80;
        this.onlineTextView.setLayoutParams(layoutParams2);
        this.actionBar.setActionBarMenuOnItemClick(new C19183());
        this.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(268435462, "screen");
        this.wakeLock.setReferenceCounted(false);
        handleIntent(getIntent());
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AndroidUtilities.checkDisplaySize();
        fixLayout();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3 && grantResults[0] != 0) {
            Builder builder = new Builder(this);
            builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
            builder.setMessage(LocaleController.getString("PermissionNoAudio", C0691R.string.PermissionNoAudio));
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", C0691R.string.PermissionOpenSettings), new C13774());
            builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), null);
            builder.show();
        }
    }

    private void switchToNextMessage() {
        if (NotificationsController.getInstance().popupMessages.size() > 1) {
            if (this.currentMessageNum < NotificationsController.getInstance().popupMessages.size() - 1) {
                this.currentMessageNum++;
            } else {
                this.currentMessageNum = 0;
            }
            this.currentMessageObject = (MessageObject) NotificationsController.getInstance().popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(2);
            this.countText.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(NotificationsController.getInstance().popupMessages.size())}));
        }
    }

    private void switchToPreviousMessage() {
        if (NotificationsController.getInstance().popupMessages.size() > 1) {
            if (this.currentMessageNum > 0) {
                this.currentMessageNum--;
            } else {
                this.currentMessageNum = NotificationsController.getInstance().popupMessages.size() - 1;
            }
            this.currentMessageObject = (MessageObject) NotificationsController.getInstance().popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(1);
            this.countText.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(NotificationsController.getInstance().popupMessages.size())}));
        }
    }

    public boolean checkTransitionAnimation() {
        if (this.animationInProgress && this.animationStartTime < System.currentTimeMillis() - 400) {
            this.animationInProgress = false;
            if (this.onAnimationEndRunnable != null) {
                this.onAnimationEndRunnable.run();
                this.onAnimationEndRunnable = null;
            }
        }
        return this.animationInProgress;
    }

    public boolean onTouchEventMy(MotionEvent motionEvent) {
        if (checkTransitionAnimation()) {
            return false;
        }
        if (motionEvent != null && motionEvent.getAction() == 0) {
            this.moveStartX = motionEvent.getX();
        } else if (motionEvent != null && motionEvent.getAction() == 2) {
            float x = motionEvent.getX();
            diff = (int) (x - this.moveStartX);
            if (!(this.moveStartX == GroundOverlayOptions.NO_DIMENSION || this.startedMoving || Math.abs(diff) <= AndroidUtilities.dp(10.0f))) {
                this.startedMoving = true;
                this.moveStartX = x;
                AndroidUtilities.lockOrientation(this);
                diff = 0;
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                } else {
                    this.velocityTracker.clear();
                }
            }
            if (this.startedMoving) {
                if (this.leftView == null && diff > 0) {
                    diff = 0;
                }
                if (this.rightView == null && diff < 0) {
                    diff = 0;
                }
                if (this.velocityTracker != null) {
                    this.velocityTracker.addMovement(motionEvent);
                }
                applyViewsLayoutParams(diff);
            }
        } else if (motionEvent == null || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent == null || !this.startedMoving) {
                applyViewsLayoutParams(0);
            } else {
                LayoutParams layoutParams = (LayoutParams) this.centerView.getLayoutParams();
                diff = (int) (motionEvent.getX() - this.moveStartX);
                int width = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                int moveDiff = 0;
                int forceMove = 0;
                View otherView = null;
                if (this.velocityTracker != null) {
                    this.velocityTracker.computeCurrentVelocity(1000);
                    if (this.velocityTracker.getXVelocity() >= 3500.0f) {
                        forceMove = 1;
                    } else if (this.velocityTracker.getXVelocity() <= -3500.0f) {
                        forceMove = 2;
                    }
                }
                if ((forceMove == 1 || diff > width / 3) && this.leftView != null) {
                    moveDiff = width - layoutParams.leftMargin;
                    otherView = this.leftView;
                    this.onAnimationEndRunnable = new C13785();
                } else if ((forceMove == 2 || diff < (-width) / 3) && this.rightView != null) {
                    moveDiff = (-width) - layoutParams.leftMargin;
                    otherView = this.rightView;
                    this.onAnimationEndRunnable = new C13796();
                } else if (layoutParams.leftMargin != 0) {
                    moveDiff = -layoutParams.leftMargin;
                    otherView = diff > 0 ? this.leftView : this.rightView;
                    this.onAnimationEndRunnable = new C13807();
                }
                if (moveDiff != 0) {
                    int time = (int) (Math.abs(((float) moveDiff) / ((float) width)) * 200.0f);
                    TranslateAnimation animation = new TranslateAnimation(0.0f, (float) moveDiff, 0.0f, 0.0f);
                    animation.setDuration((long) time);
                    this.centerView.startAnimation(animation);
                    if (otherView != null) {
                        animation = new TranslateAnimation(0.0f, (float) moveDiff, 0.0f, 0.0f);
                        animation.setDuration((long) time);
                        otherView.startAnimation(animation);
                    }
                    this.animationInProgress = true;
                    this.animationStartTime = System.currentTimeMillis();
                }
            }
            if (this.velocityTracker != null) {
                this.velocityTracker.recycle();
                this.velocityTracker = null;
            }
            this.startedMoving = false;
            this.moveStartX = GroundOverlayOptions.NO_DIMENSION;
        }
        return this.startedMoving;
    }

    private void applyViewsLayoutParams(int xOffset) {
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        if (this.leftView != null) {
            LayoutParams layoutParams = (LayoutParams) this.leftView.getLayoutParams();
            layoutParams.gravity = 51;
            layoutParams.height = -1;
            layoutParams.width = widht;
            layoutParams.leftMargin = (-widht) + xOffset;
            this.leftView.setLayoutParams(layoutParams);
        }
        if (this.centerView != null) {
            layoutParams = (LayoutParams) this.centerView.getLayoutParams();
            layoutParams.gravity = 51;
            layoutParams.height = -1;
            layoutParams.width = widht;
            layoutParams.leftMargin = xOffset;
            this.centerView.setLayoutParams(layoutParams);
        }
        if (this.rightView != null) {
            layoutParams = (LayoutParams) this.rightView.getLayoutParams();
            layoutParams.gravity = 51;
            layoutParams.height = -1;
            layoutParams.width = widht;
            layoutParams.leftMargin = widht + xOffset;
            this.rightView.setLayoutParams(layoutParams);
        }
        this.messageContainer.invalidate();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.ViewGroup getViewForMessage(int r31, boolean r32) {
        /*
        r30 = this;
        r23 = org.telegram.messenger.NotificationsController.getInstance();
        r0 = r23;
        r0 = r0.popupMessages;
        r23 = r0;
        r23 = r23.size();
        r24 = 1;
        r0 = r23;
        r1 = r24;
        if (r0 != r1) goto L_0x002f;
    L_0x0016:
        if (r31 < 0) goto L_0x002c;
    L_0x0018:
        r23 = org.telegram.messenger.NotificationsController.getInstance();
        r0 = r23;
        r0 = r0.popupMessages;
        r23 = r0;
        r23 = r23.size();
        r0 = r31;
        r1 = r23;
        if (r0 < r1) goto L_0x002f;
    L_0x002c:
        r21 = 0;
    L_0x002e:
        return r21;
    L_0x002f:
        r23 = -1;
        r0 = r31;
        r1 = r23;
        if (r0 != r1) goto L_0x01bf;
    L_0x0037:
        r23 = org.telegram.messenger.NotificationsController.getInstance();
        r0 = r23;
        r0 = r0.popupMessages;
        r23 = r0;
        r23 = r23.size();
        r31 = r23 + -1;
    L_0x0047:
        r23 = org.telegram.messenger.NotificationsController.getInstance();
        r0 = r23;
        r0 = r0.popupMessages;
        r23 = r0;
        r0 = r23;
        r1 = r31;
        r13 = r0.get(r1);
        r13 = (org.telegram.messenger.MessageObject) r13;
        r0 = r13.type;
        r23 = r0;
        r24 = 1;
        r0 = r23;
        r1 = r24;
        if (r0 == r1) goto L_0x0073;
    L_0x0067:
        r0 = r13.type;
        r23 = r0;
        r24 = 4;
        r0 = r23;
        r1 = r24;
        if (r0 != r1) goto L_0x02ec;
    L_0x0073:
        r0 = r30;
        r0 = r0.imageViews;
        r23 = r0;
        r23 = r23.size();
        if (r23 <= 0) goto L_0x01d7;
    L_0x007f:
        r0 = r30;
        r0 = r0.imageViews;
        r23 = r0;
        r24 = 0;
        r21 = r23.get(r24);
        r21 = (android.view.ViewGroup) r21;
        r0 = r30;
        r0 = r0.imageViews;
        r23 = r0;
        r24 = 0;
        r23.remove(r24);
    L_0x0098:
        r23 = 2131558450; // 0x7f0d0032 float:1.8742216E38 double:1.0531298022E-314;
        r0 = r21;
        r1 = r23;
        r16 = r0.findViewById(r1);
        r16 = (android.widget.TextView) r16;
        r23 = 2131558463; // 0x7f0d003f float:1.8742243E38 double:1.0531298087E-314;
        r0 = r21;
        r1 = r23;
        r9 = r0.findViewById(r1);
        r9 = (org.telegram.ui.Components.BackupImageView) r9;
        r23 = 1;
        r0 = r23;
        r9.setAspectFit(r0);
        r0 = r13.type;
        r23 = r0;
        r24 = 1;
        r0 = r23;
        r1 = r24;
        if (r0 != r1) goto L_0x0242;
    L_0x00c5:
        r0 = r13.photoThumbs;
        r23 = r0;
        r24 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r23, r24);
        r0 = r13.photoThumbs;
        r23 = r0;
        r24 = 100;
        r20 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r23, r24);
        r18 = 0;
        if (r7 == 0) goto L_0x0128;
    L_0x00df:
        r17 = 1;
        r0 = r13.type;
        r23 = r0;
        r24 = 1;
        r0 = r23;
        r1 = r24;
        if (r0 != r1) goto L_0x00fd;
    L_0x00ed:
        r0 = r13.messageOwner;
        r23 = r0;
        r5 = org.telegram.messenger.FileLoader.getPathToMessage(r23);
        r23 = r5.exists();
        if (r23 != 0) goto L_0x00fd;
    L_0x00fb:
        r17 = 0;
    L_0x00fd:
        if (r17 != 0) goto L_0x010b;
    L_0x00ff:
        r23 = org.telegram.messenger.MediaController.getInstance();
        r24 = 1;
        r23 = r23.canDownloadMedia(r24);
        if (r23 == 0) goto L_0x0215;
    L_0x010b:
        r0 = r7.location;
        r23 = r0;
        r24 = "100_100";
        r0 = r20;
        r0 = r0.location;
        r25 = r0;
        r0 = r7.size;
        r26 = r0;
        r0 = r23;
        r1 = r24;
        r2 = r25;
        r3 = r26;
        r9.setImage(r0, r1, r2, r3);
        r18 = 1;
    L_0x0128:
        if (r18 != 0) goto L_0x0230;
    L_0x012a:
        r23 = 8;
        r0 = r23;
        r9.setVisibility(r0);
        r23 = 0;
        r0 = r16;
        r1 = r23;
        r0.setVisibility(r1);
        r23 = 2;
        r24 = org.telegram.messenger.MessagesController.getInstance();
        r0 = r24;
        r0 = r0.fontSize;
        r24 = r0;
        r0 = r24;
        r0 = (float) r0;
        r24 = r0;
        r0 = r16;
        r1 = r23;
        r2 = r24;
        r0.setTextSize(r1, r2);
        r0 = r13.messageText;
        r23 = r0;
        r0 = r16;
        r1 = r23;
        r0.setText(r1);
    L_0x015f:
        r23 = r21.getParent();
        if (r23 != 0) goto L_0x0172;
    L_0x0165:
        r0 = r30;
        r0 = r0.messageContainer;
        r23 = r0;
        r0 = r23;
        r1 = r21;
        r0.addView(r1);
    L_0x0172:
        r23 = 0;
        r0 = r21;
        r1 = r23;
        r0.setVisibility(r1);
        if (r32 == 0) goto L_0x002e;
    L_0x017d:
        r23 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r23;
        r0 = r0.x;
        r23 = r0;
        r24 = 1103101952; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r24 = org.telegram.messenger.AndroidUtilities.dp(r24);
        r22 = r23 - r24;
        r12 = r21.getLayoutParams();
        r12 = (android.widget.FrameLayout.LayoutParams) r12;
        r23 = 51;
        r0 = r23;
        r12.gravity = r0;
        r23 = -1;
        r0 = r23;
        r12.height = r0;
        r0 = r22;
        r12.width = r0;
        r0 = r30;
        r0 = r0.currentMessageNum;
        r23 = r0;
        r0 = r31;
        r1 = r23;
        if (r0 != r1) goto L_0x044f;
    L_0x01af:
        r23 = 0;
        r0 = r23;
        r12.leftMargin = r0;
    L_0x01b5:
        r0 = r21;
        r0.setLayoutParams(r12);
        r21.invalidate();
        goto L_0x002e;
    L_0x01bf:
        r23 = org.telegram.messenger.NotificationsController.getInstance();
        r0 = r23;
        r0 = r0.popupMessages;
        r23 = r0;
        r23 = r23.size();
        r0 = r31;
        r1 = r23;
        if (r0 != r1) goto L_0x0047;
    L_0x01d3:
        r31 = 0;
        goto L_0x0047;
    L_0x01d7:
        r21 = new org.telegram.ui.PopupNotificationActivity$FrameLayoutAnimationListener;
        r0 = r21;
        r1 = r30;
        r2 = r30;
        r0.<init>(r2);
        r23 = r30.getLayoutInflater();
        r24 = 2130903056; // 0x7f030010 float:1.741292E38 double:1.0528059946E-314;
        r25 = 0;
        r23 = r23.inflate(r24, r25);
        r0 = r21;
        r1 = r23;
        r0.addView(r1);
        r23 = 2;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r21;
        r1 = r23;
        r0.setTag(r1);
        r23 = new org.telegram.ui.PopupNotificationActivity$8;
        r0 = r23;
        r1 = r30;
        r0.<init>();
        r0 = r21;
        r1 = r23;
        r0.setOnClickListener(r1);
        goto L_0x0098;
    L_0x0215:
        if (r20 == 0) goto L_0x0128;
    L_0x0217:
        r0 = r20;
        r0 = r0.location;
        r24 = r0;
        r25 = 0;
        r23 = 0;
        r23 = (android.graphics.drawable.Drawable) r23;
        r0 = r24;
        r1 = r25;
        r2 = r23;
        r9.setImage(r0, r1, r2);
        r18 = 1;
        goto L_0x0128;
    L_0x0230:
        r23 = 0;
        r0 = r23;
        r9.setVisibility(r0);
        r23 = 8;
        r0 = r16;
        r1 = r23;
        r0.setVisibility(r1);
        goto L_0x015f;
    L_0x0242:
        r0 = r13.type;
        r23 = r0;
        r24 = 4;
        r0 = r23;
        r1 = r24;
        if (r0 != r1) goto L_0x015f;
    L_0x024e:
        r23 = 8;
        r0 = r16;
        r1 = r23;
        r0.setVisibility(r1);
        r0 = r13.messageText;
        r23 = r0;
        r0 = r16;
        r1 = r23;
        r0.setText(r1);
        r23 = 0;
        r0 = r23;
        r9.setVisibility(r0);
        r0 = r13.messageOwner;
        r23 = r0;
        r0 = r23;
        r0 = r0.media;
        r23 = r0;
        r0 = r23;
        r0 = r0.geo;
        r23 = r0;
        r0 = r23;
        r10 = r0.lat;
        r0 = r13.messageOwner;
        r23 = r0;
        r0 = r23;
        r0 = r0.media;
        r23 = r0;
        r0 = r23;
        r0 = r0.geo;
        r23 = r0;
        r0 = r23;
        r14 = r0._long;
        r23 = java.util.Locale.US;
        r24 = "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:big|%f,%f&sensor=false";
        r25 = 5;
        r0 = r25;
        r0 = new java.lang.Object[r0];
        r25 = r0;
        r26 = 0;
        r27 = java.lang.Double.valueOf(r10);
        r25[r26] = r27;
        r26 = 1;
        r27 = java.lang.Double.valueOf(r14);
        r25[r26] = r27;
        r26 = 2;
        r27 = 2;
        r28 = org.telegram.messenger.AndroidUtilities.density;
        r0 = r28;
        r0 = (double) r0;
        r28 = r0;
        r28 = java.lang.Math.ceil(r28);
        r0 = r28;
        r0 = (int) r0;
        r28 = r0;
        r27 = java.lang.Math.min(r27, r28);
        r27 = java.lang.Integer.valueOf(r27);
        r25[r26] = r27;
        r26 = 3;
        r27 = java.lang.Double.valueOf(r10);
        r25[r26] = r27;
        r26 = 4;
        r27 = java.lang.Double.valueOf(r14);
        r25[r26] = r27;
        r8 = java.lang.String.format(r23, r24, r25);
        r23 = 0;
        r24 = 0;
        r0 = r23;
        r1 = r24;
        r9.setImage(r8, r0, r1);
        goto L_0x015f;
    L_0x02ec:
        r0 = r13.type;
        r23 = r0;
        r24 = 2;
        r0 = r23;
        r1 = r24;
        if (r0 != r1) goto L_0x03a0;
    L_0x02f8:
        r0 = r30;
        r0 = r0.audioViews;
        r23 = r0;
        r23 = r23.size();
        if (r23 <= 0) goto L_0x0341;
    L_0x0304:
        r0 = r30;
        r0 = r0.audioViews;
        r23 = r0;
        r24 = 0;
        r21 = r23.get(r24);
        r21 = (android.view.ViewGroup) r21;
        r0 = r30;
        r0 = r0.audioViews;
        r23 = r0;
        r24 = 0;
        r23.remove(r24);
        r23 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r21;
        r1 = r23;
        r6 = r0.findViewWithTag(r1);
        r6 = (org.telegram.ui.Components.PopupAudioView) r6;
    L_0x032d:
        r6.setMessageObject(r13);
        r23 = org.telegram.messenger.MediaController.getInstance();
        r24 = 2;
        r23 = r23.canDownloadMedia(r24);
        if (r23 == 0) goto L_0x015f;
    L_0x033c:
        r6.downloadAudioIfNeed();
        goto L_0x015f;
    L_0x0341:
        r21 = new org.telegram.ui.PopupNotificationActivity$FrameLayoutAnimationListener;
        r0 = r21;
        r1 = r30;
        r2 = r30;
        r0.<init>(r2);
        r23 = r30.getLayoutInflater();
        r24 = 2130903054; // 0x7f03000e float:1.7412915E38 double:1.0528059936E-314;
        r25 = 0;
        r23 = r23.inflate(r24, r25);
        r0 = r21;
        r1 = r23;
        r0.addView(r1);
        r23 = 3;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r21;
        r1 = r23;
        r0.setTag(r1);
        r23 = new org.telegram.ui.PopupNotificationActivity$9;
        r0 = r23;
        r1 = r30;
        r0.<init>();
        r0 = r21;
        r1 = r23;
        r0.setOnClickListener(r1);
        r23 = 2131558461; // 0x7f0d003d float:1.8742238E38 double:1.0531298077E-314;
        r0 = r21;
        r1 = r23;
        r4 = r0.findViewById(r1);
        r4 = (android.view.ViewGroup) r4;
        r6 = new org.telegram.ui.Components.PopupAudioView;
        r0 = r30;
        r6.<init>(r0);
        r23 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r23;
        r6.setTag(r0);
        r4.addView(r6);
        goto L_0x032d;
    L_0x03a0:
        r0 = r30;
        r0 = r0.textViews;
        r23 = r0;
        r23 = r23.size();
        if (r23 <= 0) goto L_0x0406;
    L_0x03ac:
        r0 = r30;
        r0 = r0.textViews;
        r23 = r0;
        r24 = 0;
        r21 = r23.get(r24);
        r21 = (android.view.ViewGroup) r21;
        r0 = r30;
        r0 = r0.textViews;
        r23 = r0;
        r24 = 0;
        r23.remove(r24);
    L_0x03c5:
        r23 = 2131558450; // 0x7f0d0032 float:1.8742216E38 double:1.0531298022E-314;
        r0 = r21;
        r1 = r23;
        r16 = r0.findViewById(r1);
        r16 = (android.widget.TextView) r16;
        r23 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r16;
        r1 = r23;
        r0.setTag(r1);
        r23 = 2;
        r24 = org.telegram.messenger.MessagesController.getInstance();
        r0 = r24;
        r0 = r0.fontSize;
        r24 = r0;
        r0 = r24;
        r0 = (float) r0;
        r24 = r0;
        r0 = r16;
        r1 = r23;
        r2 = r24;
        r0.setTextSize(r1, r2);
        r0 = r13.messageText;
        r23 = r0;
        r0 = r16;
        r1 = r23;
        r0.setText(r1);
        goto L_0x015f;
    L_0x0406:
        r21 = new org.telegram.ui.PopupNotificationActivity$FrameLayoutAnimationListener;
        r0 = r21;
        r1 = r30;
        r2 = r30;
        r0.<init>(r2);
        r23 = r30.getLayoutInflater();
        r24 = 2130903057; // 0x7f030011 float:1.7412921E38 double:1.052805995E-314;
        r25 = 0;
        r23 = r23.inflate(r24, r25);
        r0 = r21;
        r1 = r23;
        r0.addView(r1);
        r23 = 1;
        r23 = java.lang.Integer.valueOf(r23);
        r0 = r21;
        r1 = r23;
        r0.setTag(r1);
        r23 = 2131558460; // 0x7f0d003c float:1.8742236E38 double:1.053129807E-314;
        r0 = r21;
        r1 = r23;
        r19 = r0.findViewById(r1);
        r23 = new org.telegram.ui.PopupNotificationActivity$10;
        r0 = r23;
        r1 = r30;
        r0.<init>();
        r0 = r19;
        r1 = r23;
        r0.setOnClickListener(r1);
        goto L_0x03c5;
    L_0x044f:
        r0 = r30;
        r0 = r0.currentMessageNum;
        r23 = r0;
        r23 = r23 + -1;
        r0 = r31;
        r1 = r23;
        if (r0 != r1) goto L_0x0468;
    L_0x045d:
        r0 = r22;
        r0 = -r0;
        r23 = r0;
        r0 = r23;
        r12.leftMargin = r0;
        goto L_0x01b5;
    L_0x0468:
        r0 = r30;
        r0 = r0.currentMessageNum;
        r23 = r0;
        r23 = r23 + 1;
        r0 = r31;
        r1 = r23;
        if (r0 != r1) goto L_0x01b5;
    L_0x0476:
        r0 = r22;
        r12.leftMargin = r0;
        goto L_0x01b5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.getViewForMessage(int, boolean):android.view.ViewGroup");
    }

    private void reuseView(ViewGroup view) {
        if (view != null) {
            int tag = ((Integer) view.getTag()).intValue();
            view.setVisibility(8);
            if (tag == 1) {
                this.textViews.add(view);
            } else if (tag == 2) {
                this.imageViews.add(view);
            } else if (tag == 3) {
                this.audioViews.add(view);
            }
        }
    }

    private void prepareLayouts(int move) {
        if (move == 0) {
            reuseView(this.centerView);
            reuseView(this.leftView);
            reuseView(this.rightView);
            for (int a = this.currentMessageNum - 1; a < this.currentMessageNum + 2; a++) {
                if (a == this.currentMessageNum - 1) {
                    this.leftView = getViewForMessage(a, true);
                } else if (a == this.currentMessageNum) {
                    this.centerView = getViewForMessage(a, true);
                } else if (a == this.currentMessageNum + 1) {
                    this.rightView = getViewForMessage(a, true);
                }
            }
        } else if (move == 1) {
            reuseView(this.rightView);
            this.rightView = this.centerView;
            this.centerView = this.leftView;
            this.leftView = getViewForMessage(this.currentMessageNum - 1, true);
        } else if (move == 2) {
            reuseView(this.leftView);
            this.leftView = this.centerView;
            this.centerView = this.rightView;
            this.rightView = getViewForMessage(this.currentMessageNum + 1, true);
        } else if (move == 3) {
            if (this.rightView != null) {
                offset = ((LayoutParams) this.rightView.getLayoutParams()).leftMargin;
                reuseView(this.rightView);
                r4 = getViewForMessage(this.currentMessageNum + 1, false);
                this.rightView = r4;
                if (r4 != null) {
                    widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                    layoutParams = (LayoutParams) this.rightView.getLayoutParams();
                    layoutParams.gravity = 51;
                    layoutParams.height = -1;
                    layoutParams.width = widht;
                    layoutParams.leftMargin = offset;
                    this.rightView.setLayoutParams(layoutParams);
                    this.rightView.invalidate();
                }
            }
        } else if (move == 4 && this.leftView != null) {
            offset = ((LayoutParams) this.leftView.getLayoutParams()).leftMargin;
            reuseView(this.leftView);
            r4 = getViewForMessage(0, false);
            this.leftView = r4;
            if (r4 != null) {
                widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                layoutParams = (LayoutParams) this.leftView.getLayoutParams();
                layoutParams.gravity = 51;
                layoutParams.height = -1;
                layoutParams.width = widht;
                layoutParams.leftMargin = offset;
                this.leftView.setLayoutParams(layoutParams);
                this.leftView.invalidate();
            }
        }
    }

    private void fixLayout() {
        if (this.avatarContainer != null) {
            this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (PopupNotificationActivity.this.avatarContainer != null) {
                        PopupNotificationActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    int padding = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f)) / 2;
                    PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), padding, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), padding);
                    return true;
                }
            });
        }
        if (this.messageContainer != null) {
            this.messageContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (!(PopupNotificationActivity.this.checkTransitionAnimation() || PopupNotificationActivity.this.startedMoving)) {
                        MarginLayoutParams layoutParams = (MarginLayoutParams) PopupNotificationActivity.this.messageContainer.getLayoutParams();
                        layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
                        layoutParams.width = -1;
                        layoutParams.height = -1;
                        PopupNotificationActivity.this.messageContainer.setLayoutParams(layoutParams);
                        PopupNotificationActivity.this.applyViewsLayoutParams(0);
                    }
                    return true;
                }
            });
        }
    }

    private void handleIntent(Intent intent) {
        if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode() || !ApplicationLoader.isScreenOn) {
            getWindow().addFlags(2623490);
        } else {
            getWindow().addFlags(2623488);
            getWindow().clearFlags(2);
        }
        if (this.currentMessageObject == null) {
            this.currentMessageNum = 0;
        }
        getNewMessage();
    }

    private void getNewMessage() {
        if (NotificationsController.getInstance().popupMessages.isEmpty()) {
            onFinish();
            finish();
            return;
        }
        boolean found = false;
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            for (int a = 0; a < NotificationsController.getInstance().popupMessages.size(); a++) {
                if (((MessageObject) NotificationsController.getInstance().popupMessages.get(a)).getId() == this.currentMessageObject.getId()) {
                    this.currentMessageNum = a;
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            this.currentMessageNum = 0;
            this.currentMessageObject = (MessageObject) NotificationsController.getInstance().popupMessages.get(0);
            updateInterfaceForCurrentMessage(0);
        } else if (this.startedMoving) {
            if (this.currentMessageNum == NotificationsController.getInstance().popupMessages.size() - 1) {
                prepareLayouts(3);
            } else if (this.currentMessageNum == 1) {
                prepareLayouts(4);
            }
        }
        this.countText.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(NotificationsController.getInstance().popupMessages.size())}));
    }

    private void openCurrentMessage() {
        if (this.currentMessageObject != null) {
            Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            long dialog_id = this.currentMessageObject.getDialogId();
            if (((int) dialog_id) != 0) {
                int lower_id = (int) dialog_id;
                if (lower_id < 0) {
                    intent.putExtra("chatId", -lower_id);
                } else {
                    intent.putExtra("userId", lower_id);
                }
            } else {
                intent.putExtra("encId", (int) (dialog_id >> 32));
            }
            intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
            intent.setFlags(TLRPC.MESSAGE_FLAG_EDITED);
            startActivity(intent);
            onFinish();
            finish();
        }
    }

    private void updateInterfaceForCurrentMessage(int move) {
        if (this.actionBar != null) {
            this.currentChat = null;
            this.currentUser = null;
            long dialog_id = this.currentMessageObject.getDialogId();
            this.chatActivityEnterView.setDialogId(dialog_id);
            if (((int) dialog_id) != 0) {
                int lower_id = (int) dialog_id;
                if (lower_id > 0) {
                    this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
                } else {
                    this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                    this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
                }
            } else {
                this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (dialog_id >> 32))).user_id));
            }
            if (this.currentChat != null && this.currentUser != null) {
                this.nameTextView.setText(this.currentChat.title);
                this.onlineTextView.setText(UserObject.getUserName(this.currentUser));
                this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                this.nameTextView.setCompoundDrawablePadding(0);
            } else if (this.currentUser != null) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
                if (((int) dialog_id) == 0) {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(C0691R.drawable.ic_lock_white, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                } else {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(0);
                }
            }
            prepareLayouts(move);
            updateSubtitle();
            checkAndUpdateAvatar();
            applyViewsLayoutParams(0);
        }
    }

    private void updateSubtitle() {
        if (this.actionBar != null && this.currentChat == null && this.currentUser != null) {
            if (this.currentUser.id / 1000 == 777 || this.currentUser.id / 1000 == 333 || ContactsController.getInstance().contactsDict.get(this.currentUser.id) != null || (ContactsController.getInstance().contactsDict.size() == 0 && ContactsController.getInstance().isLoadingContacts())) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else if (this.currentUser.phone == null || this.currentUser.phone.length() == 0) {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            } else {
                this.nameTextView.setText(PhoneFormat.getInstance().format("+" + this.currentUser.phone));
            }
            CharSequence printString = (CharSequence) MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentMessageObject.getDialogId()));
            if (printString == null || printString.length() == 0) {
                this.lastPrintString = null;
                setTypingAnimation(false);
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
                if (user != null) {
                    this.currentUser = user;
                }
                this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentUser));
                return;
            }
            this.lastPrintString = printString;
            this.onlineTextView.setText(printString);
            setTypingAnimation(true);
        }
    }

    private void checkAndUpdateAvatar() {
        TLObject newPhoto = null;
        Drawable avatarDrawable = null;
        if (this.currentChat != null) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
            if (chat != null) {
                this.currentChat = chat;
                if (this.currentChat.photo != null) {
                    newPhoto = this.currentChat.photo.photo_small;
                }
                avatarDrawable = new AvatarDrawable(this.currentChat);
            } else {
                return;
            }
        } else if (this.currentUser != null) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
            if (user != null) {
                this.currentUser = user;
                if (this.currentUser.photo != null) {
                    newPhoto = this.currentUser.photo.photo_small;
                }
                avatarDrawable = new AvatarDrawable(this.currentUser);
            } else {
                return;
            }
        }
        if (this.avatarImageView != null) {
            this.avatarImageView.setImage(newPhoto, "50_50", avatarDrawable);
        }
    }

    private void setTypingAnimation(boolean start) {
        if (this.actionBar != null) {
            if (start) {
                try {
                    Integer type = (Integer) MessagesController.getInstance().printingStringsTypes.get(Long.valueOf(this.currentMessageObject.getDialogId()));
                    if (type.intValue() == 0) {
                        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.typingDotsDrawable, null, null, null);
                        this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                        this.typingDotsDrawable.start();
                        this.recordStatusDrawable.stop();
                        return;
                    } else if (type.intValue() == 1) {
                        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.recordStatusDrawable, null, null, null);
                        this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                        this.recordStatusDrawable.start();
                        this.typingDotsDrawable.stop();
                        return;
                    } else {
                        return;
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                    return;
                }
            }
            this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            this.onlineTextView.setCompoundDrawablePadding(0);
            this.typingDotsDrawable.stop();
            this.recordStatusDrawable.stop();
        }
    }

    public void onBackPressed() {
        if (this.chatActivityEnterView.isPopupShowing()) {
            this.chatActivityEnterView.hidePopup(true);
        } else {
            super.onBackPressed();
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.setFieldFocused(true);
        }
        ConnectionsManager.getInstance().setAppPaused(false, false);
        fixLayout();
        checkAndUpdateAvatar();
        this.wakeLock.acquire(7000);
    }

    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        if (this.chatActivityEnterView != null) {
            this.chatActivityEnterView.hidePopup(false);
            this.chatActivityEnterView.setFieldFocused(false);
        }
        ConnectionsManager.getInstance().setAppPaused(true, false);
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.appDidLogout) {
            onFinish();
            finish();
        } else if (id == NotificationCenter.pushMessagesUpdated) {
            getNewMessage();
        } else if (id == NotificationCenter.updateInterfaces) {
            if (this.currentMessageObject != null) {
                int updateMask = ((Integer) args[0]).intValue();
                if (!((updateMask & 1) == 0 && (updateMask & 4) == 0 && (updateMask & 16) == 0 && (updateMask & 32) == 0)) {
                    updateSubtitle();
                }
                if (!((updateMask & 2) == 0 && (updateMask & 8) == 0)) {
                    checkAndUpdateAvatar();
                }
                if ((updateMask & 64) != 0) {
                    CharSequence printString = (CharSequence) MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentMessageObject.getDialogId()));
                    if ((this.lastPrintString != null && printString == null) || ((this.lastPrintString == null && printString != null) || (this.lastPrintString != null && printString != null && !this.lastPrintString.equals(printString)))) {
                        updateSubtitle();
                    }
                }
            }
        } else if (id == NotificationCenter.audioDidReset) {
            mid = args[0];
            if (this.messageContainer != null) {
                count = this.messageContainer.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.messageContainer.getChildAt(a);
                    if (((Integer) view.getTag()).intValue() == 3) {
                        cell = (PopupAudioView) view.findViewWithTag(Integer.valueOf(300));
                        if (cell.getMessageObject() != null && cell.getMessageObject().getId() == mid.intValue()) {
                            cell.updateButtonState();
                            return;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.audioProgressDidChanged) {
            mid = (Integer) args[0];
            if (this.messageContainer != null) {
                count = this.messageContainer.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.messageContainer.getChildAt(a);
                    if (((Integer) view.getTag()).intValue() == 3) {
                        cell = (PopupAudioView) view.findViewWithTag(Integer.valueOf(300));
                        if (cell.getMessageObject() != null && cell.getMessageObject().getId() == mid.intValue()) {
                            cell.updateProgress();
                            return;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
            if (this.messageContainer != null) {
                count = this.messageContainer.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.messageContainer.getChildAt(a);
                    if (((Integer) view.getTag()).intValue() == 1) {
                        TextView textView = (TextView) view.findViewWithTag(Integer.valueOf(301));
                        if (textView != null) {
                            textView.invalidate();
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.contactsDidLoaded) {
            updateSubtitle();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        onFinish();
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        if (this.avatarImageView != null) {
            this.avatarImageView.setImageDrawable(null);
        }
    }

    protected void onFinish() {
        if (!this.finished) {
            this.finished = true;
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            if (this.chatActivityEnterView != null) {
                this.chatActivityEnterView.onDestroy();
            }
            if (this.wakeLock.isHeld()) {
                this.wakeLock.release();
            }
        }
    }
}
