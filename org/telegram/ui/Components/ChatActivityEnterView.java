package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.messenger.query.MessagesQuery;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_keyboardButton;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestPhone;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotKeyboardView.BotKeyboardViewDelegate;
import org.telegram.ui.Components.EmojiView.Listener;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate;
import org.telegram.ui.Components.StickersAlert.StickersAlertDelegate;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.StickersActivity;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ChatActivityEnterView extends FrameLayout implements NotificationCenterDelegate, SizeNotifierFrameLayoutDelegate, StickersAlertDelegate {
    private boolean allowGifs;
    private boolean allowShowTopView;
    private boolean allowStickers;
    private LinearLayout attachButton;
    private int audioInterfaceState;
    private ImageView audioSendButton;
    private TL_document audioToSend;
    private MessageObject audioToSendMessageObject;
    private String audioToSendPath;
    private Drawable backgroundDrawable;
    private ImageView botButton;
    private MessageObject botButtonsMessageObject;
    private int botCount;
    private PopupWindow botKeyboardPopup;
    private BotKeyboardView botKeyboardView;
    private MessageObject botMessageObject;
    private TL_replyKeyboardMarkup botReplyMarkup;
    private boolean canWriteToChannel;
    private ImageView cancelBotButton;
    private int currentPopupContentType;
    private AnimatorSet currentTopViewAnimation;
    private ChatActivityEnterViewDelegate delegate;
    private long dialog_id;
    private float distCanMove;
    private boolean editingCaption;
    private MessageObject editingMessageObject;
    private int editingMessageReqId;
    private ImageView emojiButton;
    private int emojiPadding;
    private EmojiView emojiView;
    private boolean forceShowSendButton;
    private boolean hasBotCommands;
    private boolean ignoreTextChange;
    private int innerTextChange;
    private boolean isPaused;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private String lastTimeString;
    private long lastTypingTimeSend;
    private WakeLock mWakeLock;
    private EditTextCaption messageEditText;
    private WebPage messageWebPage;
    private boolean messageWebPageSearch;
    private boolean needShowTopView;
    private ImageView notifyButton;
    private Runnable openKeyboardRunnable;
    private Activity parentActivity;
    private ChatActivity parentFragment;
    private KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    private CloseProgressDrawable2 progressDrawable;
    private RecordCircle recordCircle;
    private RecordDot recordDot;
    private FrameLayout recordPanel;
    private TextView recordTimeText;
    private FrameLayout recordedAudioPanel;
    private ImageView recordedAudioPlayButton;
    private SeekBarWaveformView recordedAudioSeekBar;
    private TextView recordedAudioTimeTextView;
    private boolean recordingAudio;
    private MessageObject replyingMessageObject;
    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private AnimatorSet runningAnimationAudio;
    private int runningAnimationType;
    private ImageView sendButton;
    private FrameLayout sendButtonContainer;
    private boolean sendByEnter;
    private boolean showKeyboardOnResume;
    private boolean silent;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private LinearLayout slideText;
    private float startedDraggingX;
    private LinearLayout textFieldContainer;
    private View topView;
    private boolean topViewShowed;
    private boolean waitingForKeyboardOpen;

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.1 */
    class C11091 implements Runnable {
        C11091() {
        }

        public void run() {
            if (ChatActivityEnterView.this.messageEditText != null && ChatActivityEnterView.this.waitingForKeyboardOpen && !ChatActivityEnterView.this.keyboardVisible && !AndroidUtilities.usingHardwareInput) {
                ChatActivityEnterView.this.messageEditText.requestFocus();
                AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
                AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.24 */
    class AnonymousClass24 implements OnClickListener {
        final /* synthetic */ KeyboardButton val$button;
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass24(MessageObject messageObject, KeyboardButton keyboardButton) {
            this.val$messageObject = messageObject;
            this.val$button = keyboardButton;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                SendMessagesHelper.getInstance().sendCurrentLocation(this.val$messageObject, this.val$button);
                return;
            }
            ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            ChatActivityEnterView.this.pendingMessageObject = this.val$messageObject;
            ChatActivityEnterView.this.pendingLocationButton = this.val$button;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.2 */
    class C11112 implements View.OnClickListener {
        C11112() {
        }

        public void onClick(View view) {
            if (ChatActivityEnterView.this.isPopupShowing() && ChatActivityEnterView.this.currentPopupContentType == 0) {
                ChatActivityEnterView.this.openKeyboardInternal();
                ChatActivityEnterView.this.removeGifFromInputField();
                return;
            }
            ChatActivityEnterView.this.showPopup(1, 0);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.3 */
    class C11123 implements OnKeyListener {
        boolean ctrlPressed;

        C11123() {
            this.ctrlPressed = false;
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            boolean z = false;
            if (i == 4 && !ChatActivityEnterView.this.keyboardVisible && ChatActivityEnterView.this.isPopupShowing()) {
                if (keyEvent.getAction() != 1) {
                    return true;
                }
                if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                    ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                }
                ChatActivityEnterView.this.showPopup(0, 0);
                ChatActivityEnterView.this.removeGifFromInputField();
                return true;
            } else if (i == 66 && ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0)) {
                ChatActivityEnterView.this.sendMessage();
                return true;
            } else if (i != 113 && i != 114) {
                return false;
            } else {
                if (keyEvent.getAction() == 0) {
                    z = true;
                }
                this.ctrlPressed = z;
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.4 */
    class C11134 implements OnEditorActionListener {
        boolean ctrlPressed;

        C11134() {
            this.ctrlPressed = false;
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            boolean z = false;
            if (i == 4) {
                ChatActivityEnterView.this.sendMessage();
                return true;
            }
            if (keyEvent != null && i == 0) {
                if ((this.ctrlPressed || ChatActivityEnterView.this.sendByEnter) && keyEvent.getAction() == 0) {
                    ChatActivityEnterView.this.sendMessage();
                    return true;
                } else if (i == 113 || i == 114) {
                    if (keyEvent.getAction() == 0) {
                        z = true;
                    }
                    this.ctrlPressed = z;
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.5 */
    class C11145 implements TextWatcher {
        boolean processChange;

        C11145() {
            this.processChange = false;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if (ChatActivityEnterView.this.innerTextChange != 1) {
                ChatActivityEnterView.this.checkSendButton(true);
                CharSequence message = AndroidUtilities.getTrimmedString(charSequence.toString());
                if (ChatActivityEnterView.this.delegate != null) {
                    if (count > 2 || charSequence == null || charSequence.length() == 0) {
                        ChatActivityEnterView.this.messageWebPageSearch = true;
                    }
                    if (!ChatActivityEnterView.this.ignoreTextChange) {
                        ChatActivityEnterViewDelegate access$1500 = ChatActivityEnterView.this.delegate;
                        boolean z = before > count + 1 || count - before > 2;
                        access$1500.onTextChanged(charSequence, z);
                    }
                }
                if (!(ChatActivityEnterView.this.innerTextChange == 2 || before == count || count - before <= 1)) {
                    this.processChange = true;
                }
                if (ChatActivityEnterView.this.editingMessageObject == null && !ChatActivityEnterView.this.canWriteToChannel && message.length() != 0 && ChatActivityEnterView.this.lastTypingTimeSend < System.currentTimeMillis() - HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS && !ChatActivityEnterView.this.ignoreTextChange) {
                    int currentTime = ConnectionsManager.getInstance().getCurrentTime();
                    User currentUser = null;
                    if (((int) ChatActivityEnterView.this.dialog_id) > 0) {
                        currentUser = MessagesController.getInstance().getUser(Integer.valueOf((int) ChatActivityEnterView.this.dialog_id));
                    }
                    if (currentUser != null) {
                        if (currentUser.id == UserConfig.getClientUserId()) {
                            return;
                        }
                        if (!(currentUser.status == null || currentUser.status.expires >= currentTime || MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(currentUser.id)))) {
                            return;
                        }
                    }
                    ChatActivityEnterView.this.lastTypingTimeSend = System.currentTimeMillis();
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.needSendTyping();
                    }
                }
            }
        }

        public void afterTextChanged(Editable editable) {
            if (ChatActivityEnterView.this.innerTextChange == 0) {
                if (ChatActivityEnterView.this.sendByEnter && editable.length() > 0 && editable.charAt(editable.length() - 1) == '\n') {
                    ChatActivityEnterView.this.sendMessage();
                }
                if (this.processChange) {
                    ImageSpan[] spans = (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class);
                    for (Object removeSpan : spans) {
                        editable.removeSpan(removeSpan);
                    }
                    Emoji.replaceEmoji(editable, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    this.processChange = false;
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.6 */
    class C11156 implements View.OnClickListener {
        C11156() {
        }

        public void onClick(View v) {
            if (ChatActivityEnterView.this.botReplyMarkup != null) {
                if (ChatActivityEnterView.this.isPopupShowing() && ChatActivityEnterView.this.currentPopupContentType == 1) {
                    if (ChatActivityEnterView.this.currentPopupContentType == 1 && ChatActivityEnterView.this.botButtonsMessageObject != null) {
                        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                    }
                    ChatActivityEnterView.this.openKeyboardInternal();
                    return;
                }
                ChatActivityEnterView.this.showPopup(1, 1);
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("hidekeyboard_" + ChatActivityEnterView.this.dialog_id).commit();
            } else if (ChatActivityEnterView.this.hasBotCommands) {
                ChatActivityEnterView.this.setFieldText("/");
                ChatActivityEnterView.this.openKeyboard();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.7 */
    class C11167 implements View.OnClickListener {
        C11167() {
        }

        public void onClick(View v) {
            ChatActivityEnterView.this.silent = !ChatActivityEnterView.this.silent;
            ChatActivityEnterView.this.notifyButton.setImageResource(ChatActivityEnterView.this.silent ? C0691R.drawable.notify_members_off : C0691R.drawable.notify_members_on);
            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("silent_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.silent).commit();
            NotificationsController.updateServerNotificationsSettings(ChatActivityEnterView.this.dialog_id);
            if (ChatActivityEnterView.this.silent) {
                Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOff", C0691R.string.ChannelNotifyMembersInfoOff), 0).show();
            } else {
                Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOn", C0691R.string.ChannelNotifyMembersInfoOn), 0).show();
            }
            ChatActivityEnterView.this.updateFieldHint();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.8 */
    class C11178 implements View.OnClickListener {
        C11178() {
        }

        public void onClick(View v) {
            MessageObject playing = MediaController.getInstance().getPlayingMessageObject();
            if (playing != null && playing == ChatActivityEnterView.this.audioToSendMessageObject) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            if (ChatActivityEnterView.this.audioToSendPath != null) {
                new File(ChatActivityEnterView.this.audioToSendPath).delete();
            }
            ChatActivityEnterView.this.hideRecordedAudioPanel();
            ChatActivityEnterView.this.checkSendButton(true);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.9 */
    class C11189 implements View.OnClickListener {
        C11189() {
        }

        public void onClick(View v) {
            if (ChatActivityEnterView.this.audioToSend != null) {
                if (!MediaController.getInstance().isPlayingAudio(ChatActivityEnterView.this.audioToSendMessageObject) || MediaController.getInstance().isAudioPaused()) {
                    ChatActivityEnterView.this.recordedAudioPlayButton.setImageResource(C0691R.drawable.s_player_pause_states);
                    MediaController.getInstance().playAudio(ChatActivityEnterView.this.audioToSendMessageObject);
                    return;
                }
                MediaController.getInstance().pauseAudio(ChatActivityEnterView.this.audioToSendMessageObject);
                ChatActivityEnterView.this.recordedAudioPlayButton.setImageResource(C0691R.drawable.s_player_play_states);
            }
        }
    }

    public interface ChatActivityEnterViewDelegate {
        void needSendTyping();

        void onAttachButtonHidden();

        void onAttachButtonShow();

        void onMessageEditEnd(boolean z);

        void onMessageSend(CharSequence charSequence);

        void onStickersTab(boolean z);

        void onTextChanged(CharSequence charSequence, boolean z);

        void onWindowSizeChanged(int i);
    }

    private class EditTextCaption extends EditText {
        private String caption;
        private StaticLayout captionLayout;
        private Object editor;
        private Field editorField;
        private Drawable[] mCursorDrawable;
        private Field mCursorDrawableField;
        private int triesCount;
        private int userNameLength;
        private int xOffset;
        private int yOffset;

        public EditTextCaption(Context context) {
            super(context);
            this.triesCount = 0;
            try {
                Field field = TextView.class.getDeclaredField("mEditor");
                field.setAccessible(true);
                this.editor = field.get(this);
                Class editorClass = Class.forName("android.widget.Editor");
                this.editorField = editorClass.getDeclaredField("mShowCursor");
                this.editorField.setAccessible(true);
                this.mCursorDrawableField = editorClass.getDeclaredField("mCursorDrawable");
                this.mCursorDrawableField.setAccessible(true);
                this.mCursorDrawable = (Drawable[]) this.mCursorDrawableField.get(this.editor);
            } catch (Throwable th) {
            }
        }

        public void setCaption(String value) {
            if ((this.caption != null && this.caption.length() != 0) || (value != null && value.length() != 0)) {
                if (this.caption == null || value == null || !this.caption.equals(value)) {
                    this.caption = value;
                    if (this.caption != null) {
                        this.caption = this.caption.replace('\n', ' ');
                    }
                    requestLayout();
                }
            }
        }

        @SuppressLint({"DrawAllocation"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            try {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } catch (Throwable e) {
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(51.0f));
                FileLog.m13e("tmessages", e);
            }
            this.captionLayout = null;
            if (this.caption != null && this.caption.length() > 0) {
                CharSequence text = getText();
                if (text.length() > 1 && text.charAt(0) == '@') {
                    int index = TextUtils.indexOf(text, ' ');
                    if (index != -1) {
                        TextPaint paint = getPaint();
                        int size = (int) Math.ceil((double) paint.measureText(text, 0, index + 1));
                        int width = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                        this.userNameLength = text.subSequence(0, index + 1).length();
                        CharSequence captionFinal = TextUtils.ellipsize(this.caption, paint, (float) (width - size), TruncateAt.END);
                        this.xOffset = size;
                        try {
                            this.captionLayout = new StaticLayout(captionFinal, getPaint(), width - size, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                            if (this.captionLayout.getLineCount() > 0) {
                                this.xOffset = (int) (((float) this.xOffset) + (-this.captionLayout.getLineLeft(0)));
                            }
                            this.yOffset = ((getMeasuredHeight() - this.captionLayout.getLineBottom(0)) / 2) + AndroidUtilities.dp(0.5f);
                        } catch (Throwable e2) {
                            FileLog.m13e("tmessages", e2);
                        }
                    }
                }
            }
        }

        protected void onDraw(Canvas canvas) {
            boolean showCursor = false;
            try {
                super.onDraw(canvas);
                if (this.captionLayout != null && this.userNameLength == length()) {
                    Paint paint = getPaint();
                    int oldColor = getPaint().getColor();
                    paint.setColor(-5066062);
                    canvas.save();
                    canvas.translate((float) this.xOffset, (float) this.yOffset);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                    paint.setColor(oldColor);
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            try {
                if (this.editorField != null && this.mCursorDrawable != null && this.mCursorDrawable[0] != null) {
                    if ((SystemClock.uptimeMillis() - this.editorField.getLong(this.editor)) % 1000 < 500) {
                        showCursor = true;
                    }
                    if (showCursor) {
                        canvas.save();
                        canvas.translate(0.0f, (float) getPaddingTop());
                        this.mCursorDrawable[0].draw(canvas);
                        canvas.restore();
                    }
                }
            } catch (Throwable th) {
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean z = false;
            if (ChatActivityEnterView.this.isPopupShowing() && event.getAction() == 0) {
                ChatActivityEnterView.this.showPopup(AndroidUtilities.usingHardwareInput ? z : 2, z);
                ChatActivityEnterView.this.openKeyboardInternal();
            }
            try {
                z = super.onTouchEvent(event);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            return z;
        }
    }

    private class RecordCircle extends View {
        private float amplitude;
        private float animateAmplitudeDiff;
        private float animateToAmplitude;
        private long lastUpdateTime;
        private Drawable micDrawable;
        private Paint paint;
        private Paint paintRecord;
        private float scale;

        public RecordCircle(Context context) {
            super(context);
            this.paint = new Paint(1);
            this.paintRecord = new Paint(1);
            this.paint.setColor(-11037236);
            this.paintRecord.setColor(218103808);
            this.micDrawable = getResources().getDrawable(C0691R.drawable.mic_pressed);
        }

        public void setAmplitude(double value) {
            this.animateToAmplitude = ((float) Math.min(100.0d, value)) / 100.0f;
            this.animateAmplitudeDiff = (this.animateToAmplitude - this.amplitude) / 150.0f;
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
        }

        public float getScale() {
            return this.scale;
        }

        public void setScale(float value) {
            this.scale = value;
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            float sc;
            float alpha;
            int cx = getMeasuredWidth() / 2;
            int cy = getMeasuredHeight() / 2;
            if (this.scale <= 0.5f) {
                sc = this.scale / 0.5f;
                alpha = sc;
            } else if (this.scale <= AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION) {
                sc = TouchHelperCallback.ALPHA_FULL - (((this.scale - 0.5f) / 0.25f) * 0.1f);
                alpha = TouchHelperCallback.ALPHA_FULL;
            } else {
                sc = 0.9f + (((this.scale - AdaptiveEvaluator.DEFAULT_BANDWIDTH_FRACTION) / 0.25f) * 0.1f);
                alpha = TouchHelperCallback.ALPHA_FULL;
            }
            long dt = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.animateToAmplitude != this.amplitude) {
                this.amplitude += this.animateAmplitudeDiff * ((float) dt);
                if (this.animateAmplitudeDiff > 0.0f) {
                    if (this.amplitude > this.animateToAmplitude) {
                        this.amplitude = this.animateToAmplitude;
                    }
                } else if (this.amplitude < this.animateToAmplitude) {
                    this.amplitude = this.animateToAmplitude;
                }
                invalidate();
            }
            this.lastUpdateTime = System.currentTimeMillis();
            if (this.amplitude != 0.0f) {
                canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, (((float) AndroidUtilities.dp(42.0f)) + (((float) AndroidUtilities.dp(20.0f)) * this.amplitude)) * this.scale, this.paintRecord);
            }
            canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) AndroidUtilities.dp(42.0f)) * sc, this.paint);
            this.micDrawable.setBounds(cx - (this.micDrawable.getIntrinsicWidth() / 2), cy - (this.micDrawable.getIntrinsicHeight() / 2), (this.micDrawable.getIntrinsicWidth() / 2) + cx, (this.micDrawable.getIntrinsicHeight() / 2) + cy);
            this.micDrawable.setAlpha((int) (255.0f * alpha));
            this.micDrawable.draw(canvas);
        }
    }

    private class RecordDot extends View {
        private float alpha;
        private Drawable dotDrawable;
        private boolean isIncr;
        private long lastUpdateTime;

        public RecordDot(Context context) {
            super(context);
            this.dotDrawable = getResources().getDrawable(C0691R.drawable.rec);
        }

        public void resetAlpha() {
            this.alpha = TouchHelperCallback.ALPHA_FULL;
            this.lastUpdateTime = System.currentTimeMillis();
            this.isIncr = false;
            invalidate();
        }

        protected void onDraw(Canvas canvas) {
            this.dotDrawable.setBounds(0, 0, AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f));
            this.dotDrawable.setAlpha((int) (255.0f * this.alpha));
            long dt = System.currentTimeMillis() - this.lastUpdateTime;
            if (this.isIncr) {
                this.alpha += ((float) dt) / 400.0f;
                if (this.alpha >= TouchHelperCallback.ALPHA_FULL) {
                    this.alpha = TouchHelperCallback.ALPHA_FULL;
                    this.isIncr = false;
                }
            } else {
                this.alpha -= ((float) dt) / 400.0f;
                if (this.alpha <= 0.0f) {
                    this.alpha = 0.0f;
                    this.isIncr = true;
                }
            }
            this.lastUpdateTime = System.currentTimeMillis();
            this.dotDrawable.draw(canvas);
            invalidate();
        }
    }

    private class SeekBarWaveformView extends View {
        private SeekBarWaveform seekBarWaveform;

        /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.SeekBarWaveformView.1 */
        class C18121 implements SeekBarDelegate {
            final /* synthetic */ ChatActivityEnterView val$this$0;

            C18121(ChatActivityEnterView chatActivityEnterView) {
                this.val$this$0 = chatActivityEnterView;
            }

            public void onSeekBarDrag(float progress) {
                if (ChatActivityEnterView.this.audioToSendMessageObject != null) {
                    ChatActivityEnterView.this.audioToSendMessageObject.audioProgress = progress;
                    MediaController.getInstance().seekToProgress(ChatActivityEnterView.this.audioToSendMessageObject, progress);
                }
            }
        }

        public SeekBarWaveformView(Context context) {
            super(context);
            this.seekBarWaveform = new SeekBarWaveform(context);
            this.seekBarWaveform.setColors(-6107400, -1, -6107400);
            this.seekBarWaveform.setDelegate(new C18121(ChatActivityEnterView.this));
        }

        public void setWaveform(byte[] waveform) {
            this.seekBarWaveform.setWaveform(waveform);
            invalidate();
        }

        public void setProgress(float progress) {
            this.seekBarWaveform.setProgress(progress);
            invalidate();
        }

        public boolean isDragging() {
            return this.seekBarWaveform.isDragging();
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean result = this.seekBarWaveform.onTouch(event.getAction(), event.getX(), event.getY());
            if (result) {
                if (event.getAction() == 0) {
                    ChatActivityEnterView.this.requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
            }
            if (result || super.onTouchEvent(event)) {
                return true;
            }
            return false;
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            this.seekBarWaveform.setSize(right - left, bottom - top);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            this.seekBarWaveform.draw(canvas);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.13 */
    class AnonymousClass13 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$openKeyboard;

        AnonymousClass13(boolean z) {
            this.val$openKeyboard = z;
        }

        public void onAnimationEnd(Animator animation) {
            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                if (ChatActivityEnterView.this.recordedAudioPanel.getVisibility() != 0 && (!ChatActivityEnterView.this.forceShowSendButton || this.val$openKeyboard)) {
                    ChatActivityEnterView.this.openKeyboard();
                }
                ChatActivityEnterView.this.currentTopViewAnimation = null;
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                ChatActivityEnterView.this.currentTopViewAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.25 */
    class AnonymousClass25 implements DialogsActivityDelegate {
        final /* synthetic */ KeyboardButton val$button;
        final /* synthetic */ MessageObject val$messageObject;

        AnonymousClass25(MessageObject messageObject, KeyboardButton keyboardButton) {
            this.val$messageObject = messageObject;
            this.val$button = keyboardButton;
        }

        public void didSelectDialog(DialogsActivity fragment, long did, boolean param) {
            int uid = this.val$messageObject.messageOwner.from_id;
            if (this.val$messageObject.messageOwner.via_bot_id != 0) {
                uid = this.val$messageObject.messageOwner.via_bot_id;
            }
            User user = MessagesController.getInstance().getUser(Integer.valueOf(uid));
            if (user == null) {
                fragment.finishFragment();
                return;
            }
            DraftQuery.saveDraft(did, "@" + user.username + " " + this.val$button.query, null, null, true);
            if (did != ChatActivityEnterView.this.dialog_id) {
                int lower_part = (int) did;
                if (lower_part != 0) {
                    Bundle args = new Bundle();
                    if (lower_part > 0) {
                        args.putInt("user_id", lower_part);
                    } else if (lower_part < 0) {
                        args.putInt("chat_id", -lower_part);
                    }
                    if (MessagesController.checkCanOpenChat(args, fragment)) {
                        if (!ChatActivityEnterView.this.parentFragment.presentFragment(new ChatActivity(args), true)) {
                            fragment.finishFragment();
                            return;
                        } else if (!AndroidUtilities.isTablet()) {
                            ChatActivityEnterView.this.parentFragment.removeSelfFromStack();
                            return;
                        } else {
                            return;
                        }
                    }
                    return;
                }
                fragment.finishFragment();
                return;
            }
            fragment.finishFragment();
        }
    }

    public ChatActivityEnterView(Activity context, SizeNotifierFrameLayout parent, ChatActivity fragment, boolean isChat) {
        super(context);
        this.currentPopupContentType = -1;
        this.isPaused = true;
        this.startedDraggingX = GroundOverlayOptions.NO_DIMENSION;
        this.distCanMove = (float) AndroidUtilities.dp(80.0f);
        this.messageWebPageSearch = true;
        this.openKeyboardRunnable = new C11091();
        this.backgroundDrawable = context.getResources().getDrawable(C0691R.drawable.compose_panel);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStartError);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStopped);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioRouteChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        this.parentActivity = context;
        this.parentFragment = fragment;
        this.sizeNotifierLayout = parent;
        this.sizeNotifierLayout.setDelegate(this);
        this.sendByEnter = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("send_by_enter", false);
        this.textFieldContainer = new LinearLayout(context);
        this.textFieldContainer.setBackgroundColor(getResources().getColor(C0691R.color.message_bar_mic_background));
        this.textFieldContainer.setOrientation(0);
        addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 2.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        this.textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, -2, (float) TouchHelperCallback.ALPHA_FULL));
        this.emojiButton = new ImageView(context);
        this.emojiButton.setImageResource(C0691R.drawable.ic_msg_panel_smiles);
        this.emojiButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.emojiButton.setPadding(0, AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL), 0, 0);
        if (VERSION.SDK_INT >= 21) {
            this.emojiButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
        }
        frameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48.0f, 83, 3.0f, 0.0f, 0.0f, 0.0f));
        this.emojiButton.setOnClickListener(new C11112());
        this.messageEditText = new EditTextCaption(context);
        updateFieldHint();
        this.messageEditText.setImeOptions(268435456);
        this.messageEditText.setInputType((this.messageEditText.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS) | AccessibilityNodeInfoCompat.ACTION_SET_SELECTION);
        this.messageEditText.setSingleLine(false);
        this.messageEditText.setMaxLines(4);
        this.messageEditText.setTextSize(1, 18.0f);
        this.messageEditText.setGravity(80);
        this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
        this.messageEditText.setBackgroundDrawable(null);
        this.messageEditText.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        this.messageEditText.setHintTextColor(-5066062);
        frameLayout.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0f, 80, 52.0f, 0.0f, isChat ? 50.0f : 2.0f, 0.0f));
        this.messageEditText.setOnKeyListener(new C11123());
        this.messageEditText.setOnEditorActionListener(new C11134());
        this.messageEditText.addTextChangedListener(new C11145());
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(this.messageEditText, Integer.valueOf(C0691R.drawable.field_carret));
        } catch (Exception e) {
        }
        if (isChat) {
            this.attachButton = new LinearLayout(context);
            this.attachButton.setOrientation(0);
            this.attachButton.setEnabled(false);
            this.attachButton.setPivotX((float) AndroidUtilities.dp(48.0f));
            frameLayout.addView(this.attachButton, LayoutHelper.createFrame(-2, 48, 85));
            this.botButton = new ImageView(context);
            this.botButton.setImageResource(C0691R.drawable.bot_keyboard2);
            this.botButton.setScaleType(ScaleType.CENTER);
            this.botButton.setVisibility(8);
            if (VERSION.SDK_INT >= 21) {
                this.botButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
            }
            this.attachButton.addView(this.botButton, LayoutHelper.createLinear(48, 48));
            this.botButton.setOnClickListener(new C11156());
            this.notifyButton = new ImageView(context);
            this.notifyButton.setImageResource(this.silent ? C0691R.drawable.notify_members_off : C0691R.drawable.notify_members_on);
            this.notifyButton.setScaleType(ScaleType.CENTER);
            this.notifyButton.setVisibility(this.canWriteToChannel ? 0 : 8);
            if (VERSION.SDK_INT >= 21) {
                this.notifyButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
            }
            this.attachButton.addView(this.notifyButton, LayoutHelper.createLinear(48, 48));
            this.notifyButton.setOnClickListener(new C11167());
        }
        this.recordedAudioPanel = new FrameLayout(context);
        this.recordedAudioPanel.setVisibility(this.audioToSend == null ? 8 : 0);
        this.recordedAudioPanel.setBackgroundColor(-1);
        this.recordedAudioPanel.setFocusable(true);
        this.recordedAudioPanel.setFocusableInTouchMode(true);
        this.recordedAudioPanel.setClickable(true);
        frameLayout.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageResource(C0691R.drawable.ic_ab_fwd_delete);
        this.recordedAudioPanel.addView(imageView, LayoutHelper.createFrame(48, 48.0f));
        imageView.setOnClickListener(new C11178());
        View view = new View(context);
        view.setBackgroundResource(C0691R.drawable.recorded);
        this.recordedAudioPanel.addView(view, LayoutHelper.createFrame(-1, 32.0f, 19, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioSeekBar = new SeekBarWaveformView(context);
        this.recordedAudioPanel.addView(this.recordedAudioSeekBar, LayoutHelper.createFrame(-1, 32.0f, 19, 92.0f, 0.0f, 52.0f, 0.0f));
        this.recordedAudioPlayButton = new ImageView(context);
        this.recordedAudioPlayButton.setImageResource(C0691R.drawable.s_player_play_states);
        this.recordedAudioPlayButton.setScaleType(ScaleType.CENTER);
        this.recordedAudioPanel.addView(this.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0f, 83, 48.0f, 0.0f, 0.0f, 0.0f));
        this.recordedAudioPlayButton.setOnClickListener(new C11189());
        this.recordedAudioTimeTextView = new TextView(context);
        this.recordedAudioTimeTextView.setTextColor(-1);
        this.recordedAudioTimeTextView.setTextSize(1, 13.0f);
        this.recordedAudioTimeTextView.setText("0:13");
        this.recordedAudioPanel.addView(this.recordedAudioTimeTextView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 13.0f, 0.0f));
        this.recordPanel = new FrameLayout(context);
        this.recordPanel.setVisibility(8);
        this.recordPanel.setBackgroundColor(-1);
        frameLayout.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48, 80));
        this.slideText = new LinearLayout(context);
        this.slideText.setOrientation(0);
        this.recordPanel.addView(this.slideText, LayoutHelper.createFrame(-2, -2.0f, 17, BitmapDescriptorFactory.HUE_ORANGE, 0.0f, 0.0f, 0.0f));
        imageView = new ImageView(context);
        imageView.setImageResource(C0691R.drawable.slidearrow);
        this.slideText.addView(imageView, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 0, 0));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString("SlideToCancel", C0691R.string.SlideToCancel));
        textView.setTextColor(-6710887);
        textView.setTextSize(1, 12.0f);
        this.slideText.addView(textView, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        linearLayout.setBackgroundColor(-1);
        this.recordPanel.addView(linearLayout, LayoutHelper.createFrame(-2, -2, 16));
        this.recordDot = new RecordDot(context);
        linearLayout.addView(this.recordDot, LayoutHelper.createLinear(11, 11, 16, 0, 1, 0, 0));
        this.recordTimeText = new TextView(context);
        this.recordTimeText.setText("00:00");
        this.recordTimeText.setTextColor(-11711413);
        this.recordTimeText.setTextSize(1, 16.0f);
        linearLayout.addView(this.recordTimeText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
        this.sendButtonContainer = new FrameLayout(context);
        this.textFieldContainer.addView(this.sendButtonContainer, LayoutHelper.createLinear(48, 48, 80));
        this.audioSendButton = new ImageView(context);
        this.audioSendButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.audioSendButton.setImageResource(C0691R.drawable.mic);
        this.audioSendButton.setBackgroundColor(getResources().getColor(C0691R.color.message_bar_mic_background));
        this.audioSendButton.setSoundEffectsEnabled(false);
        this.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.sendButtonContainer.addView(this.audioSendButton, LayoutHelper.createFrame(48, 48.0f));
        this.audioSendButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        if (VERSION.SDK_INT < 23 || ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                            String action;
                            if (((int) ChatActivityEnterView.this.dialog_id) < 0) {
                                Chat currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) ChatActivityEnterView.this.dialog_id)));
                                if (currentChat == null || currentChat.participants_count <= MessagesController.getInstance().groupBigSize) {
                                    action = "chat_upload_audio";
                                } else {
                                    action = "bigchat_upload_audio";
                                }
                            } else {
                                action = "pm_upload_audio";
                            }
                            if (!MessagesController.isFeatureEnabled(action, ChatActivityEnterView.this.parentFragment)) {
                                return false;
                            }
                        }
                        ChatActivityEnterView.this.parentActivity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 3);
                        return false;
                    }
                    ChatActivityEnterView.this.startedDraggingX = GroundOverlayOptions.NO_DIMENSION;
                    MediaController.getInstance().startRecording(ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
                    ChatActivityEnterView.this.updateAudioRecordIntefrace();
                    ChatActivityEnterView.this.audioSendButton.getParent().requestDisallowInterceptTouchEvent(true);
                } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    ChatActivityEnterView.this.startedDraggingX = GroundOverlayOptions.NO_DIMENSION;
                    MediaController.getInstance().stopRecording(1);
                    ChatActivityEnterView.this.recordingAudio = false;
                    ChatActivityEnterView.this.updateAudioRecordIntefrace();
                } else if (motionEvent.getAction() == 2 && ChatActivityEnterView.this.recordingAudio) {
                    float x = motionEvent.getX();
                    if (x < (-ChatActivityEnterView.this.distCanMove)) {
                        MediaController.getInstance().stopRecording(0);
                        ChatActivityEnterView.this.recordingAudio = false;
                        ChatActivityEnterView.this.updateAudioRecordIntefrace();
                    }
                    x += ChatActivityEnterView.this.audioSendButton.getX();
                    LayoutParams params = (LayoutParams) ChatActivityEnterView.this.slideText.getLayoutParams();
                    if (ChatActivityEnterView.this.startedDraggingX != GroundOverlayOptions.NO_DIMENSION) {
                        float dist = x - ChatActivityEnterView.this.startedDraggingX;
                        ChatActivityEnterView.this.recordCircle.setTranslationX(dist);
                        params.leftMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE) + ((int) dist);
                        ChatActivityEnterView.this.slideText.setLayoutParams(params);
                        float alpha = TouchHelperCallback.ALPHA_FULL + (dist / ChatActivityEnterView.this.distCanMove);
                        if (alpha > TouchHelperCallback.ALPHA_FULL) {
                            alpha = TouchHelperCallback.ALPHA_FULL;
                        } else if (alpha < 0.0f) {
                            alpha = 0.0f;
                        }
                        ChatActivityEnterView.this.slideText.setAlpha(alpha);
                    }
                    if (x <= (ChatActivityEnterView.this.slideText.getX() + ((float) ChatActivityEnterView.this.slideText.getWidth())) + ((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) && ChatActivityEnterView.this.startedDraggingX == GroundOverlayOptions.NO_DIMENSION) {
                        ChatActivityEnterView.this.startedDraggingX = x;
                        ChatActivityEnterView.this.distCanMove = ((float) ((ChatActivityEnterView.this.recordPanel.getMeasuredWidth() - ChatActivityEnterView.this.slideText.getMeasuredWidth()) - AndroidUtilities.dp(48.0f))) / 2.0f;
                        if (ChatActivityEnterView.this.distCanMove <= 0.0f) {
                            ChatActivityEnterView.this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                        } else if (ChatActivityEnterView.this.distCanMove > ((float) AndroidUtilities.dp(80.0f))) {
                            ChatActivityEnterView.this.distCanMove = (float) AndroidUtilities.dp(80.0f);
                        }
                    }
                    if (params.leftMargin > AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) {
                        params.leftMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
                        ChatActivityEnterView.this.recordCircle.setTranslationX(0.0f);
                        ChatActivityEnterView.this.slideText.setLayoutParams(params);
                        ChatActivityEnterView.this.slideText.setAlpha(TouchHelperCallback.ALPHA_FULL);
                        ChatActivityEnterView.this.startedDraggingX = GroundOverlayOptions.NO_DIMENSION;
                    }
                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });
        this.recordCircle = new RecordCircle(context);
        this.recordCircle.setVisibility(8);
        this.sizeNotifierLayout.addView(this.recordCircle, LayoutHelper.createFrame(124, 124.0f, 85, 0.0f, 0.0f, -36.0f, -38.0f));
        this.cancelBotButton = new ImageView(context);
        this.cancelBotButton.setVisibility(4);
        this.cancelBotButton.setScaleType(ScaleType.CENTER_INSIDE);
        ImageView imageView2 = this.cancelBotButton;
        Drawable closeProgressDrawable2 = new CloseProgressDrawable2();
        this.progressDrawable = closeProgressDrawable2;
        imageView2.setImageDrawable(closeProgressDrawable2);
        this.cancelBotButton.setSoundEffectsEnabled(false);
        this.cancelBotButton.setScaleX(0.1f);
        this.cancelBotButton.setScaleY(0.1f);
        this.cancelBotButton.setAlpha(0.0f);
        this.sendButtonContainer.addView(this.cancelBotButton, LayoutHelper.createFrame(48, 48.0f));
        this.cancelBotButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String text = ChatActivityEnterView.this.messageEditText.getText().toString();
                int idx = text.indexOf(32);
                if (idx == -1 || idx == text.length() - 1) {
                    ChatActivityEnterView.this.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                } else {
                    ChatActivityEnterView.this.setFieldText(text.substring(0, idx + 1));
                }
            }
        });
        this.sendButton = new ImageView(context);
        this.sendButton.setVisibility(4);
        this.sendButton.setScaleType(ScaleType.CENTER_INSIDE);
        this.sendButton.setImageResource(C0691R.drawable.ic_send);
        this.sendButton.setSoundEffectsEnabled(false);
        this.sendButton.setScaleX(0.1f);
        this.sendButton.setScaleY(0.1f);
        this.sendButton.setAlpha(0.0f);
        this.sendButtonContainer.addView(this.sendButton, LayoutHelper.createFrame(48, 48.0f));
        this.sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ChatActivityEnterView.this.sendMessage();
            }
        });
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
        this.keyboardHeight = sharedPreferences.getInt("kbd_height", AndroidUtilities.dp(200.0f));
        this.keyboardHeightLand = sharedPreferences.getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
        checkSendButton(false);
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == this.topView) {
            canvas.save();
            canvas.clipRect(0, 0, getMeasuredWidth(), child.getLayoutParams().height + AndroidUtilities.dp(2.0f));
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.topView) {
            canvas.restore();
        }
        return result;
    }

    protected void onDraw(Canvas canvas) {
        int top;
        if (this.topView == null || this.topView.getVisibility() != 0) {
            top = 0;
        } else {
            top = (int) this.topView.getTranslationY();
        }
        this.backgroundDrawable.setBounds(0, top, getMeasuredWidth(), getMeasuredHeight());
        this.backgroundDrawable.draw(canvas);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void showContextProgress(boolean show) {
        if (this.progressDrawable != null) {
            if (show) {
                this.progressDrawable.startAnimation();
            } else {
                this.progressDrawable.stopAnimation();
            }
        }
    }

    public void setCaption(String caption) {
        if (this.messageEditText != null) {
            this.messageEditText.setCaption(caption);
            checkSendButton(true);
        }
    }

    public void addTopView(View view, int height) {
        if (view != null) {
            this.topView = view;
            this.topView.setVisibility(8);
            this.topView.setTranslationY((float) height);
            addView(this.topView, 0, LayoutHelper.createFrame(-1, (float) height, 51, 0.0f, 2.0f, 0.0f, 0.0f));
            this.needShowTopView = false;
        }
    }

    public void setForceShowSendButton(boolean value, boolean animated) {
        this.forceShowSendButton = value;
        checkSendButton(animated);
    }

    public void setAllowStickersAndGifs(boolean value, boolean value2) {
        if (!((this.allowStickers == value && this.allowGifs == value2) || this.emojiView == null)) {
            if (this.emojiView.getVisibility() == 0) {
                hidePopup(false);
            }
            this.sizeNotifierLayout.removeView(this.emojiView);
            this.emojiView = null;
        }
        this.allowStickers = value;
        this.allowGifs = value2;
    }

    public void setOpenGifsTabFirst() {
        createEmojiView();
        this.emojiView.loadGifRecent();
        this.emojiView.switchToGifRecent();
    }

    public void showTopView(boolean animated, boolean openKeyboard) {
        if (this.topView != null && !this.topViewShowed && getVisibility() == 0) {
            this.needShowTopView = true;
            this.topViewShowed = true;
            if (this.allowShowTopView) {
                this.topView.setVisibility(0);
                if (this.currentTopViewAnimation != null) {
                    this.currentTopViewAnimation.cancel();
                    this.currentTopViewAnimation = null;
                }
                resizeForTopView(true);
                if (!animated) {
                    this.topView.setTranslationY(0.0f);
                } else if (this.keyboardVisible || isPopupShowing()) {
                    this.currentTopViewAnimation = new AnimatorSet();
                    AnimatorSet animatorSet = this.currentTopViewAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.topView, "translationY", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    this.currentTopViewAnimation.addListener(new AnonymousClass13(openKeyboard));
                    this.currentTopViewAnimation.setDuration(200);
                    this.currentTopViewAnimation.start();
                } else {
                    this.topView.setTranslationY(0.0f);
                    if (this.recordedAudioPanel.getVisibility() == 0) {
                        return;
                    }
                    if (!this.forceShowSendButton || openKeyboard) {
                        openKeyboard();
                    }
                }
            }
        }
    }

    public void hideTopView(boolean animated) {
        if (this.topView != null && this.topViewShowed) {
            this.topViewShowed = false;
            this.needShowTopView = false;
            if (this.allowShowTopView) {
                if (this.currentTopViewAnimation != null) {
                    this.currentTopViewAnimation.cancel();
                    this.currentTopViewAnimation = null;
                }
                if (animated) {
                    this.currentTopViewAnimation = new AnimatorSet();
                    AnimatorSet animatorSet = this.currentTopViewAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.topView, "translationY", new float[]{(float) this.topView.getLayoutParams().height});
                    animatorSet.playTogether(animatorArr);
                    this.currentTopViewAnimation.addListener(new AnimatorListenerAdapterProxy() {
                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                ChatActivityEnterView.this.topView.setVisibility(8);
                                ChatActivityEnterView.this.resizeForTopView(false);
                                ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (ChatActivityEnterView.this.currentTopViewAnimation != null && ChatActivityEnterView.this.currentTopViewAnimation.equals(animation)) {
                                ChatActivityEnterView.this.currentTopViewAnimation = null;
                            }
                        }
                    });
                    this.currentTopViewAnimation.setDuration(200);
                    this.currentTopViewAnimation.start();
                    return;
                }
                this.topView.setVisibility(8);
                this.topView.setTranslationY((float) this.topView.getLayoutParams().height);
            }
        }
    }

    public boolean isTopViewVisible() {
        return this.topView != null && this.topView.getVisibility() == 0;
    }

    private void onWindowSizeChanged() {
        int size = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            size -= this.emojiPadding;
        }
        if (this.delegate != null) {
            this.delegate.onWindowSizeChanged(size);
        }
        if (this.topView == null) {
            return;
        }
        if (size < AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight()) {
            if (this.allowShowTopView) {
                this.allowShowTopView = false;
                if (this.needShowTopView) {
                    this.topView.setVisibility(8);
                    resizeForTopView(false);
                    this.topView.setTranslationY((float) this.topView.getLayoutParams().height);
                }
            }
        } else if (!this.allowShowTopView) {
            this.allowShowTopView = true;
            if (this.needShowTopView) {
                this.topView.setVisibility(0);
                resizeForTopView(true);
                this.topView.setTranslationY(0.0f);
            }
        }
    }

    private void resizeForTopView(boolean show) {
        LayoutParams layoutParams = (LayoutParams) this.textFieldContainer.getLayoutParams();
        layoutParams.topMargin = (show ? this.topView.getLayoutParams().height : 0) + AndroidUtilities.dp(2.0f);
        this.textFieldContainer.setLayoutParams(layoutParams);
    }

    public void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStartError);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStopped);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioRouteChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
        if (this.emojiView != null) {
            this.emojiView.onDestroy();
        }
        if (this.mWakeLock != null) {
            try {
                this.mWakeLock.release();
                this.mWakeLock = null;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
        if (this.sizeNotifierLayout != null) {
            this.sizeNotifierLayout.setDelegate(null);
        }
    }

    public void onPause() {
        this.isPaused = true;
        closeKeyboard();
    }

    public void onResume() {
        this.isPaused = false;
        if (this.showKeyboardOnResume) {
            this.showKeyboardOnResume = false;
            this.messageEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.messageEditText);
            if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible) {
                this.waitingForKeyboardOpen = true;
                AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
            }
        }
    }

    public void setDialogId(long id) {
        int i = 1;
        this.dialog_id = id;
        if (((int) this.dialog_id) < 0) {
            boolean z;
            Chat currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) this.dialog_id)));
            this.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + this.dialog_id, false);
            if (!ChatObject.isChannel(currentChat) || (!(currentChat.creator || currentChat.editor) || currentChat.megagroup)) {
                z = false;
            } else {
                z = true;
            }
            this.canWriteToChannel = z;
            if (this.notifyButton != null) {
                int i2;
                ImageView imageView = this.notifyButton;
                if (this.canWriteToChannel) {
                    i2 = 0;
                } else {
                    i2 = 8;
                }
                imageView.setVisibility(i2);
                this.notifyButton.setImageResource(this.silent ? C0691R.drawable.notify_members_off : C0691R.drawable.notify_members_on);
                LinearLayout linearLayout = this.attachButton;
                float f = ((this.botButton == null || this.botButton.getVisibility() == 8) && (this.notifyButton == null || this.notifyButton.getVisibility() == 8)) ? 48.0f : 96.0f;
                linearLayout.setPivotX((float) AndroidUtilities.dp(f));
            }
            if (this.attachButton != null) {
                if (this.attachButton.getVisibility() != 0) {
                    i = 0;
                }
                updateFieldRight(i);
            }
        }
    }

    private void updateFieldHint() {
        boolean isChannel = false;
        if (((int) this.dialog_id) < 0) {
            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) this.dialog_id)));
            isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
        }
        if (!isChannel) {
            this.messageEditText.setHint(LocaleController.getString("TypeMessage", C0691R.string.TypeMessage));
        } else if (this.editingMessageObject != null) {
            CharSequence string;
            EditTextCaption editTextCaption = this.messageEditText;
            if (this.editingCaption) {
                string = LocaleController.getString("Caption", C0691R.string.Caption);
            } else {
                string = LocaleController.getString("TypeMessage", C0691R.string.TypeMessage);
            }
            editTextCaption.setHint(string);
        } else if (this.silent) {
            this.messageEditText.setHint(LocaleController.getString("ChannelSilentBroadcast", C0691R.string.ChannelSilentBroadcast));
        } else {
            this.messageEditText.setHint(LocaleController.getString("ChannelBroadcast", C0691R.string.ChannelBroadcast));
        }
    }

    public void setReplyingMessageObject(MessageObject messageObject) {
        if (messageObject != null) {
            if (this.botMessageObject == null && this.botButtonsMessageObject != this.replyingMessageObject) {
                this.botMessageObject = this.botButtonsMessageObject;
            }
            this.replyingMessageObject = messageObject;
            setButtons(this.replyingMessageObject, true);
        } else if (messageObject == null && this.replyingMessageObject == this.botButtonsMessageObject) {
            this.replyingMessageObject = null;
            setButtons(this.botMessageObject, false);
            this.botMessageObject = null;
        } else {
            this.replyingMessageObject = messageObject;
        }
    }

    public void setWebPage(WebPage webPage, boolean searchWebPages) {
        this.messageWebPage = webPage;
        this.messageWebPageSearch = searchWebPages;
    }

    public boolean isMessageWebPageSearchEnabled() {
        return this.messageWebPageSearch;
    }

    private void hideRecordedAudioPanel() {
        this.audioToSendPath = null;
        this.audioToSend = null;
        this.audioToSendMessageObject = null;
        AnimatorSet AnimatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this.recordedAudioPanel, "alpha", new float[]{0.0f});
        AnimatorSet.playTogether(animatorArr);
        AnimatorSet.setDuration(200);
        AnimatorSet.addListener(new AnimatorListenerAdapterProxy() {
            public void onAnimationEnd(Animator animation) {
                ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
            }
        });
        AnimatorSet.start();
    }

    private void sendMessage() {
        if (this.parentFragment != null) {
            String action;
            if (((int) this.dialog_id) < 0) {
                Chat currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) this.dialog_id)));
                if (currentChat == null || currentChat.participants_count <= MessagesController.getInstance().groupBigSize) {
                    action = "chat_message";
                } else {
                    action = "bigchat_message";
                }
            } else {
                action = "pm_message";
            }
            if (!MessagesController.isFeatureEnabled(action, this.parentFragment)) {
                return;
            }
        }
        if (this.audioToSend != null) {
            MessageObject playing = MediaController.getInstance().getPlayingMessageObject();
            if (playing != null && playing == this.audioToSendMessageObject) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            SendMessagesHelper.getInstance().sendMessage(this.audioToSend, null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, null, null);
            if (this.delegate != null) {
                this.delegate.onMessageSend(null);
            }
            hideRecordedAudioPanel();
            checkSendButton(true);
            return;
        }
        CharSequence message = this.messageEditText.getText();
        if (processSendingText(message)) {
            this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.lastTypingTimeSend = 0;
            if (this.delegate != null) {
                this.delegate.onMessageSend(message);
            }
        } else if (this.forceShowSendButton && this.delegate != null) {
            this.delegate.onMessageSend(null);
        }
    }

    public void doneEditingMessage() {
        if (this.editingMessageObject != null) {
            this.delegate.onMessageEditEnd(true);
            this.editingMessageReqId = SendMessagesHelper.getInstance().editMessage(this.editingMessageObject, this.messageEditText.getText().toString(), this.messageWebPageSearch, this.parentFragment, MessagesQuery.getEntities(this.messageEditText.getText()), new Runnable() {
                public void run() {
                    ChatActivityEnterView.this.editingMessageReqId = 0;
                    ChatActivityEnterView.this.setEditingMessageObject(null, false);
                }
            });
        }
    }

    public boolean processSendingText(CharSequence text) {
        text = AndroidUtilities.getTrimmedString(text);
        if (text.length() == 0) {
            return false;
        }
        int count = (int) Math.ceil((double) (((float) text.length()) / 4096.0f));
        for (int a = 0; a < count; a++) {
            CharSequence mess = text.subSequence(a * MessagesController.UPDATE_MASK_SEND_STATE, Math.min((a + 1) * MessagesController.UPDATE_MASK_SEND_STATE, text.length()));
            SendMessagesHelper.getInstance().sendMessage(mess.toString(), this.dialog_id, this.replyingMessageObject, this.messageWebPage, this.messageWebPageSearch, MessagesQuery.getEntities(mess), null, null);
        }
        return true;
    }

    private void checkSendButton(boolean animated) {
        if (this.editingMessageObject == null) {
            if (this.isPaused) {
                animated = false;
            }
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            ArrayList<Animator> animators;
            if (AndroidUtilities.getTrimmedString(this.messageEditText.getText()).length() > 0 || this.forceShowSendButton || this.audioToSend != null) {
                boolean showBotButton;
                boolean showSendButton;
                if (this.messageEditText.caption == null || this.sendButton.getVisibility() != 0) {
                    showBotButton = false;
                } else {
                    showBotButton = true;
                }
                if (this.messageEditText.caption == null && this.cancelBotButton.getVisibility() == 0) {
                    showSendButton = true;
                } else {
                    showSendButton = false;
                }
                if (this.audioSendButton.getVisibility() != 0 && !showBotButton && !showSendButton) {
                    return;
                }
                if (!animated) {
                    this.audioSendButton.setScaleX(0.1f);
                    this.audioSendButton.setScaleY(0.1f);
                    this.audioSendButton.setAlpha(0.0f);
                    if (this.messageEditText.caption != null) {
                        this.sendButton.setScaleX(0.1f);
                        this.sendButton.setScaleY(0.1f);
                        this.sendButton.setAlpha(0.0f);
                        this.cancelBotButton.setScaleX(TouchHelperCallback.ALPHA_FULL);
                        this.cancelBotButton.setScaleY(TouchHelperCallback.ALPHA_FULL);
                        this.cancelBotButton.setAlpha(TouchHelperCallback.ALPHA_FULL);
                        this.cancelBotButton.setVisibility(0);
                        this.sendButton.setVisibility(8);
                    } else {
                        this.cancelBotButton.setScaleX(0.1f);
                        this.cancelBotButton.setScaleY(0.1f);
                        this.cancelBotButton.setAlpha(0.0f);
                        this.sendButton.setScaleX(TouchHelperCallback.ALPHA_FULL);
                        this.sendButton.setScaleY(TouchHelperCallback.ALPHA_FULL);
                        this.sendButton.setAlpha(TouchHelperCallback.ALPHA_FULL);
                        this.sendButton.setVisibility(0);
                        this.cancelBotButton.setVisibility(8);
                    }
                    this.audioSendButton.setVisibility(8);
                    if (this.attachButton != null) {
                        this.attachButton.setVisibility(8);
                        if (this.delegate != null) {
                            this.delegate.onAttachButtonHidden();
                        }
                        updateFieldRight(0);
                    }
                } else if (this.runningAnimationType != 1 || this.messageEditText.caption != null) {
                    if (this.runningAnimationType != 3 || this.messageEditText.caption == null) {
                        if (this.runningAnimation != null) {
                            this.runningAnimation.cancel();
                            this.runningAnimation = null;
                        }
                        if (this.runningAnimation2 != null) {
                            this.runningAnimation2.cancel();
                            this.runningAnimation2 = null;
                        }
                        if (this.attachButton != null) {
                            this.runningAnimation2 = new AnimatorSet();
                            animatorSet = this.runningAnimation2;
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.attachButton, "alpha", new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.attachButton, "scaleX", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                            this.runningAnimation2.setDuration(100);
                            this.runningAnimation2.addListener(new AnimatorListenerAdapterProxy() {
                                public void onAnimationEnd(Animator animation) {
                                    if (ChatActivityEnterView.this.runningAnimation2 != null && ChatActivityEnterView.this.runningAnimation2.equals(animation)) {
                                        ChatActivityEnterView.this.attachButton.setVisibility(8);
                                    }
                                }

                                public void onAnimationCancel(Animator animation) {
                                    if (ChatActivityEnterView.this.runningAnimation2 != null && ChatActivityEnterView.this.runningAnimation2.equals(animation)) {
                                        ChatActivityEnterView.this.runningAnimation2 = null;
                                    }
                                }
                            });
                            this.runningAnimation2.start();
                            updateFieldRight(0);
                            if (this.delegate != null) {
                                this.delegate.onAttachButtonHidden();
                            }
                        }
                        this.runningAnimation = new AnimatorSet();
                        animators = new ArrayList();
                        if (this.audioSendButton.getVisibility() == 0) {
                            animators.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleX", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleY", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[]{0.0f}));
                        }
                        if (showBotButton) {
                            animators.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[]{0.0f}));
                        } else if (showSendButton) {
                            animators.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[]{0.1f}));
                            animators.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[]{0.0f}));
                        }
                        if (this.messageEditText.caption != null) {
                            this.runningAnimationType = 3;
                            animators.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[]{TouchHelperCallback.ALPHA_FULL}));
                            animators.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[]{TouchHelperCallback.ALPHA_FULL}));
                            animators.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[]{TouchHelperCallback.ALPHA_FULL}));
                            this.cancelBotButton.setVisibility(0);
                        } else {
                            this.runningAnimationType = 1;
                            animators.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[]{TouchHelperCallback.ALPHA_FULL}));
                            animators.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[]{TouchHelperCallback.ALPHA_FULL}));
                            animators.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[]{TouchHelperCallback.ALPHA_FULL}));
                            this.sendButton.setVisibility(0);
                        }
                        this.runningAnimation.playTogether(animators);
                        this.runningAnimation.setDuration(150);
                        this.runningAnimation.addListener(new AnimatorListenerAdapterProxy() {
                            public void onAnimationEnd(Animator animation) {
                                if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animation)) {
                                    if (ChatActivityEnterView.this.messageEditText.caption != null) {
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(0);
                                        ChatActivityEnterView.this.sendButton.setVisibility(8);
                                    } else {
                                        ChatActivityEnterView.this.sendButton.setVisibility(0);
                                        ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                    }
                                    ChatActivityEnterView.this.audioSendButton.setVisibility(8);
                                    ChatActivityEnterView.this.runningAnimation = null;
                                    ChatActivityEnterView.this.runningAnimationType = 0;
                                }
                            }

                            public void onAnimationCancel(Animator animation) {
                                if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animation)) {
                                    ChatActivityEnterView.this.runningAnimation = null;
                                }
                            }
                        });
                        this.runningAnimation.start();
                    }
                }
            } else if (this.sendButton.getVisibility() != 0 && this.cancelBotButton.getVisibility() != 0) {
            } else {
                if (!animated) {
                    this.sendButton.setScaleX(0.1f);
                    this.sendButton.setScaleY(0.1f);
                    this.sendButton.setAlpha(0.0f);
                    this.cancelBotButton.setScaleX(0.1f);
                    this.cancelBotButton.setScaleY(0.1f);
                    this.cancelBotButton.setAlpha(0.0f);
                    this.audioSendButton.setScaleX(TouchHelperCallback.ALPHA_FULL);
                    this.audioSendButton.setScaleY(TouchHelperCallback.ALPHA_FULL);
                    this.audioSendButton.setAlpha(TouchHelperCallback.ALPHA_FULL);
                    this.cancelBotButton.setVisibility(8);
                    this.sendButton.setVisibility(8);
                    this.audioSendButton.setVisibility(0);
                    if (this.attachButton != null) {
                        this.delegate.onAttachButtonShow();
                        this.attachButton.setVisibility(0);
                        updateFieldRight(1);
                    }
                } else if (this.runningAnimationType != 2) {
                    if (this.runningAnimation != null) {
                        this.runningAnimation.cancel();
                        this.runningAnimation = null;
                    }
                    if (this.runningAnimation2 != null) {
                        this.runningAnimation2.cancel();
                        this.runningAnimation2 = null;
                    }
                    if (this.attachButton != null) {
                        this.attachButton.setVisibility(0);
                        this.runningAnimation2 = new AnimatorSet();
                        animatorSet = this.runningAnimation2;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.attachButton, "alpha", new float[]{TouchHelperCallback.ALPHA_FULL});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.attachButton, "scaleX", new float[]{TouchHelperCallback.ALPHA_FULL});
                        animatorSet.playTogether(animatorArr);
                        this.runningAnimation2.setDuration(100);
                        this.runningAnimation2.start();
                        updateFieldRight(1);
                        this.delegate.onAttachButtonShow();
                    }
                    this.audioSendButton.setVisibility(0);
                    this.runningAnimation = new AnimatorSet();
                    this.runningAnimationType = 2;
                    animators = new ArrayList();
                    animators.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleX", new float[]{TouchHelperCallback.ALPHA_FULL}));
                    animators.add(ObjectAnimator.ofFloat(this.audioSendButton, "scaleY", new float[]{TouchHelperCallback.ALPHA_FULL}));
                    animators.add(ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[]{TouchHelperCallback.ALPHA_FULL}));
                    if (this.cancelBotButton.getVisibility() == 0) {
                        animators.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[]{0.1f}));
                        animators.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[]{0.1f}));
                        animators.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[]{0.0f}));
                    } else {
                        animators.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[]{0.1f}));
                        animators.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[]{0.1f}));
                        animators.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[]{0.0f}));
                    }
                    this.runningAnimation.playTogether(animators);
                    this.runningAnimation.setDuration(150);
                    this.runningAnimation.addListener(new AnimatorListenerAdapterProxy() {
                        public void onAnimationEnd(Animator animation) {
                            if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animation)) {
                                ChatActivityEnterView.this.sendButton.setVisibility(8);
                                ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                                ChatActivityEnterView.this.audioSendButton.setVisibility(0);
                                ChatActivityEnterView.this.runningAnimation = null;
                                ChatActivityEnterView.this.runningAnimationType = 0;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (ChatActivityEnterView.this.runningAnimation != null && ChatActivityEnterView.this.runningAnimation.equals(animation)) {
                                ChatActivityEnterView.this.runningAnimation = null;
                            }
                        }
                    });
                    this.runningAnimation.start();
                }
            }
        }
    }

    private void updateFieldRight(int attachVisible) {
        if (this.messageEditText != null && this.editingMessageObject == null) {
            LayoutParams layoutParams = (LayoutParams) this.messageEditText.getLayoutParams();
            if (attachVisible == 1) {
                if ((this.botButton == null || this.botButton.getVisibility() != 0) && (this.notifyButton == null || this.notifyButton.getVisibility() != 0)) {
                    layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                }
            } else if (attachVisible != 2) {
                layoutParams.rightMargin = AndroidUtilities.dp(2.0f);
            } else if (layoutParams.rightMargin != AndroidUtilities.dp(2.0f)) {
                if ((this.botButton == null || this.botButton.getVisibility() != 0) && (this.notifyButton == null || this.notifyButton.getVisibility() != 0)) {
                    layoutParams.rightMargin = AndroidUtilities.dp(50.0f);
                } else {
                    layoutParams.rightMargin = AndroidUtilities.dp(98.0f);
                }
            }
            this.messageEditText.setLayoutParams(layoutParams);
        }
    }

    private void updateAudioRecordIntefrace() {
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (!this.recordingAudio) {
            if (this.mWakeLock != null) {
                try {
                    this.mWakeLock.release();
                    this.mWakeLock = null;
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
            AndroidUtilities.unlockOrientation(this.parentActivity);
            if (this.audioInterfaceState != 0) {
                this.audioInterfaceState = 0;
                if (this.runningAnimationAudio != null) {
                    this.runningAnimationAudio.cancel();
                }
                this.runningAnimationAudio = new AnimatorSet();
                animatorSet = this.runningAnimationAudio;
                animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[]{(float) AndroidUtilities.displaySize.x});
                animatorArr[1] = ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[]{TouchHelperCallback.ALPHA_FULL});
                animatorSet.playTogether(animatorArr);
                this.runningAnimationAudio.setDuration(300);
                this.runningAnimationAudio.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animator) {
                        if (ChatActivityEnterView.this.runningAnimationAudio != null && ChatActivityEnterView.this.runningAnimationAudio.equals(animator)) {
                            LayoutParams params = (LayoutParams) ChatActivityEnterView.this.slideText.getLayoutParams();
                            params.leftMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
                            ChatActivityEnterView.this.slideText.setLayoutParams(params);
                            ChatActivityEnterView.this.slideText.setAlpha(TouchHelperCallback.ALPHA_FULL);
                            ChatActivityEnterView.this.recordPanel.setVisibility(8);
                            ChatActivityEnterView.this.recordCircle.setVisibility(8);
                            ChatActivityEnterView.this.runningAnimationAudio = null;
                        }
                    }
                });
                this.runningAnimationAudio.setInterpolator(new AccelerateInterpolator());
                this.runningAnimationAudio.start();
            }
        } else if (this.audioInterfaceState != 1) {
            this.audioInterfaceState = 1;
            try {
                if (this.mWakeLock == null) {
                    this.mWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(536870918, "audio record lock");
                    this.mWakeLock.acquire();
                }
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
            AndroidUtilities.lockOrientation(this.parentActivity);
            this.recordPanel.setVisibility(0);
            this.recordCircle.setVisibility(0);
            this.recordCircle.setAmplitude(0.0d);
            this.recordTimeText.setText("00:00");
            this.recordDot.resetAlpha();
            this.lastTimeString = null;
            LayoutParams params = (LayoutParams) this.slideText.getLayoutParams();
            params.leftMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
            this.slideText.setLayoutParams(params);
            this.slideText.setAlpha(TouchHelperCallback.ALPHA_FULL);
            this.recordPanel.setX((float) AndroidUtilities.displaySize.x);
            this.recordCircle.setTranslationX(0.0f);
            if (this.runningAnimationAudio != null) {
                this.runningAnimationAudio.cancel();
            }
            this.runningAnimationAudio = new AnimatorSet();
            animatorSet = this.runningAnimationAudio;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[]{TouchHelperCallback.ALPHA_FULL});
            animatorArr[2] = ObjectAnimator.ofFloat(this.audioSendButton, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.runningAnimationAudio.setDuration(300);
            this.runningAnimationAudio.addListener(new AnimatorListenerAdapterProxy() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatActivityEnterView.this.runningAnimationAudio != null && ChatActivityEnterView.this.runningAnimationAudio.equals(animator)) {
                        ChatActivityEnterView.this.recordPanel.setX(0.0f);
                        ChatActivityEnterView.this.runningAnimationAudio = null;
                    }
                }
            });
            this.runningAnimationAudio.setInterpolator(new DecelerateInterpolator());
            this.runningAnimationAudio.start();
        }
    }

    public void setDelegate(ChatActivityEnterViewDelegate delegate) {
        this.delegate = delegate;
    }

    public void setCommand(MessageObject messageObject, String command, boolean longPress, boolean username) {
        if (command != null && getVisibility() == 0) {
            User user;
            if (longPress) {
                String text = this.messageEditText.getText().toString();
                user = (messageObject == null || ((int) this.dialog_id) >= 0) ? null : MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if ((this.botCount != 1 || username) && user != null && user.bot && !command.contains("@")) {
                    text = String.format(Locale.US, "%s@%s", new Object[]{command, user.username}) + " " + text.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", TtmlNode.ANONYMOUS_REGION_ID);
                } else {
                    text = command + " " + text.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", TtmlNode.ANONYMOUS_REGION_ID);
                }
                this.ignoreTextChange = true;
                this.messageEditText.setText(text);
                this.messageEditText.setSelection(this.messageEditText.getText().length());
                this.ignoreTextChange = false;
                if (this.delegate != null) {
                    this.delegate.onTextChanged(this.messageEditText.getText(), true);
                }
                if (!this.keyboardVisible && this.currentPopupContentType == -1) {
                    openKeyboard();
                    return;
                }
                return;
            }
            user = (messageObject == null || ((int) this.dialog_id) >= 0) ? null : MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
            if ((this.botCount != 1 || username) && user != null && user.bot && !command.contains("@")) {
                SendMessagesHelper.getInstance().sendMessage(String.format(Locale.US, "%s@%s", new Object[]{command, user.username}), this.dialog_id, null, null, false, null, null, null);
            } else {
                SendMessagesHelper.getInstance().sendMessage(command, this.dialog_id, null, null, false, null, null, null);
            }
        }
    }

    public void setEditingMessageObject(MessageObject messageObject, boolean caption) {
        if (this.audioToSend == null && this.editingMessageObject != messageObject) {
            if (this.editingMessageReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.editingMessageReqId, true);
                this.editingMessageReqId = 0;
            }
            this.editingMessageObject = messageObject;
            this.editingCaption = caption;
            if (this.editingMessageObject != null) {
                InputFilter[] inputFilters = new InputFilter[1];
                if (caption) {
                    inputFilters[0] = new LengthFilter(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                    if (this.editingMessageObject.caption != null) {
                        setFieldText(Emoji.replaceEmoji(new SpannableStringBuilder(this.editingMessageObject.caption.toString()), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
                    } else {
                        setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                } else {
                    inputFilters[0] = new LengthFilter(MessagesController.UPDATE_MASK_SEND_STATE);
                    if (this.editingMessageObject.messageText != null) {
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(this.editingMessageObject.messageText.toString());
                        ArrayList<MessageEntity> entities = MessagesQuery.getEntities(this.editingMessageObject.messageText);
                        if (entities != null) {
                            for (int a = 0; a < entities.size(); a++) {
                                TL_inputMessageEntityMentionName entity = (TL_inputMessageEntityMentionName) entities.get(a);
                                if (entity.offset + entity.length < this.editingMessageObject.messageText.length() && this.editingMessageObject.messageText.charAt(entity.offset + entity.length) == ' ') {
                                    entity.length++;
                                }
                                stringBuilder.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + entity.user_id.user_id), entity.offset, entity.offset + entity.length, 33);
                            }
                        }
                        setFieldText(Emoji.replaceEmoji(stringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
                    } else {
                        setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                }
                this.messageEditText.setFilters(inputFilters);
                openKeyboard();
                LayoutParams layoutParams = (LayoutParams) this.messageEditText.getLayoutParams();
                layoutParams.rightMargin = AndroidUtilities.dp(4.0f);
                this.messageEditText.setLayoutParams(layoutParams);
                this.sendButton.setVisibility(8);
                this.cancelBotButton.setVisibility(8);
                this.audioSendButton.setVisibility(8);
                this.attachButton.setVisibility(8);
                this.sendButtonContainer.setVisibility(8);
            } else {
                this.messageEditText.setFilters(new InputFilter[0]);
                this.delegate.onMessageEditEnd(false);
                this.audioSendButton.setVisibility(0);
                this.attachButton.setVisibility(0);
                this.sendButtonContainer.setVisibility(0);
                this.attachButton.setScaleX(TouchHelperCallback.ALPHA_FULL);
                this.attachButton.setAlpha(TouchHelperCallback.ALPHA_FULL);
                this.sendButton.setScaleX(0.1f);
                this.sendButton.setScaleY(0.1f);
                this.sendButton.setAlpha(0.0f);
                this.cancelBotButton.setScaleX(0.1f);
                this.cancelBotButton.setScaleY(0.1f);
                this.cancelBotButton.setAlpha(0.0f);
                this.audioSendButton.setScaleX(TouchHelperCallback.ALPHA_FULL);
                this.audioSendButton.setScaleY(TouchHelperCallback.ALPHA_FULL);
                this.audioSendButton.setAlpha(TouchHelperCallback.ALPHA_FULL);
                this.sendButton.setVisibility(8);
                this.cancelBotButton.setVisibility(8);
                this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.delegate.onAttachButtonShow();
                updateFieldRight(1);
            }
            updateFieldHint();
        }
    }

    public void setFieldText(CharSequence text) {
        if (this.messageEditText != null) {
            this.ignoreTextChange = true;
            this.messageEditText.setText(text);
            this.messageEditText.setSelection(this.messageEditText.getText().length());
            this.ignoreTextChange = false;
            if (this.delegate != null) {
                this.delegate.onTextChanged(this.messageEditText.getText(), true);
            }
        }
    }

    public void setSelection(int start) {
        if (this.messageEditText != null) {
            this.messageEditText.setSelection(start, this.messageEditText.length());
        }
    }

    public int getCursorPosition() {
        if (this.messageEditText == null) {
            return 0;
        }
        return this.messageEditText.getSelectionStart();
    }

    public void replaceWithText(int start, int len, CharSequence text) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.messageEditText.getText());
            builder.replace(start, start + len, text);
            this.messageEditText.setText(builder);
            this.messageEditText.setSelection(text.length() + start);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public void setFieldFocused() {
        if (this.messageEditText != null) {
            try {
                this.messageEditText.requestFocus();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    public void setFieldFocused(boolean focus) {
        if (this.messageEditText != null) {
            if (focus) {
                if (!this.messageEditText.isFocused()) {
                    this.messageEditText.postDelayed(new Runnable() {
                        public void run() {
                            if (ChatActivityEnterView.this.messageEditText != null) {
                                try {
                                    ChatActivityEnterView.this.messageEditText.requestFocus();
                                } catch (Throwable e) {
                                    FileLog.m13e("tmessages", e);
                                }
                            }
                        }
                    }, 600);
                }
            } else if (this.messageEditText.isFocused() && !this.keyboardVisible) {
                this.messageEditText.clearFocus();
            }
        }
    }

    public boolean hasText() {
        return this.messageEditText != null && this.messageEditText.length() > 0;
    }

    public CharSequence getFieldText() {
        if (this.messageEditText == null || this.messageEditText.length() <= 0) {
            return null;
        }
        return this.messageEditText.getText();
    }

    public void addToAttachLayout(View view) {
        if (this.attachButton != null) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            if (VERSION.SDK_INT >= 21) {
                view.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.INPUT_FIELD_SELECTOR_COLOR));
            }
            this.attachButton.addView(view, LayoutHelper.createLinear(48, 48));
        }
    }

    private void updateBotButton() {
        if (this.botButton != null) {
            float f;
            if (this.hasBotCommands || this.botReplyMarkup != null) {
                if (this.botButton.getVisibility() != 0) {
                    this.botButton.setVisibility(0);
                }
                if (this.botReplyMarkup == null) {
                    this.botButton.setImageResource(C0691R.drawable.bot_keyboard);
                } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                    this.botButton.setImageResource(C0691R.drawable.ic_msg_panel_kb);
                } else {
                    this.botButton.setImageResource(C0691R.drawable.bot_keyboard2);
                }
            } else {
                this.botButton.setVisibility(8);
            }
            updateFieldRight(2);
            LinearLayout linearLayout = this.attachButton;
            if ((this.botButton == null || this.botButton.getVisibility() == 8) && (this.notifyButton == null || this.notifyButton.getVisibility() == 8)) {
                f = 48.0f;
            } else {
                f = 96.0f;
            }
            linearLayout.setPivotX((float) AndroidUtilities.dp(f));
        }
    }

    public void setBotsCount(int count, boolean hasCommands) {
        this.botCount = count;
        if (this.hasBotCommands != hasCommands) {
            this.hasBotCommands = hasCommands;
            updateBotButton();
        }
    }

    public void setButtons(MessageObject messageObject) {
        setButtons(messageObject, true);
    }

    public void setButtons(MessageObject messageObject, boolean openKeyboard) {
        TL_replyKeyboardMarkup tL_replyKeyboardMarkup = null;
        if (this.replyingMessageObject != null && this.replyingMessageObject == this.botButtonsMessageObject && this.replyingMessageObject != messageObject) {
            this.botMessageObject = messageObject;
        } else if (this.botButton == null) {
        } else {
            if (this.botButtonsMessageObject != null && this.botButtonsMessageObject == messageObject) {
                return;
            }
            if (this.botButtonsMessageObject != null || messageObject != null) {
                if (this.botKeyboardView == null) {
                    this.botKeyboardView = new BotKeyboardView(this.parentActivity);
                    this.botKeyboardView.setVisibility(8);
                    this.botKeyboardView.setDelegate(new BotKeyboardViewDelegate() {
                        public void didPressedButton(KeyboardButton button) {
                            MessageObject object = ChatActivityEnterView.this.replyingMessageObject != null ? ChatActivityEnterView.this.replyingMessageObject : ((int) ChatActivityEnterView.this.dialog_id) < 0 ? ChatActivityEnterView.this.botButtonsMessageObject : null;
                            ChatActivityEnterView.this.didPressedBotButton(button, object, ChatActivityEnterView.this.replyingMessageObject != null ? ChatActivityEnterView.this.replyingMessageObject : ChatActivityEnterView.this.botButtonsMessageObject);
                            if (ChatActivityEnterView.this.replyingMessageObject != null) {
                                ChatActivityEnterView.this.openKeyboardInternal();
                                ChatActivityEnterView.this.setButtons(ChatActivityEnterView.this.botMessageObject, false);
                            } else if (ChatActivityEnterView.this.botButtonsMessageObject.messageOwner.reply_markup.single_use) {
                                ChatActivityEnterView.this.openKeyboardInternal();
                                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("answered_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
                            }
                            if (ChatActivityEnterView.this.delegate != null) {
                                ChatActivityEnterView.this.delegate.onMessageSend(null);
                            }
                        }
                    });
                    this.sizeNotifierLayout.addView(this.botKeyboardView);
                }
                this.botButtonsMessageObject = messageObject;
                TL_replyKeyboardMarkup tL_replyKeyboardMarkup2 = (messageObject == null || !(messageObject.messageOwner.reply_markup instanceof TL_replyKeyboardMarkup)) ? null : (TL_replyKeyboardMarkup) messageObject.messageOwner.reply_markup;
                this.botReplyMarkup = tL_replyKeyboardMarkup2;
                this.botKeyboardView.setPanelHeight(AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight);
                BotKeyboardView botKeyboardView = this.botKeyboardView;
                if (this.botReplyMarkup != null) {
                    tL_replyKeyboardMarkup = this.botReplyMarkup;
                }
                botKeyboardView.setButtons(tL_replyKeyboardMarkup);
                if (this.botReplyMarkup != null) {
                    boolean keyboardHidden;
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (preferences.getInt("hidekeyboard_" + this.dialog_id, 0) == messageObject.getId()) {
                        keyboardHidden = true;
                    } else {
                        keyboardHidden = false;
                    }
                    if (this.botButtonsMessageObject == this.replyingMessageObject || !this.botReplyMarkup.single_use || preferences.getInt("answered_" + this.dialog_id, 0) != messageObject.getId()) {
                        if (!(keyboardHidden || this.messageEditText.length() != 0 || isPopupShowing())) {
                            showPopup(1, 1);
                        }
                    } else {
                        return;
                    }
                } else if (isPopupShowing() && this.currentPopupContentType == 1) {
                    if (openKeyboard) {
                        openKeyboardInternal();
                    } else {
                        showPopup(0, 1);
                    }
                }
                updateBotButton();
            }
        }
    }

    public void didPressedBotButton(KeyboardButton button, MessageObject replyMessageObject, MessageObject messageObject) {
        if (button != null && messageObject != null) {
            if (button instanceof TL_keyboardButton) {
                SendMessagesHelper.getInstance().sendMessage(button.text, this.dialog_id, replyMessageObject, null, false, null, null, null);
            } else if (button instanceof TL_keyboardButtonUrl) {
                this.parentFragment.showOpenUrlAlert(button.url);
            } else if (button instanceof TL_keyboardButtonRequestPhone) {
                this.parentFragment.shareMyContact(messageObject);
            } else if (button instanceof TL_keyboardButtonRequestGeoLocation) {
                Builder builder = new Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString("ShareYouLocationTitle", C0691R.string.ShareYouLocationTitle));
                builder.setMessage(LocaleController.getString("ShareYouLocationInfo", C0691R.string.ShareYouLocationInfo));
                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new AnonymousClass24(messageObject, button));
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                this.parentFragment.showDialog(builder.create());
            } else if (button instanceof TL_keyboardButtonCallback) {
                SendMessagesHelper.getInstance().sendCallback(messageObject, button, this.parentFragment);
            } else if ((button instanceof TL_keyboardButtonSwitchInline) && !this.parentFragment.processSwitchButton((TL_keyboardButtonSwitchInline) button)) {
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 1);
                DialogsActivity fragment = new DialogsActivity(args);
                fragment.setDelegate(new AnonymousClass25(messageObject, button));
                this.parentFragment.presentFragment(fragment);
            }
        }
    }

    public boolean isPopupView(View view) {
        return view == this.botKeyboardView || view == this.emojiView;
    }

    public boolean isRecordCircle(View view) {
        return view == this.recordCircle;
    }

    private void createEmojiView() {
        if (this.emojiView == null) {
            this.emojiView = new EmojiView(this.allowStickers, this.allowGifs, this.parentActivity);
            this.emojiView.setVisibility(8);
            this.emojiView.setListener(new Listener() {

                /* renamed from: org.telegram.ui.Components.ChatActivityEnterView.26.1 */
                class C11101 implements OnClickListener {
                    C11101() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChatActivityEnterView.this.emojiView.clearRecentEmoji();
                    }
                }

                public boolean onBackspace() {
                    if (ChatActivityEnterView.this.messageEditText.length() == 0) {
                        return false;
                    }
                    ChatActivityEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
                    return true;
                }

                public void onEmojiSelected(String symbol) {
                    int i = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
                    if (i < 0) {
                        i = 0;
                    }
                    try {
                        ChatActivityEnterView.this.innerTextChange = 2;
                        CharSequence localCharSequence = Emoji.replaceEmoji(symbol, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(i, localCharSequence));
                        int j = i + localCharSequence.length();
                        ChatActivityEnterView.this.messageEditText.setSelection(j, j);
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    } finally {
                        ChatActivityEnterView.this.innerTextChange = 0;
                    }
                }

                public void onStickerSelected(Document sticker) {
                    ChatActivityEnterView.this.onStickerSelected(sticker);
                }

                public void onStickersSettingsClick() {
                    if (ChatActivityEnterView.this.parentFragment != null) {
                        ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity());
                    }
                }

                public void onGifSelected(Document gif) {
                    SendMessagesHelper.getInstance().sendSticker(gif, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
                    if (((int) ChatActivityEnterView.this.dialog_id) == 0) {
                        MessagesController.getInstance().saveGif(gif);
                    }
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onMessageSend(null);
                    }
                }

                public void onGifTab(boolean opened) {
                    if (!AndroidUtilities.usingHardwareInput) {
                        if (opened) {
                            if (ChatActivityEnterView.this.messageEditText.length() == 0) {
                                ChatActivityEnterView.this.messageEditText.setText("@gif ");
                                ChatActivityEnterView.this.messageEditText.setSelection(ChatActivityEnterView.this.messageEditText.length());
                            }
                        } else if (ChatActivityEnterView.this.messageEditText.getText().toString().equals("@gif ")) {
                            ChatActivityEnterView.this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        }
                    }
                }

                public void onStickersTab(boolean opened) {
                    ChatActivityEnterView.this.delegate.onStickersTab(opened);
                }

                public void onClearEmojiRecent() {
                    if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentActivity != null) {
                        Builder builder = new Builder(ChatActivityEnterView.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                        builder.setMessage(LocaleController.getString("ClearRecentEmoji", C0691R.string.ClearRecentEmoji));
                        builder.setPositiveButton(LocaleController.getString("ClearButton", C0691R.string.ClearButton).toUpperCase(), new C11101());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                        ChatActivityEnterView.this.parentFragment.showDialog(builder.create());
                    }
                }
            });
            this.emojiView.setVisibility(8);
            this.sizeNotifierLayout.addView(this.emojiView);
        }
    }

    public void onStickerSelected(Document sticker) {
        SendMessagesHelper.getInstance().sendSticker(sticker, this.dialog_id, this.replyingMessageObject);
        if (this.delegate != null) {
            this.delegate.onMessageSend(null);
        }
    }

    public void addStickerToRecent(Document sticker) {
        createEmojiView();
        this.emojiView.addRecentSticker(sticker);
    }

    private void showPopup(int show, int contentType) {
        if (show == 1) {
            if (contentType == 0 && this.emojiView == null) {
                if (this.parentActivity != null) {
                    createEmojiView();
                } else {
                    return;
                }
            }
            View currentView = null;
            if (contentType == 0) {
                this.emojiView.setVisibility(0);
                if (!(this.botKeyboardView == null || this.botKeyboardView.getVisibility() == 8)) {
                    this.botKeyboardView.setVisibility(8);
                }
                currentView = this.emojiView;
            } else if (contentType == 1) {
                if (!(this.emojiView == null || this.emojiView.getVisibility() == 8)) {
                    this.emojiView.setVisibility(8);
                }
                this.botKeyboardView.setVisibility(0);
                currentView = this.botKeyboardView;
            }
            this.currentPopupContentType = contentType;
            if (this.keyboardHeight <= 0) {
                this.keyboardHeight = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200.0f));
            }
            if (this.keyboardHeightLand <= 0) {
                this.keyboardHeightLand = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
            }
            int currentHeight = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? this.keyboardHeightLand : this.keyboardHeight;
            if (contentType == 1) {
                currentHeight = Math.min(this.botKeyboardView.getKeyboardHeight(), currentHeight);
            }
            if (this.botKeyboardView != null) {
                this.botKeyboardView.setPanelHeight(currentHeight);
            }
            LayoutParams layoutParams = (LayoutParams) currentView.getLayoutParams();
            layoutParams.width = AndroidUtilities.displaySize.x;
            layoutParams.height = currentHeight;
            currentView.setLayoutParams(layoutParams);
            AndroidUtilities.hideKeyboard(this.messageEditText);
            if (this.sizeNotifierLayout != null) {
                this.emojiPadding = currentHeight;
                this.sizeNotifierLayout.requestLayout();
                if (contentType == 0) {
                    this.emojiButton.setImageResource(C0691R.drawable.ic_msg_panel_kb);
                } else if (contentType == 1) {
                    this.emojiButton.setImageResource(C0691R.drawable.ic_msg_panel_smiles);
                }
                updateBotButton();
                onWindowSizeChanged();
                return;
            }
            return;
        }
        if (this.emojiButton != null) {
            this.emojiButton.setImageResource(C0691R.drawable.ic_msg_panel_smiles);
        }
        this.currentPopupContentType = -1;
        if (this.emojiView != null) {
            this.emojiView.setVisibility(8);
        }
        if (this.botKeyboardView != null) {
            this.botKeyboardView.setVisibility(8);
        }
        if (this.sizeNotifierLayout != null) {
            if (show == 0) {
                this.emojiPadding = 0;
            }
            this.sizeNotifierLayout.requestLayout();
            onWindowSizeChanged();
        }
        updateBotButton();
    }

    public void hidePopup(boolean byBackButton) {
        if (isPopupShowing()) {
            if (this.currentPopupContentType == 1 && byBackButton && this.botButtonsMessageObject != null) {
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
            }
            showPopup(0, 0);
            removeGifFromInputField();
        }
    }

    private void removeGifFromInputField() {
        if (!AndroidUtilities.usingHardwareInput && this.messageEditText.getText().toString().equals("@gif ")) {
            this.messageEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
        }
    }

    private void openKeyboardInternal() {
        int i = (AndroidUtilities.usingHardwareInput || this.isPaused) ? 0 : 2;
        showPopup(i, 0);
        this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.messageEditText);
        if (this.isPaused) {
            this.showKeyboardOnResume = true;
        } else if (!AndroidUtilities.usingHardwareInput && !this.keyboardVisible) {
            this.waitingForKeyboardOpen = true;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
            AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100);
        }
    }

    public boolean isEditingMessage() {
        return this.editingMessageObject != null;
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    public boolean isEditingCaption() {
        return this.editingCaption;
    }

    public boolean hasAudioToSend() {
        return this.audioToSendMessageObject != null;
    }

    public void openKeyboard() {
        AndroidUtilities.showKeyboard(this.messageEditText);
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
    }

    public boolean isPopupShowing() {
        return (this.emojiView != null && this.emojiView.getVisibility() == 0) || (this.botKeyboardView != null && this.botKeyboardView.getVisibility() == 0);
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    public void addRecentGif(SearchImage searchImage) {
        if (this.emojiView != null) {
            this.emojiView.addRecentGif(searchImage);
        }
    }

    public void onSizeChanged(int height, boolean isWidthGreater) {
        boolean z = true;
        if (height > AndroidUtilities.dp(50.0f) && this.keyboardVisible) {
            if (isWidthGreater) {
                this.keyboardHeightLand = height;
                ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
            } else {
                this.keyboardHeight = height;
                ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height", this.keyboardHeight).commit();
            }
        }
        if (isPopupShowing()) {
            int newHeight = isWidthGreater ? this.keyboardHeightLand : this.keyboardHeight;
            if (this.currentPopupContentType == 1 && !this.botKeyboardView.isFullSize()) {
                newHeight = Math.min(this.botKeyboardView.getKeyboardHeight(), newHeight);
            }
            View currentView = null;
            if (this.currentPopupContentType == 0) {
                currentView = this.emojiView;
            } else if (this.currentPopupContentType == 1) {
                currentView = this.botKeyboardView;
            }
            if (this.botKeyboardView != null) {
                this.botKeyboardView.setPanelHeight(newHeight);
            }
            LayoutParams layoutParams = (LayoutParams) currentView.getLayoutParams();
            if (!(layoutParams.width == AndroidUtilities.displaySize.x && layoutParams.height == newHeight)) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = newHeight;
                currentView.setLayoutParams(layoutParams);
                if (this.sizeNotifierLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    this.sizeNotifierLayout.requestLayout();
                    onWindowSizeChanged();
                }
            }
        }
        if (this.lastSizeChangeValue1 == height && this.lastSizeChangeValue2 == isWidthGreater) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = height;
        this.lastSizeChangeValue2 = isWidthGreater;
        boolean oldValue = this.keyboardVisible;
        if (height <= 0) {
            z = false;
        }
        this.keyboardVisible = z;
        if (this.keyboardVisible && isPopupShowing()) {
            showPopup(0, this.currentPopupContentType);
        }
        if (!(this.emojiPadding == 0 || this.keyboardVisible || this.keyboardVisible == oldValue || isPopupShowing())) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public int getEmojiHeight() {
        if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            return this.keyboardHeightLand;
        }
        return this.keyboardHeight;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r27, java.lang.Object... r28) {
        /*
        r26 = this;
        r18 = org.telegram.messenger.NotificationCenter.emojiDidLoaded;
        r0 = r27;
        r1 = r18;
        if (r0 != r1) goto L_0x002b;
    L_0x0008:
        r0 = r26;
        r0 = r0.emojiView;
        r18 = r0;
        if (r18 == 0) goto L_0x0019;
    L_0x0010:
        r0 = r26;
        r0 = r0.emojiView;
        r18 = r0;
        r18.invalidateViews();
    L_0x0019:
        r0 = r26;
        r0 = r0.botKeyboardView;
        r18 = r0;
        if (r18 == 0) goto L_0x002a;
    L_0x0021:
        r0 = r26;
        r0 = r0.botKeyboardView;
        r18 = r0;
        r18.invalidateViews();
    L_0x002a:
        return;
    L_0x002b:
        r18 = org.telegram.messenger.NotificationCenter.recordProgressChanged;
        r0 = r27;
        r1 = r18;
        if (r0 != r1) goto L_0x00f3;
    L_0x0033:
        r18 = 0;
        r18 = r28[r18];
        r18 = (java.lang.Long) r18;
        r16 = r18.longValue();
        r18 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r18 = r16 / r18;
        r15 = java.lang.Long.valueOf(r18);
        r18 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r18 = r16 % r18;
        r0 = r18;
        r0 = (int) r0;
        r18 = r0;
        r12 = r18 / 10;
        r18 = "%02d:%02d.%02d";
        r19 = 3;
        r0 = r19;
        r0 = new java.lang.Object[r0];
        r19 = r0;
        r20 = 0;
        r22 = r15.longValue();
        r24 = 60;
        r22 = r22 / r24;
        r21 = java.lang.Long.valueOf(r22);
        r19[r20] = r21;
        r20 = 1;
        r22 = r15.longValue();
        r24 = 60;
        r22 = r22 % r24;
        r21 = java.lang.Long.valueOf(r22);
        r19[r20] = r21;
        r20 = 2;
        r21 = java.lang.Integer.valueOf(r12);
        r19[r20] = r21;
        r14 = java.lang.String.format(r18, r19);
        r0 = r26;
        r0 = r0.lastTimeString;
        r18 = r0;
        if (r18 == 0) goto L_0x009c;
    L_0x008e:
        r0 = r26;
        r0 = r0.lastTimeString;
        r18 = r0;
        r0 = r18;
        r18 = r0.equals(r14);
        if (r18 != 0) goto L_0x00d6;
    L_0x009c:
        r18 = r15.longValue();
        r20 = 5;
        r18 = r18 % r20;
        r20 = 0;
        r18 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r18 != 0) goto L_0x00c3;
    L_0x00aa:
        r18 = org.telegram.messenger.MessagesController.getInstance();
        r0 = r26;
        r0 = r0.dialog_id;
        r20 = r0;
        r19 = 1;
        r22 = 0;
        r0 = r18;
        r1 = r20;
        r3 = r19;
        r4 = r22;
        r0.sendTyping(r1, r3, r4);
    L_0x00c3:
        r0 = r26;
        r0 = r0.recordTimeText;
        r18 = r0;
        if (r18 == 0) goto L_0x00d6;
    L_0x00cb:
        r0 = r26;
        r0 = r0.recordTimeText;
        r18 = r0;
        r0 = r18;
        r0.setText(r14);
    L_0x00d6:
        r0 = r26;
        r0 = r0.recordCircle;
        r18 = r0;
        if (r18 == 0) goto L_0x002a;
    L_0x00de:
        r0 = r26;
        r0 = r0.recordCircle;
        r19 = r0;
        r18 = 1;
        r18 = r28[r18];
        r18 = (java.lang.Double) r18;
        r20 = r18.doubleValue();
        r19.setAmplitude(r20);
        goto L_0x002a;
    L_0x00f3:
        r18 = org.telegram.messenger.NotificationCenter.closeChats;
        r0 = r27;
        r1 = r18;
        if (r0 != r1) goto L_0x011a;
    L_0x00fb:
        r0 = r26;
        r0 = r0.messageEditText;
        r18 = r0;
        if (r18 == 0) goto L_0x002a;
    L_0x0103:
        r0 = r26;
        r0 = r0.messageEditText;
        r18 = r0;
        r18 = r18.isFocused();
        if (r18 == 0) goto L_0x002a;
    L_0x010f:
        r0 = r26;
        r0 = r0.messageEditText;
        r18 = r0;
        org.telegram.messenger.AndroidUtilities.hideKeyboard(r18);
        goto L_0x002a;
    L_0x011a:
        r18 = org.telegram.messenger.NotificationCenter.recordStartError;
        r0 = r27;
        r1 = r18;
        if (r0 == r1) goto L_0x012a;
    L_0x0122:
        r18 = org.telegram.messenger.NotificationCenter.recordStopped;
        r0 = r27;
        r1 = r18;
        if (r0 != r1) goto L_0x0158;
    L_0x012a:
        r0 = r26;
        r0 = r0.recordingAudio;
        r18 = r0;
        if (r18 == 0) goto L_0x002a;
    L_0x0132:
        r18 = org.telegram.messenger.MessagesController.getInstance();
        r0 = r26;
        r0 = r0.dialog_id;
        r20 = r0;
        r19 = 2;
        r22 = 0;
        r0 = r18;
        r1 = r20;
        r3 = r19;
        r4 = r22;
        r0.sendTyping(r1, r3, r4);
        r18 = 0;
        r0 = r18;
        r1 = r26;
        r1.recordingAudio = r0;
        r26.updateAudioRecordIntefrace();
        goto L_0x002a;
    L_0x0158:
        r18 = org.telegram.messenger.NotificationCenter.recordStarted;
        r0 = r27;
        r1 = r18;
        if (r0 != r1) goto L_0x0175;
    L_0x0160:
        r0 = r26;
        r0 = r0.recordingAudio;
        r18 = r0;
        if (r18 != 0) goto L_0x002a;
    L_0x0168:
        r18 = 1;
        r0 = r18;
        r1 = r26;
        r1.recordingAudio = r0;
        r26.updateAudioRecordIntefrace();
        goto L_0x002a;
    L_0x0175:
        r18 = org.telegram.messenger.NotificationCenter.audioDidSent;
        r0 = r27;
        r1 = r18;
        if (r0 != r1) goto L_0x0334;
    L_0x017d:
        r18 = 0;
        r18 = r28[r18];
        r18 = (org.telegram.tgnet.TLRPC.TL_document) r18;
        r0 = r18;
        r1 = r26;
        r1.audioToSend = r0;
        r18 = 1;
        r18 = r28[r18];
        r18 = (java.lang.String) r18;
        r0 = r18;
        r1 = r26;
        r1.audioToSendPath = r0;
        r0 = r26;
        r0 = r0.audioToSend;
        r18 = r0;
        if (r18 == 0) goto L_0x031f;
    L_0x019d:
        r0 = r26;
        r0 = r0.recordedAudioPanel;
        r18 = r0;
        if (r18 == 0) goto L_0x002a;
    L_0x01a5:
        r10 = new org.telegram.tgnet.TLRPC$TL_message;
        r10.<init>();
        r18 = 1;
        r0 = r18;
        r10.out = r0;
        r18 = 0;
        r0 = r18;
        r10.id = r0;
        r18 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r18.<init>();
        r0 = r18;
        r10.to_id = r0;
        r0 = r10.to_id;
        r18 = r0;
        r19 = org.telegram.messenger.UserConfig.getClientUserId();
        r0 = r19;
        r10.from_id = r0;
        r0 = r19;
        r1 = r18;
        r1.user_id = r0;
        r18 = java.lang.System.currentTimeMillis();
        r20 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r18 = r18 / r20;
        r0 = r18;
        r0 = (int) r0;
        r18 = r0;
        r0 = r18;
        r10.date = r0;
        r18 = "-1";
        r0 = r18;
        r10.message = r0;
        r0 = r26;
        r0 = r0.audioToSendPath;
        r18 = r0;
        r0 = r18;
        r10.attachPath = r0;
        r18 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
        r18.<init>();
        r0 = r18;
        r10.media = r0;
        r0 = r10.media;
        r18 = r0;
        r0 = r26;
        r0 = r0.audioToSend;
        r19 = r0;
        r0 = r19;
        r1 = r18;
        r1.document = r0;
        r0 = r10.flags;
        r18 = r0;
        r0 = r18;
        r0 = r0 | 768;
        r18 = r0;
        r0 = r18;
        r10.flags = r0;
        r18 = new org.telegram.messenger.MessageObject;
        r19 = 0;
        r20 = 0;
        r0 = r18;
        r1 = r19;
        r2 = r20;
        r0.<init>(r10, r1, r2);
        r0 = r18;
        r1 = r26;
        r1.audioToSendMessageObject = r0;
        r0 = r26;
        r0 = r0.recordedAudioPanel;
        r18 = r0;
        r19 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r18.setAlpha(r19);
        r0 = r26;
        r0 = r0.recordedAudioPanel;
        r18 = r0;
        r19 = 0;
        r18.setVisibility(r19);
        r8 = 0;
        r6 = 0;
    L_0x0246:
        r0 = r26;
        r0 = r0.audioToSend;
        r18 = r0;
        r0 = r18;
        r0 = r0.attributes;
        r18 = r0;
        r18 = r18.size();
        r0 = r18;
        if (r6 >= r0) goto L_0x0276;
    L_0x025a:
        r0 = r26;
        r0 = r0.audioToSend;
        r18 = r0;
        r0 = r18;
        r0 = r0.attributes;
        r18 = r0;
        r0 = r18;
        r7 = r0.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r7;
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
        r18 = r0;
        if (r18 == 0) goto L_0x0317;
    L_0x0274:
        r8 = r7.duration;
    L_0x0276:
        r6 = 0;
    L_0x0277:
        r0 = r26;
        r0 = r0.audioToSend;
        r18 = r0;
        r0 = r18;
        r0 = r0.attributes;
        r18 = r0;
        r18 = r18.size();
        r0 = r18;
        if (r6 >= r0) goto L_0x02d5;
    L_0x028b:
        r0 = r26;
        r0 = r0.audioToSend;
        r18 = r0;
        r0 = r18;
        r0 = r0.attributes;
        r18 = r0;
        r0 = r18;
        r7 = r0.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r7;
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
        r18 = r0;
        if (r18 == 0) goto L_0x031b;
    L_0x02a5:
        r0 = r7.waveform;
        r18 = r0;
        if (r18 == 0) goto L_0x02b6;
    L_0x02ab:
        r0 = r7.waveform;
        r18 = r0;
        r0 = r18;
        r0 = r0.length;
        r18 = r0;
        if (r18 != 0) goto L_0x02c8;
    L_0x02b6:
        r18 = org.telegram.messenger.MediaController.getInstance();
        r0 = r26;
        r0 = r0.audioToSendPath;
        r19 = r0;
        r18 = r18.getWaveform(r19);
        r0 = r18;
        r7.waveform = r0;
    L_0x02c8:
        r0 = r26;
        r0 = r0.recordedAudioSeekBar;
        r18 = r0;
        r0 = r7.waveform;
        r19 = r0;
        r18.setWaveform(r19);
    L_0x02d5:
        r0 = r26;
        r0 = r0.recordedAudioTimeTextView;
        r18 = r0;
        r19 = "%d:%02d";
        r20 = 2;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r21 = 0;
        r22 = r8 / 60;
        r22 = java.lang.Integer.valueOf(r22);
        r20[r21] = r22;
        r21 = 1;
        r22 = r8 % 60;
        r22 = java.lang.Integer.valueOf(r22);
        r20[r21] = r22;
        r19 = java.lang.String.format(r19, r20);
        r18.setText(r19);
        r26.closeKeyboard();
        r18 = 0;
        r0 = r26;
        r1 = r18;
        r0.hidePopup(r1);
        r18 = 0;
        r0 = r26;
        r1 = r18;
        r0.checkSendButton(r1);
        goto L_0x002a;
    L_0x0317:
        r6 = r6 + 1;
        goto L_0x0246;
    L_0x031b:
        r6 = r6 + 1;
        goto L_0x0277;
    L_0x031f:
        r0 = r26;
        r0 = r0.delegate;
        r18 = r0;
        if (r18 == 0) goto L_0x002a;
    L_0x0327:
        r0 = r26;
        r0 = r0.delegate;
        r18 = r0;
        r19 = 0;
        r18.onMessageSend(r19);
        goto L_0x002a;
    L_0x0334:
        r18 = org.telegram.messenger.NotificationCenter.audioRouteChanged;
        r0 = r27;
        r1 = r18;
        if (r0 != r1) goto L_0x0364;
    L_0x033c:
        r0 = r26;
        r0 = r0.parentActivity;
        r18 = r0;
        if (r18 == 0) goto L_0x002a;
    L_0x0344:
        r18 = 0;
        r18 = r28[r18];
        r18 = (java.lang.Boolean) r18;
        r9 = r18.booleanValue();
        r0 = r26;
        r0 = r0.parentActivity;
        r19 = r0;
        if (r9 == 0) goto L_0x0361;
    L_0x0356:
        r18 = 0;
    L_0x0358:
        r0 = r19;
        r1 = r18;
        r0.setVolumeControlStream(r1);
        goto L_0x002a;
    L_0x0361:
        r18 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        goto L_0x0358;
    L_0x0364:
        r18 = org.telegram.messenger.NotificationCenter.audioDidReset;
        r0 = r27;
        r1 = r18;
        if (r0 != r1) goto L_0x039d;
    L_0x036c:
        r0 = r26;
        r0 = r0.audioToSendMessageObject;
        r18 = r0;
        if (r18 == 0) goto L_0x002a;
    L_0x0374:
        r18 = org.telegram.messenger.MediaController.getInstance();
        r0 = r26;
        r0 = r0.audioToSendMessageObject;
        r19 = r0;
        r18 = r18.isPlayingAudio(r19);
        if (r18 != 0) goto L_0x002a;
    L_0x0384:
        r0 = r26;
        r0 = r0.recordedAudioPlayButton;
        r18 = r0;
        r19 = 2130837945; // 0x7f0201b9 float:1.7280858E38 double:1.0527738255E-314;
        r18.setImageResource(r19);
        r0 = r26;
        r0 = r0.recordedAudioSeekBar;
        r18 = r0;
        r19 = 0;
        r18.setProgress(r19);
        goto L_0x002a;
    L_0x039d:
        r18 = org.telegram.messenger.NotificationCenter.audioProgressDidChanged;
        r0 = r27;
        r1 = r18;
        if (r0 != r1) goto L_0x002a;
    L_0x03a5:
        r18 = 0;
        r11 = r28[r18];
        r11 = (java.lang.Integer) r11;
        r0 = r26;
        r0 = r0.audioToSendMessageObject;
        r18 = r0;
        if (r18 == 0) goto L_0x002a;
    L_0x03b3:
        r18 = org.telegram.messenger.MediaController.getInstance();
        r0 = r26;
        r0 = r0.audioToSendMessageObject;
        r19 = r0;
        r18 = r18.isPlayingAudio(r19);
        if (r18 == 0) goto L_0x002a;
    L_0x03c3:
        r18 = org.telegram.messenger.MediaController.getInstance();
        r13 = r18.getPlayingMessageObject();
        r0 = r26;
        r0 = r0.audioToSendMessageObject;
        r18 = r0;
        r0 = r13.audioProgress;
        r19 = r0;
        r0 = r19;
        r1 = r18;
        r1.audioProgress = r0;
        r0 = r26;
        r0 = r0.audioToSendMessageObject;
        r18 = r0;
        r0 = r13.audioProgressSec;
        r19 = r0;
        r0 = r19;
        r1 = r18;
        r1.audioProgressSec = r0;
        r0 = r26;
        r0 = r0.recordedAudioSeekBar;
        r18 = r0;
        r18 = r18.isDragging();
        if (r18 != 0) goto L_0x002a;
    L_0x03f7:
        r0 = r26;
        r0 = r0.recordedAudioSeekBar;
        r18 = r0;
        r0 = r26;
        r0 = r0.audioToSendMessageObject;
        r19 = r0;
        r0 = r19;
        r0 = r0.audioProgress;
        r19 = r0;
        r18.setProgress(r19);
        goto L_0x002a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatActivityEnterView.didReceivedNotification(int, java.lang.Object[]):void");
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2 && this.pendingLocationButton != null) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                SendMessagesHelper.getInstance().sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
            }
            this.pendingLocationButton = null;
            this.pendingMessageObject = null;
        }
    }
}
