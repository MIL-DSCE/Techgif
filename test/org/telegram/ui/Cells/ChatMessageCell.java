package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.ViewStructure;
import com.google.android.c2dm.C2DMessaging;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import net.hockeyapp.android.UpdateFragment;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.TextLayoutBlock;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class ChatMessageCell extends BaseCell implements SeekBarDelegate, ImageReceiverDelegate, FileDownloadProgressListener {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static TextPaint audioPerformerPaint;
    private static TextPaint audioTimePaint;
    private static TextPaint audioTitlePaint;
    private static TextPaint botButtonPaint;
    private static Paint botProgressPaint;
    private static TextPaint contactNamePaint;
    private static TextPaint contactPhonePaint;
    private static Paint deleteProgressPaint;
    private static Paint docBackPaint;
    private static TextPaint docNamePaint;
    private static TextPaint durationPaint;
    private static TextPaint forwardNamePaint;
    private static TextPaint infoPaint;
    private static TextPaint locationAddressPaint;
    private static TextPaint locationTitlePaint;
    private static TextPaint namePaint;
    private static Paint replyLinePaint;
    private static TextPaint replyNamePaint;
    private static TextPaint replyTextPaint;
    private static TextPaint timePaint;
    private static Paint urlPaint;
    private static Paint urlSelectionPaint;
    private int TAG;
    private boolean allowAssistant;
    private StaticLayout authorLayout;
    private int authorX;
    private int availableTimeWidth;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private boolean avatarPressed;
    private int backgroundWidth;
    private ArrayList<BotButton> botButtons;
    private HashMap<String, BotButton> botButtonsByData;
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private boolean cancelLoading;
    private int captionHeight;
    private StaticLayout captionLayout;
    private int captionX;
    private int captionY;
    private AvatarDrawable contactAvatarDrawable;
    private Drawable currentBackgroundDrawable;
    private Chat currentChat;
    private Chat currentForwardChannel;
    private String currentForwardNameString;
    private User currentForwardUser;
    private MessageObject currentMessageObject;
    private String currentNameString;
    private FileLocation currentPhoto;
    private String currentPhotoFilter;
    private String currentPhotoFilterThumb;
    private PhotoSize currentPhotoObject;
    private PhotoSize currentPhotoObjectThumb;
    private FileLocation currentReplyPhoto;
    private String currentTimeString;
    private String currentUrl;
    private User currentUser;
    private User currentViaBotUser;
    private String currentViewsString;
    private ChatMessageCellDelegate delegate;
    private RectF deleteProgressRect;
    private StaticLayout descriptionLayout;
    private int descriptionX;
    private int descriptionY;
    private boolean disallowLongPress;
    private StaticLayout docTitleLayout;
    private int docTitleOffsetX;
    private Document documentAttach;
    private int documentAttachType;
    private boolean drawBackground;
    private boolean drawForwardedName;
    private boolean drawImageButton;
    private boolean drawName;
    private boolean drawNameLayout;
    private boolean drawPhotoImage;
    private boolean drawShareButton;
    private boolean drawTime;
    private StaticLayout durationLayout;
    private int durationWidth;
    private int firstVisibleBlockNum;
    private boolean forwardBotPressed;
    private boolean forwardName;
    private float[] forwardNameOffsetX;
    private boolean forwardNamePressed;
    private int forwardNameX;
    private int forwardNameY;
    private StaticLayout[] forwardedNameLayout;
    private int forwardedNameWidth;
    private boolean hasLinkPreview;
    private boolean imagePressed;
    private StaticLayout infoLayout;
    private int infoWidth;
    private boolean isAvatarVisible;
    public boolean isChat;
    private boolean isCheckPressed;
    private boolean isHighlighted;
    private boolean isPressed;
    private boolean isSmallImage;
    private int keyboardHeight;
    private int lastDeleteDate;
    private int lastSendState;
    private String lastTimeString;
    private int lastViewsCount;
    private int lastVisibleBlockNum;
    private int layoutHeight;
    private int layoutWidth;
    private int linkBlockNum;
    private int linkPreviewHeight;
    private boolean linkPreviewPressed;
    private int linkSelectionBlockNum;
    private boolean mediaBackground;
    private int mediaOffsetY;
    private StaticLayout nameLayout;
    private float nameOffsetX;
    private int nameWidth;
    private float nameX;
    private float nameY;
    private int namesOffset;
    private boolean needNewVisiblePart;
    private boolean needReplyImage;
    private boolean otherPressed;
    private int otherX;
    private int otherY;
    private StaticLayout performerLayout;
    private int performerX;
    private ImageReceiver photoImage;
    private boolean photoNotSet;
    private int pressedBotButton;
    private ClickableSpan pressedLink;
    private int pressedLinkType;
    private RadialProgress radialProgress;
    private RectF rect;
    private ImageReceiver replyImageReceiver;
    private StaticLayout replyNameLayout;
    private float replyNameOffset;
    private int replyNameWidth;
    private boolean replyPressed;
    private int replyStartX;
    private int replyStartY;
    private StaticLayout replyTextLayout;
    private float replyTextOffset;
    private int replyTextWidth;
    private Rect scrollRect;
    private SeekBar seekBar;
    private SeekBarWaveform seekBarWaveform;
    private int seekBarX;
    private int seekBarY;
    private boolean sharePressed;
    private int shareStartX;
    private int shareStartY;
    private StaticLayout siteNameLayout;
    private StaticLayout songLayout;
    private int songX;
    private int substractBackgroundHeight;
    private int textX;
    private int textY;
    private int timeAudioX;
    private StaticLayout timeLayout;
    private int timeTextWidth;
    private int timeWidth;
    private int timeWidthAudio;
    private int timeX;
    private StaticLayout titleLayout;
    private int titleX;
    private int totalHeight;
    private int totalVisibleBlocksCount;
    private ArrayList<LinkPath> urlPath;
    private ArrayList<LinkPath> urlPathCache;
    private ArrayList<LinkPath> urlPathSelection;
    private boolean useSeekBarWaweform;
    private int viaNameWidth;
    private int viaWidth;
    private StaticLayout videoInfoLayout;
    private StaticLayout viewsLayout;
    private int viewsTextWidth;
    private boolean wasLayout;
    private int widthForButtons;

    private class BotButton {
        private int angle;
        private KeyboardButton button;
        private int height;
        private long lastUpdateTime;
        private float progressAlpha;
        private StaticLayout title;
        private int width;
        private int f11x;
        private int f12y;

        private BotButton() {
        }

        static /* synthetic */ float access$616(BotButton x0, float x1) {
            float f = x0.progressAlpha + x1;
            x0.progressAlpha = f;
            return f;
        }

        static /* synthetic */ float access$624(BotButton x0, float x1) {
            float f = x0.progressAlpha - x1;
            x0.progressAlpha = f;
            return f;
        }

        static /* synthetic */ int access$716(BotButton x0, float x1) {
            int i = (int) (((float) x0.angle) + x1);
            x0.angle = i;
            return i;
        }

        static /* synthetic */ int access$720(BotButton x0, int x1) {
            int i = x0.angle - x1;
            x0.angle = i;
            return i;
        }
    }

    public interface ChatMessageCellDelegate {
        boolean canPerformActions();

        void didLongPressed(ChatMessageCell chatMessageCell);

        void didPressedBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton);

        void didPressedCancelSendButton(ChatMessageCell chatMessageCell);

        void didPressedChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i);

        void didPressedImage(ChatMessageCell chatMessageCell);

        void didPressedOther(ChatMessageCell chatMessageCell);

        void didPressedReplyMessage(ChatMessageCell chatMessageCell, int i);

        void didPressedShare(ChatMessageCell chatMessageCell);

        void didPressedUrl(MessageObject messageObject, ClickableSpan clickableSpan, boolean z);

        void didPressedUserAvatar(ChatMessageCell chatMessageCell, User user);

        void didPressedViaBot(ChatMessageCell chatMessageCell, String str);

        void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2);

        boolean needPlayAudio(MessageObject messageObject);
    }

    public ChatMessageCell(Context context) {
        super(context);
        this.scrollRect = new Rect();
        this.deleteProgressRect = new RectF();
        this.rect = new RectF();
        this.urlPathCache = new ArrayList();
        this.urlPath = new ArrayList();
        this.urlPathSelection = new ArrayList();
        this.botButtons = new ArrayList();
        this.botButtonsByData = new HashMap();
        this.isCheckPressed = true;
        this.drawBackground = true;
        this.backgroundWidth = 100;
        this.forwardedNameLayout = new StaticLayout[DOCUMENT_ATTACH_TYPE_GIF];
        this.forwardNameOffsetX = new float[DOCUMENT_ATTACH_TYPE_GIF];
        this.drawTime = true;
        if (infoPaint == null) {
            infoPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            infoPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            docNamePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            docNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            docNamePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            docBackPaint = new Paint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            deleteProgressPaint = new Paint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            deleteProgressPaint.setColor(Theme.MSG_SECRET_TIME_TEXT_COLOR);
            botProgressPaint = new Paint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            botProgressPaint.setColor(Theme.MSG_BOT_PROGRESS_COLOR);
            botProgressPaint.setStrokeCap(Cap.ROUND);
            botProgressPaint.setStyle(Style.STROKE);
            botProgressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            locationTitlePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            locationTitlePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            locationTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            locationAddressPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            locationAddressPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            urlPaint = new Paint();
            urlPaint.setColor(Theme.MSG_LINK_SELECT_BACKGROUND_COLOR);
            urlSelectionPaint = new Paint();
            urlSelectionPaint.setColor(Theme.MSG_TEXT_SELECT_BACKGROUND_COLOR);
            audioTimePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            audioTimePaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            audioTitlePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            audioTitlePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
            audioTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            audioPerformerPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            audioPerformerPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            botButtonPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            botButtonPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            botButtonPaint.setColor(Theme.MSG_BOT_BUTTON_TEXT_COLOR);
            botButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            contactNamePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            contactNamePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            contactNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            contactPhonePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            contactPhonePaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            durationPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            durationPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            durationPaint.setColor(Theme.MSG_WEB_PREVIEW_DURATION_TEXT_COLOR);
            timePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            timePaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            namePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            namePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            forwardNamePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            forwardNamePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            replyNamePaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            replyNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            replyNamePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            replyTextPaint = new TextPaint(DOCUMENT_ATTACH_TYPE_DOCUMENT);
            replyTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            replyTextPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
            replyLinePaint = new Paint();
        }
        this.avatarImage = new ImageReceiver(this);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.replyImageReceiver = new ImageReceiver(this);
        this.TAG = MediaController.getInstance().generateObserverTag();
        this.contactAvatarDrawable = new AvatarDrawable();
        this.photoImage = new ImageReceiver(this);
        this.photoImage.setDelegate(this);
        this.radialProgress = new RadialProgress(this);
        this.seekBar = new SeekBar(context);
        this.seekBar.setDelegate(this);
        this.seekBarWaveform = new SeekBarWaveform(context);
        this.seekBarWaveform.setDelegate(this);
        this.seekBarWaveform.setParentView(this);
        this.radialProgress = new RadialProgress(this);
    }

    private void resetPressedLink(int type) {
        if (this.pressedLink == null) {
            return;
        }
        if (this.pressedLinkType == type || type == -1) {
            resetUrlPaths(false);
            this.pressedLink = null;
            this.pressedLinkType = -1;
            invalidate();
        }
    }

    private void resetUrlPaths(boolean text) {
        if (text) {
            if (!this.urlPathSelection.isEmpty()) {
                this.urlPathCache.addAll(this.urlPathSelection);
                this.urlPathSelection.clear();
            }
        } else if (!this.urlPath.isEmpty()) {
            this.urlPathCache.addAll(this.urlPath);
            this.urlPath.clear();
        }
    }

    private LinkPath obtainNewUrlPath(boolean text) {
        LinkPath linkPath;
        if (this.urlPathCache.isEmpty()) {
            linkPath = new LinkPath();
        } else {
            linkPath = (LinkPath) this.urlPathCache.get(DOCUMENT_ATTACH_TYPE_NONE);
            this.urlPathCache.remove(DOCUMENT_ATTACH_TYPE_NONE);
        }
        if (text) {
            this.urlPathSelection.add(linkPath);
        } else {
            this.urlPath.add(linkPath);
        }
        return linkPath;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkTextBlockMotionEvent(android.view.MotionEvent r27) {
        /*
        r26 = this;
        r0 = r26;
        r0 = r0.currentMessageObject;
        r22 = r0;
        r0 = r22;
        r0 = r0.type;
        r22 = r0;
        if (r22 != 0) goto L_0x0042;
    L_0x000e:
        r0 = r26;
        r0 = r0.currentMessageObject;
        r22 = r0;
        r0 = r22;
        r0 = r0.textLayoutBlocks;
        r22 = r0;
        if (r22 == 0) goto L_0x0042;
    L_0x001c:
        r0 = r26;
        r0 = r0.currentMessageObject;
        r22 = r0;
        r0 = r22;
        r0 = r0.textLayoutBlocks;
        r22 = r0;
        r22 = r22.isEmpty();
        if (r22 != 0) goto L_0x0042;
    L_0x002e:
        r0 = r26;
        r0 = r0.currentMessageObject;
        r22 = r0;
        r0 = r22;
        r0 = r0.messageText;
        r22 = r0;
        r0 = r22;
        r0 = r0 instanceof android.text.Spannable;
        r22 = r0;
        if (r22 != 0) goto L_0x0045;
    L_0x0042:
        r22 = 0;
    L_0x0044:
        return r22;
    L_0x0045:
        r22 = r27.getAction();
        if (r22 == 0) goto L_0x0065;
    L_0x004b:
        r22 = r27.getAction();
        r23 = 1;
        r0 = r22;
        r1 = r23;
        if (r0 != r1) goto L_0x03db;
    L_0x0057:
        r0 = r26;
        r0 = r0.pressedLinkType;
        r22 = r0;
        r23 = 1;
        r0 = r22;
        r1 = r23;
        if (r0 != r1) goto L_0x03db;
    L_0x0065:
        r22 = r27.getX();
        r0 = r22;
        r0 = (int) r0;
        r20 = r0;
        r22 = r27.getY();
        r0 = r22;
        r0 = (int) r0;
        r21 = r0;
        r0 = r26;
        r0 = r0.textX;
        r22 = r0;
        r0 = r20;
        r1 = r22;
        if (r0 < r1) goto L_0x0413;
    L_0x0083:
        r0 = r26;
        r0 = r0.textY;
        r22 = r0;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x0413;
    L_0x008f:
        r0 = r26;
        r0 = r0.textX;
        r22 = r0;
        r0 = r26;
        r0 = r0.currentMessageObject;
        r23 = r0;
        r0 = r23;
        r0 = r0.textWidth;
        r23 = r0;
        r22 = r22 + r23;
        r0 = r20;
        r1 = r22;
        if (r0 > r1) goto L_0x0413;
    L_0x00a9:
        r0 = r26;
        r0 = r0.textY;
        r22 = r0;
        r0 = r26;
        r0 = r0.currentMessageObject;
        r23 = r0;
        r0 = r23;
        r0 = r0.textHeight;
        r23 = r0;
        r22 = r22 + r23;
        r0 = r21;
        r1 = r22;
        if (r0 > r1) goto L_0x0413;
    L_0x00c3:
        r0 = r26;
        r0 = r0.textY;
        r22 = r0;
        r21 = r21 - r22;
        r6 = 0;
        r4 = 0;
    L_0x00cd:
        r0 = r26;
        r0 = r0.currentMessageObject;
        r22 = r0;
        r0 = r22;
        r0 = r0.textLayoutBlocks;
        r22 = r0;
        r22 = r22.size();
        r0 = r22;
        if (r4 >= r0) goto L_0x0104;
    L_0x00e1:
        r0 = r26;
        r0 = r0.currentMessageObject;
        r22 = r0;
        r0 = r22;
        r0 = r0.textLayoutBlocks;
        r22 = r0;
        r0 = r22;
        r22 = r0.get(r4);
        r22 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r22;
        r0 = r22;
        r0 = r0.textYOffset;
        r22 = r0;
        r0 = r21;
        r0 = (float) r0;
        r23 = r0;
        r22 = (r22 > r23 ? 1 : (r22 == r23 ? 0 : -1));
        if (r22 <= 0) goto L_0x0319;
    L_0x0104:
        r0 = r26;
        r0 = r0.currentMessageObject;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r0 = r22;
        r0 = r0.textLayoutBlocks;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r0 = r22;
        r5 = r0.get(r6);	 Catch:{ Exception -> 0x03d3 }
        r5 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r5;	 Catch:{ Exception -> 0x03d3 }
        r0 = r26;
        r0 = r0.textX;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r0 = r5.textXOffset;	 Catch:{ Exception -> 0x03d3 }
        r23 = r0;
        r0 = r23;
        r0 = (double) r0;	 Catch:{ Exception -> 0x03d3 }
        r24 = r0;
        r24 = java.lang.Math.ceil(r24);	 Catch:{ Exception -> 0x03d3 }
        r0 = r24;
        r0 = (int) r0;	 Catch:{ Exception -> 0x03d3 }
        r23 = r0;
        r22 = r22 - r23;
        r20 = r20 - r22;
        r0 = r21;
        r0 = (float) r0;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r0 = r5.textYOffset;	 Catch:{ Exception -> 0x03d3 }
        r23 = r0;
        r22 = r22 - r23;
        r0 = r22;
        r0 = (int) r0;	 Catch:{ Exception -> 0x03d3 }
        r21 = r0;
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r0 = r22;
        r1 = r21;
        r13 = r0.getLineForVertical(r1);	 Catch:{ Exception -> 0x03d3 }
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r0 = r20;
        r0 = (float) r0;	 Catch:{ Exception -> 0x03d3 }
        r23 = r0;
        r0 = r22;
        r1 = r23;
        r22 = r0.getOffsetForHorizontal(r13, r1);	 Catch:{ Exception -> 0x03d3 }
        r0 = r5.charactersOffset;	 Catch:{ Exception -> 0x03d3 }
        r23 = r0;
        r17 = r22 + r23;
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r0 = r22;
        r11 = r0.getLineLeft(r13);	 Catch:{ Exception -> 0x03d3 }
        r0 = r20;
        r0 = (float) r0;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r22 = (r11 > r22 ? 1 : (r11 == r22 ? 0 : -1));
        if (r22 > 0) goto L_0x03db;
    L_0x017a:
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r0 = r22;
        r22 = r0.getLineWidth(r13);	 Catch:{ Exception -> 0x03d3 }
        r22 = r22 + r11;
        r0 = r20;
        r0 = (float) r0;	 Catch:{ Exception -> 0x03d3 }
        r23 = r0;
        r22 = (r22 > r23 ? 1 : (r22 == r23 ? 0 : -1));
        if (r22 < 0) goto L_0x03db;
    L_0x018f:
        r0 = r26;
        r0 = r0.currentMessageObject;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r0 = r22;
        r7 = r0.messageText;	 Catch:{ Exception -> 0x03d3 }
        r7 = (android.text.Spannable) r7;	 Catch:{ Exception -> 0x03d3 }
        r22 = android.text.style.ClickableSpan.class;
        r0 = r17;
        r1 = r17;
        r2 = r22;
        r14 = r7.getSpans(r0, r1, r2);	 Catch:{ Exception -> 0x03d3 }
        r14 = (android.text.style.ClickableSpan[]) r14;	 Catch:{ Exception -> 0x03d3 }
        r10 = 0;
        r0 = r14.length;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        if (r22 == 0) goto L_0x01c4;
    L_0x01af:
        r0 = r14.length;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        if (r22 == 0) goto L_0x01c5;
    L_0x01b4:
        r22 = 0;
        r22 = r14[r22];	 Catch:{ Exception -> 0x03d3 }
        r0 = r22;
        r0 = r0 instanceof org.telegram.ui.Components.URLSpanBotCommand;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        if (r22 == 0) goto L_0x01c5;
    L_0x01c0:
        r22 = org.telegram.ui.Components.URLSpanBotCommand.enabled;	 Catch:{ Exception -> 0x03d3 }
        if (r22 != 0) goto L_0x01c5;
    L_0x01c4:
        r10 = 1;
    L_0x01c5:
        if (r10 != 0) goto L_0x03db;
    L_0x01c7:
        r22 = r27.getAction();	 Catch:{ Exception -> 0x03d3 }
        if (r22 != 0) goto L_0x03df;
    L_0x01cd:
        r22 = 0;
        r22 = r14[r22];	 Catch:{ Exception -> 0x03d3 }
        r0 = r22;
        r1 = r26;
        r1.pressedLink = r0;	 Catch:{ Exception -> 0x03d3 }
        r0 = r26;
        r0.linkBlockNum = r6;	 Catch:{ Exception -> 0x03d3 }
        r22 = 1;
        r0 = r22;
        r1 = r26;
        r1.pressedLinkType = r0;	 Catch:{ Exception -> 0x03d3 }
        r22 = 0;
        r0 = r26;
        r1 = r22;
        r0.resetUrlPaths(r1);	 Catch:{ Exception -> 0x03d3 }
        r22 = 0;
        r0 = r26;
        r1 = r22;
        r18 = r0.obtainNewUrlPath(r1);	 Catch:{ Exception -> 0x03c9 }
        r0 = r26;
        r0 = r0.pressedLink;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r22;
        r22 = r7.getSpanStart(r0);	 Catch:{ Exception -> 0x03c9 }
        r0 = r5.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r23 = r0;
        r19 = r22 - r23;
        r0 = r26;
        r0 = r0.pressedLink;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r22;
        r9 = r7.getSpanEnd(r0);	 Catch:{ Exception -> 0x03c9 }
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r22 = r22.getText();	 Catch:{ Exception -> 0x03c9 }
        r12 = r22.length();	 Catch:{ Exception -> 0x03c9 }
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r23 = 0;
        r0 = r18;
        r1 = r22;
        r2 = r19;
        r3 = r23;
        r0.setCurrentLayout(r1, r2, r3);	 Catch:{ Exception -> 0x03c9 }
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r5.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r23 = r0;
        r23 = r9 - r23;
        r0 = r22;
        r1 = r19;
        r2 = r23;
        r3 = r18;
        r0.getSelectionPath(r1, r2, r3);	 Catch:{ Exception -> 0x03c9 }
        r0 = r5.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r22 = r22 + r12;
        r0 = r22;
        if (r9 < r0) goto L_0x02b5;
    L_0x0250:
        r4 = r6 + 1;
    L_0x0252:
        r0 = r26;
        r0 = r0.currentMessageObject;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r22;
        r0 = r0.textLayoutBlocks;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r22 = r22.size();	 Catch:{ Exception -> 0x03c9 }
        r0 = r22;
        if (r4 >= r0) goto L_0x02b5;
    L_0x0266:
        r0 = r26;
        r0 = r0.currentMessageObject;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r22;
        r0 = r0.textLayoutBlocks;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r22;
        r15 = r0.get(r4);	 Catch:{ Exception -> 0x03c9 }
        r15 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r15;	 Catch:{ Exception -> 0x03c9 }
        r0 = r15.textLayout;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r22 = r22.getText();	 Catch:{ Exception -> 0x03c9 }
        r12 = r22.length();	 Catch:{ Exception -> 0x03c9 }
        r0 = r15.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r15.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r23 = r0;
        r24 = android.text.style.ClickableSpan.class;
        r0 = r22;
        r1 = r23;
        r2 = r24;
        r16 = r7.getSpans(r0, r1, r2);	 Catch:{ Exception -> 0x03c9 }
        r16 = (android.text.style.ClickableSpan[]) r16;	 Catch:{ Exception -> 0x03c9 }
        if (r16 == 0) goto L_0x02b5;
    L_0x029e:
        r0 = r16;
        r0 = r0.length;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        if (r22 == 0) goto L_0x02b5;
    L_0x02a5:
        r22 = 0;
        r22 = r16[r22];	 Catch:{ Exception -> 0x03c9 }
        r0 = r26;
        r0 = r0.pressedLink;	 Catch:{ Exception -> 0x03c9 }
        r23 = r0;
        r0 = r22;
        r1 = r23;
        if (r0 == r1) goto L_0x031e;
    L_0x02b5:
        if (r19 >= 0) goto L_0x0312;
    L_0x02b7:
        r4 = r6 + -1;
    L_0x02b9:
        if (r4 < 0) goto L_0x0312;
    L_0x02bb:
        r0 = r26;
        r0 = r0.currentMessageObject;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r22;
        r0 = r0.textLayoutBlocks;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r22;
        r15 = r0.get(r4);	 Catch:{ Exception -> 0x03c9 }
        r15 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r15;	 Catch:{ Exception -> 0x03c9 }
        r0 = r15.textLayout;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r22 = r22.getText();	 Catch:{ Exception -> 0x03c9 }
        r12 = r22.length();	 Catch:{ Exception -> 0x03c9 }
        r0 = r15.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r22 = r22 + r12;
        r22 = r22 + -1;
        r0 = r15.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r23 = r0;
        r23 = r23 + r12;
        r23 = r23 + -1;
        r24 = android.text.style.ClickableSpan.class;
        r0 = r22;
        r1 = r23;
        r2 = r24;
        r16 = r7.getSpans(r0, r1, r2);	 Catch:{ Exception -> 0x03c9 }
        r16 = (android.text.style.ClickableSpan[]) r16;	 Catch:{ Exception -> 0x03c9 }
        if (r16 == 0) goto L_0x0312;
    L_0x02fb:
        r0 = r16;
        r0 = r0.length;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        if (r22 == 0) goto L_0x0312;
    L_0x0302:
        r22 = 0;
        r22 = r16[r22];	 Catch:{ Exception -> 0x03c9 }
        r0 = r26;
        r0 = r0.pressedLink;	 Catch:{ Exception -> 0x03c9 }
        r23 = r0;
        r0 = r22;
        r1 = r23;
        if (r0 == r1) goto L_0x0369;
    L_0x0312:
        r26.invalidate();	 Catch:{ Exception -> 0x03d3 }
        r22 = 1;
        goto L_0x0044;
    L_0x0319:
        r6 = r4;
        r4 = r4 + 1;
        goto L_0x00cd;
    L_0x031e:
        r22 = 0;
        r0 = r26;
        r1 = r22;
        r18 = r0.obtainNewUrlPath(r1);	 Catch:{ Exception -> 0x03c9 }
        r0 = r15.textLayout;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r23 = 0;
        r0 = r15.height;	 Catch:{ Exception -> 0x03c9 }
        r24 = r0;
        r0 = r24;
        r0 = (float) r0;	 Catch:{ Exception -> 0x03c9 }
        r24 = r0;
        r0 = r18;
        r1 = r22;
        r2 = r23;
        r3 = r24;
        r0.setCurrentLayout(r1, r2, r3);	 Catch:{ Exception -> 0x03c9 }
        r0 = r15.textLayout;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r23 = 0;
        r0 = r15.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r24 = r0;
        r24 = r9 - r24;
        r0 = r22;
        r1 = r23;
        r2 = r24;
        r3 = r18;
        r0.getSelectionPath(r1, r2, r3);	 Catch:{ Exception -> 0x03c9 }
        r0 = r5.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r22 = r22 + r12;
        r22 = r22 + -1;
        r0 = r22;
        if (r9 < r0) goto L_0x02b5;
    L_0x0365:
        r4 = r4 + 1;
        goto L_0x0252;
    L_0x0369:
        r22 = 0;
        r0 = r26;
        r1 = r22;
        r18 = r0.obtainNewUrlPath(r1);	 Catch:{ Exception -> 0x03c9 }
        r0 = r26;
        r0 = r0.pressedLink;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r22;
        r22 = r7.getSpanStart(r0);	 Catch:{ Exception -> 0x03c9 }
        r0 = r15.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r23 = r0;
        r19 = r22 - r23;
        r0 = r15.textLayout;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r15.height;	 Catch:{ Exception -> 0x03c9 }
        r23 = r0;
        r0 = r23;
        r0 = -r0;
        r23 = r0;
        r0 = r23;
        r0 = (float) r0;	 Catch:{ Exception -> 0x03c9 }
        r23 = r0;
        r0 = r18;
        r1 = r22;
        r2 = r19;
        r3 = r23;
        r0.setCurrentLayout(r1, r2, r3);	 Catch:{ Exception -> 0x03c9 }
        r0 = r15.textLayout;	 Catch:{ Exception -> 0x03c9 }
        r22 = r0;
        r0 = r26;
        r0 = r0.pressedLink;	 Catch:{ Exception -> 0x03c9 }
        r23 = r0;
        r0 = r23;
        r23 = r7.getSpanEnd(r0);	 Catch:{ Exception -> 0x03c9 }
        r0 = r15.charactersOffset;	 Catch:{ Exception -> 0x03c9 }
        r24 = r0;
        r23 = r23 - r24;
        r0 = r22;
        r1 = r19;
        r2 = r23;
        r3 = r18;
        r0.getSelectionPath(r1, r2, r3);	 Catch:{ Exception -> 0x03c9 }
        if (r19 >= 0) goto L_0x0312;
    L_0x03c5:
        r4 = r4 + -1;
        goto L_0x02b9;
    L_0x03c9:
        r8 = move-exception;
        r22 = "tmessages";
        r0 = r22;
        org.telegram.messenger.FileLog.m13e(r0, r8);	 Catch:{ Exception -> 0x03d3 }
        goto L_0x0312;
    L_0x03d3:
        r8 = move-exception;
        r22 = "tmessages";
        r0 = r22;
        org.telegram.messenger.FileLog.m13e(r0, r8);
    L_0x03db:
        r22 = 0;
        goto L_0x0044;
    L_0x03df:
        r22 = 0;
        r22 = r14[r22];	 Catch:{ Exception -> 0x03d3 }
        r0 = r26;
        r0 = r0.pressedLink;	 Catch:{ Exception -> 0x03d3 }
        r23 = r0;
        r0 = r22;
        r1 = r23;
        if (r0 != r1) goto L_0x03db;
    L_0x03ef:
        r0 = r26;
        r0 = r0.delegate;	 Catch:{ Exception -> 0x03d3 }
        r22 = r0;
        r0 = r26;
        r0 = r0.currentMessageObject;	 Catch:{ Exception -> 0x03d3 }
        r23 = r0;
        r0 = r26;
        r0 = r0.pressedLink;	 Catch:{ Exception -> 0x03d3 }
        r24 = r0;
        r25 = 0;
        r22.didPressedUrl(r23, r24, r25);	 Catch:{ Exception -> 0x03d3 }
        r22 = 1;
        r0 = r26;
        r1 = r22;
        r0.resetPressedLink(r1);	 Catch:{ Exception -> 0x03d3 }
        r22 = 1;
        goto L_0x0044;
    L_0x0413:
        r22 = 1;
        r0 = r26;
        r1 = r22;
        r0.resetPressedLink(r1);
        goto L_0x03db;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkTextBlockMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkCaptionMotionEvent(MotionEvent event) {
        if (!(this.currentMessageObject.caption instanceof Spannable) || this.captionLayout == null) {
            return false;
        }
        if (event.getAction() == 0 || ((this.linkPreviewPressed || this.pressedLink != null) && event.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT)) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x < this.captionX || x > this.captionX + this.backgroundWidth || y < this.captionY || y > this.captionY + this.captionHeight) {
                resetPressedLink(DOCUMENT_ATTACH_TYPE_AUDIO);
            } else if (event.getAction() == 0) {
                try {
                    x -= this.captionX;
                    int line = this.captionLayout.getLineForVertical(y - this.captionY);
                    int off = this.captionLayout.getOffsetForHorizontal(line, (float) x);
                    float left = this.captionLayout.getLineLeft(line);
                    if (left <= ((float) x) && this.captionLayout.getLineWidth(line) + left >= ((float) x)) {
                        Spannable buffer = this.currentMessageObject.caption;
                        ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                        boolean ignore = false;
                        if (link.length == 0 || !(link.length == 0 || !(link[DOCUMENT_ATTACH_TYPE_NONE] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled)) {
                            ignore = true;
                        }
                        if (!ignore) {
                            this.pressedLink = link[DOCUMENT_ATTACH_TYPE_NONE];
                            this.pressedLinkType = DOCUMENT_ATTACH_TYPE_AUDIO;
                            resetUrlPaths(false);
                            try {
                                LinkPath path = obtainNewUrlPath(false);
                                int start = buffer.getSpanStart(this.pressedLink);
                                path.setCurrentLayout(this.captionLayout, start, 0.0f);
                                this.captionLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), path);
                            } catch (Throwable e) {
                                FileLog.m13e("tmessages", e);
                            }
                            invalidate();
                            return true;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
            } else if (this.pressedLinkType == DOCUMENT_ATTACH_TYPE_AUDIO) {
                this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
                resetPressedLink(DOCUMENT_ATTACH_TYPE_AUDIO);
                return true;
            }
        }
        return false;
    }

    private boolean checkLinkPreviewMotionEvent(MotionEvent event) {
        if (this.currentMessageObject.type != 0 || !this.hasLinkPreview) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (x >= this.textX && x <= this.textX + this.backgroundWidth && y >= this.textY + this.currentMessageObject.textHeight && y <= ((this.textY + this.currentMessageObject.textHeight) + this.linkPreviewHeight) + AndroidUtilities.dp(8.0f)) {
            WebPage webPage;
            if (event.getAction() == 0) {
                if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT && this.drawPhotoImage && this.photoImage.isInsideImage((float) x, (float) y)) {
                    if (!this.drawImageButton || this.buttonState == -1 || x < this.buttonX || x > this.buttonX + AndroidUtilities.dp(48.0f) || y < this.buttonY || y > this.buttonY + AndroidUtilities.dp(48.0f)) {
                        this.linkPreviewPressed = true;
                        webPage = this.currentMessageObject.messageOwner.media.webpage;
                        if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF || this.buttonState != -1 || !MediaController.getInstance().canAutoplayGifs() || (this.photoImage.getAnimation() != null && TextUtils.isEmpty(webPage.embed_url))) {
                            return true;
                        }
                        this.linkPreviewPressed = false;
                        return false;
                    }
                    this.buttonPressed = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                    return true;
                } else if (this.descriptionLayout != null && y >= this.descriptionY) {
                    try {
                        x -= (this.textX + AndroidUtilities.dp(10.0f)) + this.descriptionX;
                        int line = this.descriptionLayout.getLineForVertical(y - this.descriptionY);
                        int off = this.descriptionLayout.getOffsetForHorizontal(line, (float) x);
                        float left = this.descriptionLayout.getLineLeft(line);
                        if (left <= ((float) x) && this.descriptionLayout.getLineWidth(line) + left >= ((float) x)) {
                            Spannable buffer = this.currentMessageObject.linkDescription;
                            ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                            boolean ignore = false;
                            if (link.length == 0 || !(link.length == 0 || !(link[DOCUMENT_ATTACH_TYPE_NONE] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled)) {
                                ignore = true;
                            }
                            if (!ignore) {
                                this.pressedLink = link[DOCUMENT_ATTACH_TYPE_NONE];
                                this.linkBlockNum = -10;
                                this.pressedLinkType = DOCUMENT_ATTACH_TYPE_GIF;
                                resetUrlPaths(false);
                                try {
                                    LinkPath path = obtainNewUrlPath(false);
                                    int start = buffer.getSpanStart(this.pressedLink);
                                    path.setCurrentLayout(this.descriptionLayout, start, 0.0f);
                                    this.descriptionLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), path);
                                } catch (Throwable e) {
                                    FileLog.m13e("tmessages", e);
                                }
                                invalidate();
                                return true;
                            }
                        }
                    } catch (Throwable e2) {
                        FileLog.m13e("tmessages", e2);
                    }
                }
            } else if (event.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                if (this.pressedLinkType != DOCUMENT_ATTACH_TYPE_GIF && this.buttonPressed == 0 && !this.linkPreviewPressed) {
                    resetPressedLink(DOCUMENT_ATTACH_TYPE_GIF);
                } else if (this.buttonPressed != 0) {
                    if (event.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                        playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                        didPressedButton(false);
                        invalidate();
                    } else if (event.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                        this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                        invalidate();
                    }
                } else if (this.pressedLink != null) {
                    if (this.pressedLink instanceof URLSpan) {
                        Browser.openUrl(getContext(), ((URLSpan) this.pressedLink).getURL());
                    } else {
                        this.pressedLink.onClick(this);
                    }
                    resetPressedLink(DOCUMENT_ATTACH_TYPE_GIF);
                } else {
                    if (!this.drawImageButton) {
                        webPage = this.currentMessageObject.messageOwner.media.webpage;
                        if (webPage != null) {
                            if (VERSION.SDK_INT < 16 || TextUtils.isEmpty(webPage.embed_url)) {
                                Browser.openUrl(getContext(), webPage.url);
                            } else {
                                this.delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.description, webPage.url, webPage.embed_width, webPage.embed_height);
                            }
                        }
                    } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF) {
                        if (this.buttonState == -1) {
                            if (MediaController.getInstance().canAutoplayGifs()) {
                                this.delegate.didPressedImage(this);
                            } else {
                                this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                                this.currentMessageObject.audioProgress = TouchHelperCallback.ALPHA_FULL;
                                this.photoImage.setAllowStartAnimation(false);
                                this.photoImage.stopAnimation();
                                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                                invalidate();
                                playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                            }
                        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_GIF || this.buttonState == 0) {
                            didPressedButton(false);
                            playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                        }
                    } else if (this.buttonState == -1) {
                        this.delegate.didPressedImage(this);
                        playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    }
                    resetPressedLink(DOCUMENT_ATTACH_TYPE_GIF);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkOtherButtonMotionEvent(MotionEvent event) {
        if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT && this.currentMessageObject.type != 12 && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC && this.documentAttachType != DOCUMENT_ATTACH_TYPE_VIDEO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF && this.currentMessageObject.type != 8) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            if (x < this.otherX - AndroidUtilities.dp(20.0f) || x > this.otherX + AndroidUtilities.dp(20.0f) || y < this.otherY - AndroidUtilities.dp(4.0f) || y > this.otherY + AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE)) {
                return false;
            }
            this.otherPressed = true;
            return true;
        } else if (event.getAction() != DOCUMENT_ATTACH_TYPE_DOCUMENT || !this.otherPressed) {
            return false;
        } else {
            this.otherPressed = false;
            playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
            this.delegate.didPressedOther(this);
            return false;
        }
    }

    private boolean checkPhotoImageMotionEvent(MotionEvent event) {
        if (!this.drawPhotoImage && this.documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        if (event.getAction() == 0) {
            if (this.buttonState != -1 && x >= this.buttonX && x <= this.buttonX + AndroidUtilities.dp(48.0f) && y >= this.buttonY && y <= this.buttonY + AndroidUtilities.dp(48.0f)) {
                this.buttonPressed = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                invalidate();
                result = true;
            } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                if (x >= this.photoImage.getImageX() && x <= (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(50.0f) && y >= this.photoImage.getImageY() && y <= this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                    this.imagePressed = true;
                    result = true;
                }
            } else if (!(this.currentMessageObject.type == 13 && this.currentMessageObject.getInputStickerSet() == null)) {
                if (x >= this.photoImage.getImageX() && x <= this.photoImage.getImageX() + this.backgroundWidth && y >= this.photoImage.getImageY() && y <= this.photoImage.getImageY() + this.photoImage.getImageHeight()) {
                    this.imagePressed = true;
                    result = true;
                }
                if (this.currentMessageObject.type == 12 && MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)) == null) {
                    this.imagePressed = false;
                    result = false;
                }
            }
            if (!this.imagePressed) {
                return result;
            }
            if (this.currentMessageObject.isSecretPhoto()) {
                this.imagePressed = false;
                return result;
            } else if (this.currentMessageObject.isSendError()) {
                this.imagePressed = false;
                return false;
            } else if (this.currentMessageObject.type != 8 || this.buttonState != -1 || !MediaController.getInstance().canAutoplayGifs() || this.photoImage.getAnimation() != null) {
                return result;
            } else {
                this.imagePressed = false;
                return false;
            }
        } else if (event.getAction() != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            return false;
        } else {
            if (this.buttonPressed == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                didPressedButton(false);
                this.radialProgress.swapBackground(getDrawableForCurrentState());
                invalidate();
                return false;
            } else if (!this.imagePressed) {
                return false;
            } else {
                this.imagePressed = false;
                if (this.buttonState == -1 || this.buttonState == DOCUMENT_ATTACH_TYPE_GIF || this.buttonState == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    didClickedImage();
                } else if (this.buttonState == 0 && this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    didPressedButton(false);
                }
                invalidate();
                return false;
            }
        }
    }

    private boolean checkAudioMotionEvent(MotionEvent event) {
        if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC) {
            return false;
        }
        boolean result;
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (this.useSeekBarWaweform) {
            result = this.seekBarWaveform.onTouch(event.getAction(), (event.getX() - ((float) this.seekBarX)) - ((float) AndroidUtilities.dp(13.0f)), event.getY() - ((float) this.seekBarY));
        } else {
            result = this.seekBar.onTouch(event.getAction(), event.getX() - ((float) this.seekBarX), event.getY() - ((float) this.seekBarY));
        }
        if (result) {
            if (!this.useSeekBarWaweform && event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (this.useSeekBarWaweform && !this.seekBarWaveform.isStartDraging() && event.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                didPressedButton(true);
            }
            this.disallowLongPress = true;
            invalidate();
            return result;
        }
        int side = AndroidUtilities.dp(36.0f);
        boolean area = (this.buttonState == 0 || this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.buttonState == DOCUMENT_ATTACH_TYPE_GIF) ? x >= this.buttonX - AndroidUtilities.dp(12.0f) && x <= (this.buttonX - AndroidUtilities.dp(12.0f)) + this.backgroundWidth && y >= this.namesOffset + this.mediaOffsetY && y <= this.layoutHeight : x >= this.buttonX && x <= this.buttonX + side && y >= this.buttonY && y <= this.buttonY + side;
        if (event.getAction() == 0) {
            if (!area) {
                return result;
            }
            this.buttonPressed = DOCUMENT_ATTACH_TYPE_DOCUMENT;
            invalidate();
            this.radialProgress.swapBackground(getDrawableForCurrentState());
            return true;
        } else if (this.buttonPressed == 0) {
            return result;
        } else {
            if (event.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                didPressedButton(true);
                invalidate();
            } else if (event.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                invalidate();
            } else if (event.getAction() == DOCUMENT_ATTACH_TYPE_GIF && !area) {
                this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
                invalidate();
            }
            this.radialProgress.swapBackground(getDrawableForCurrentState());
            return result;
        }
    }

    private boolean checkBotButtonMotionEvent(MotionEvent event) {
        if (this.botButtons.isEmpty()) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == 0) {
            int addX;
            if (this.currentMessageObject.isOutOwner()) {
                addX = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                addX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(this.mediaBackground ? TouchHelperCallback.ALPHA_FULL : 7.0f);
            }
            int a = DOCUMENT_ATTACH_TYPE_NONE;
            while (a < this.botButtons.size()) {
                BotButton button = (BotButton) this.botButtons.get(a);
                int y2 = (button.f12y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                if (x < button.f11x + addX || x > (button.f11x + addX) + button.width || y < y2 || y > button.height + y2) {
                    a += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                } else {
                    this.pressedBotButton = a;
                    invalidate();
                    return true;
                }
            }
            return false;
        } else if (event.getAction() != DOCUMENT_ATTACH_TYPE_DOCUMENT || this.pressedBotButton == -1) {
            return false;
        } else {
            playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
            this.delegate.didPressedBotButton(this, ((BotButton) this.botButtons.get(this.pressedBotButton)).button);
            this.pressedBotButton = -1;
            invalidate();
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.currentMessageObject == null || !this.delegate.canPerformActions()) {
            return super.onTouchEvent(event);
        }
        this.disallowLongPress = false;
        boolean result = checkTextBlockMotionEvent(event);
        if (!result) {
            result = checkOtherButtonMotionEvent(event);
        }
        if (!result) {
            result = checkLinkPreviewMotionEvent(event);
        }
        if (!result) {
            result = checkCaptionMotionEvent(event);
        }
        if (!result) {
            result = checkAudioMotionEvent(event);
        }
        if (!result) {
            result = checkPhotoImageMotionEvent(event);
        }
        if (!result) {
            result = checkBotButtonMotionEvent(event);
        }
        if (event.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
            this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
            this.pressedBotButton = -1;
            this.linkPreviewPressed = false;
            this.otherPressed = false;
            this.imagePressed = false;
            result = false;
            resetPressedLink(-1);
        }
        if (!this.disallowLongPress && result && event.getAction() == 0) {
            startCheckLongPress();
        }
        if (!(event.getAction() == 0 || event.getAction() == DOCUMENT_ATTACH_TYPE_GIF)) {
            cancelCheckLongPress();
        }
        if (result) {
            return result;
        }
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() != 0) {
            if (event.getAction() != DOCUMENT_ATTACH_TYPE_GIF) {
                cancelCheckLongPress();
            }
            if (this.avatarPressed) {
                if (event.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.avatarPressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate == null) {
                        return result;
                    }
                    if (this.currentUser != null) {
                        this.delegate.didPressedUserAvatar(this, this.currentUser);
                        return result;
                    } else if (this.currentChat == null) {
                        return result;
                    } else {
                        this.delegate.didPressedChannelAvatar(this, this.currentChat, DOCUMENT_ATTACH_TYPE_NONE);
                        return result;
                    }
                } else if (event.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.avatarPressed = false;
                    return result;
                } else if (event.getAction() != DOCUMENT_ATTACH_TYPE_GIF || !this.isAvatarVisible || this.avatarImage.isInsideImage(x, y)) {
                    return result;
                } else {
                    this.avatarPressed = false;
                    return result;
                }
            } else if (this.forwardNamePressed) {
                if (event.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.forwardNamePressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate == null) {
                        return result;
                    }
                    if (this.currentForwardChannel != null) {
                        this.delegate.didPressedChannelAvatar(this, this.currentForwardChannel, this.currentMessageObject.messageOwner.fwd_from.channel_post);
                        return result;
                    } else if (this.currentForwardUser == null) {
                        return result;
                    } else {
                        this.delegate.didPressedUserAvatar(this, this.currentForwardUser);
                        return result;
                    }
                } else if (event.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.forwardNamePressed = false;
                    return result;
                } else if (event.getAction() != DOCUMENT_ATTACH_TYPE_GIF) {
                    return result;
                } else {
                    if (x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                        return result;
                    }
                    this.forwardNamePressed = false;
                    return result;
                }
            } else if (this.forwardBotPressed) {
                if (event.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.forwardBotPressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate == null) {
                        return result;
                    }
                    this.delegate.didPressedViaBot(this, this.currentViaBotUser != null ? this.currentViaBotUser.username : this.currentMessageObject.messageOwner.via_bot_name);
                    return result;
                } else if (event.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.forwardBotPressed = false;
                    return result;
                } else if (event.getAction() != DOCUMENT_ATTACH_TYPE_GIF) {
                    return result;
                } else {
                    if (!this.drawForwardedName || this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] == null) {
                        if (x >= this.nameX + ((float) this.viaNameWidth) && x <= (this.nameX + ((float) this.viaNameWidth)) + ((float) this.viaWidth) && y >= this.nameY - ((float) AndroidUtilities.dp(4.0f)) && y <= this.nameY + ((float) AndroidUtilities.dp(20.0f))) {
                            return result;
                        }
                        this.forwardBotPressed = false;
                        return result;
                    } else if (x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                        return result;
                    } else {
                        this.forwardBotPressed = false;
                        return result;
                    }
                }
            } else if (this.replyPressed) {
                if (event.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.replyPressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate == null) {
                        return result;
                    }
                    this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
                    return result;
                } else if (event.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.replyPressed = false;
                    return result;
                } else if (event.getAction() != DOCUMENT_ATTACH_TYPE_GIF) {
                    return result;
                } else {
                    if (x >= ((float) this.replyStartX) && x <= ((float) (this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth))) && y >= ((float) this.replyStartY) && y <= ((float) (this.replyStartY + AndroidUtilities.dp(35.0f)))) {
                        return result;
                    }
                    this.replyPressed = false;
                    return result;
                }
            } else if (!this.sharePressed) {
                return result;
            } else {
                if (event.getAction() == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    this.sharePressed = false;
                    playSoundEffect(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.delegate != null) {
                        this.delegate.didPressedShare(this);
                    }
                } else if (event.getAction() == DOCUMENT_ATTACH_TYPE_AUDIO) {
                    this.sharePressed = false;
                } else if (event.getAction() == DOCUMENT_ATTACH_TYPE_GIF && (x < ((float) this.shareStartX) || x > ((float) (this.shareStartX + AndroidUtilities.dp(40.0f))) || y < ((float) this.shareStartY) || y > ((float) (this.shareStartY + AndroidUtilities.dp(32.0f))))) {
                    this.sharePressed = false;
                }
                invalidate();
                return result;
            }
        } else if (this.delegate != null && !this.delegate.canPerformActions()) {
            return result;
        } else {
            if (this.isAvatarVisible && this.avatarImage.isInsideImage(x, y)) {
                this.avatarPressed = true;
                result = true;
            } else if (this.drawForwardedName && this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] != null && x >= ((float) this.forwardNameX) && x <= ((float) (this.forwardNameX + this.forwardedNameWidth)) && y >= ((float) this.forwardNameY) && y <= ((float) (this.forwardNameY + AndroidUtilities.dp(32.0f)))) {
                if (this.viaWidth == 0 || x < ((float) ((this.forwardNameX + this.viaNameWidth) + AndroidUtilities.dp(4.0f)))) {
                    this.forwardNamePressed = true;
                } else {
                    this.forwardBotPressed = true;
                }
                result = true;
            } else if (this.drawNameLayout && this.nameLayout != null && this.viaWidth != 0 && x >= this.nameX + ((float) this.viaNameWidth) && x <= (this.nameX + ((float) this.viaNameWidth)) + ((float) this.viaWidth) && y >= this.nameY - ((float) AndroidUtilities.dp(4.0f)) && y <= this.nameY + ((float) AndroidUtilities.dp(20.0f))) {
                this.forwardBotPressed = true;
                result = true;
            } else if (this.currentMessageObject.isReply() && x >= ((float) this.replyStartX) && x <= ((float) (this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth))) && y >= ((float) this.replyStartY) && y <= ((float) (this.replyStartY + AndroidUtilities.dp(35.0f)))) {
                this.replyPressed = true;
                result = true;
            } else if (this.drawShareButton && x >= ((float) this.shareStartX) && x <= ((float) (this.shareStartX + AndroidUtilities.dp(40.0f))) && y >= ((float) this.shareStartY) && y <= ((float) (this.shareStartY + AndroidUtilities.dp(32.0f)))) {
                this.sharePressed = true;
                result = true;
                invalidate();
            }
            if (!result) {
                return result;
            }
            startCheckLongPress();
            return result;
        }
    }

    public void updateAudioProgress() {
        if (this.currentMessageObject != null && this.documentAttach != null) {
            if (this.useSeekBarWaweform) {
                if (!this.seekBarWaveform.isDragging()) {
                    this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress);
                }
            } else if (!this.seekBar.isDragging()) {
                this.seekBar.setProgress(this.currentMessageObject.audioProgress);
            }
            int duration = DOCUMENT_ATTACH_TYPE_NONE;
            int a;
            DocumentAttribute attribute;
            Object[] objArr;
            String timeString;
            if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
                if (MediaController.getInstance().isPlayingAudio(this.currentMessageObject)) {
                    duration = this.currentMessageObject.audioProgressSec;
                } else {
                    for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.documentAttach.attributes.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                        if (attribute instanceof TL_documentAttributeAudio) {
                            duration = attribute.duration;
                            break;
                        }
                    }
                }
                objArr = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                objArr[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(duration / 60);
                objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(duration % 60);
                timeString = String.format("%02d:%02d", objArr);
                if (this.lastTimeString == null || !(this.lastTimeString == null || this.lastTimeString.equals(timeString))) {
                    this.lastTimeString = timeString;
                    this.timeWidthAudio = (int) Math.ceil((double) audioTimePaint.measureText(timeString));
                    this.durationLayout = new StaticLayout(timeString, audioTimePaint, this.timeWidthAudio, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                }
            } else {
                int currentProgress = DOCUMENT_ATTACH_TYPE_NONE;
                for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.documentAttach.attributes.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                    if (attribute instanceof TL_documentAttributeAudio) {
                        duration = attribute.duration;
                        break;
                    }
                }
                if (MediaController.getInstance().isPlayingAudio(this.currentMessageObject)) {
                    currentProgress = this.currentMessageObject.audioProgressSec;
                }
                objArr = new Object[DOCUMENT_ATTACH_TYPE_VIDEO];
                objArr[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(currentProgress / 60);
                objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(currentProgress % 60);
                objArr[DOCUMENT_ATTACH_TYPE_GIF] = Integer.valueOf(duration / 60);
                objArr[DOCUMENT_ATTACH_TYPE_AUDIO] = Integer.valueOf(duration % 60);
                timeString = String.format("%d:%02d / %d:%02d", objArr);
                if (this.lastTimeString == null || !(this.lastTimeString == null || this.lastTimeString.equals(timeString))) {
                    this.lastTimeString = timeString;
                    this.durationLayout = new StaticLayout(timeString, audioTimePaint, (int) Math.ceil((double) audioTimePaint.measureText(timeString)), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                }
            }
            invalidate();
        }
    }

    public void downloadAudioIfNeed() {
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttach.size < 5242880 && this.buttonState == DOCUMENT_ATTACH_TYPE_GIF) {
            FileLoader.getInstance().loadFile(this.documentAttach, true, false);
            this.buttonState = DOCUMENT_ATTACH_TYPE_VIDEO;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        }
    }

    public void setVisiblePart(int position, int height) {
        if (this.currentMessageObject != null && this.currentMessageObject.textLayoutBlocks != null) {
            position -= this.textY;
            int newFirst = -1;
            int newLast = -1;
            int newCount = DOCUMENT_ATTACH_TYPE_NONE;
            int startBlock = DOCUMENT_ATTACH_TYPE_NONE;
            int a = DOCUMENT_ATTACH_TYPE_NONE;
            while (a < this.currentMessageObject.textLayoutBlocks.size() && ((TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a)).textYOffset <= ((float) position)) {
                startBlock = a;
                a += DOCUMENT_ATTACH_TYPE_DOCUMENT;
            }
            for (a = startBlock; a < this.currentMessageObject.textLayoutBlocks.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                TextLayoutBlock block = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a);
                float y = block.textYOffset;
                if (intersect(y, ((float) block.height) + y, (float) position, (float) (position + height))) {
                    if (newFirst == -1) {
                        newFirst = a;
                    }
                    newLast = a;
                    newCount += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                } else if (y > ((float) position)) {
                    break;
                }
            }
            if (this.lastVisibleBlockNum != newLast || this.firstVisibleBlockNum != newFirst || this.totalVisibleBlocksCount != newCount) {
                this.lastVisibleBlockNum = newLast;
                this.firstVisibleBlockNum = newFirst;
                this.totalVisibleBlocksCount = newCount;
                invalidate();
            }
        }
    }

    private boolean intersect(float left1, float right1, float left2, float right2) {
        if (left1 <= left2) {
            if (right1 >= left2) {
                return true;
            }
            return false;
        } else if (left1 > right2) {
            return false;
        } else {
            return true;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.text.StaticLayout generateStaticLayout(java.lang.CharSequence r17, android.text.TextPaint r18, int r19, int r20, int r21, int r22) {
        /*
        r16 = new android.text.SpannableStringBuilder;
        r16.<init>(r17);
        r13 = 0;
        r1 = new android.text.StaticLayout;
        r5 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r6 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r7 = 0;
        r8 = 0;
        r2 = r17;
        r3 = r18;
        r4 = r20;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8);
        r12 = 0;
    L_0x0018:
        r0 = r21;
        if (r12 >= r0) goto L_0x0049;
    L_0x001c:
        r14 = r1.getLineDirections(r12);
        r2 = r1.getLineLeft(r12);
        r3 = 0;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 != 0) goto L_0x003d;
    L_0x0029:
        r2 = r1.getLineStart(r12);
        r2 = r1.isRtlCharAt(r2);
        if (r2 != 0) goto L_0x003d;
    L_0x0033:
        r2 = r1.getLineEnd(r12);
        r2 = r1.isRtlCharAt(r2);
        if (r2 == 0) goto L_0x003f;
    L_0x003d:
        r19 = r20;
    L_0x003f:
        r15 = r1.getLineEnd(r12);
        r2 = r17.length();
        if (r15 != r2) goto L_0x0066;
    L_0x0049:
        r5 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r6 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r2 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r7 = (float) r2;
        r8 = 0;
        r9 = android.text.TextUtils.TruncateAt.END;
        r2 = r16;
        r3 = r18;
        r4 = r19;
        r10 = r19;
        r11 = r22;
        r2 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        return r2;
    L_0x0066:
        r15 = r15 + -1;
        r2 = r15 + r13;
        r0 = r16;
        r2 = r0.charAt(r2);
        r3 = 32;
        if (r2 != r3) goto L_0x0090;
    L_0x0074:
        r2 = r15 + r13;
        r3 = r15 + r13;
        r3 = r3 + 1;
        r4 = "\n";
        r0 = r16;
        r0.replace(r2, r3, r4);
    L_0x0081:
        r2 = r1.getLineCount();
        r2 = r2 + -1;
        if (r12 == r2) goto L_0x0049;
    L_0x0089:
        r2 = r22 + -1;
        if (r12 == r2) goto L_0x0049;
    L_0x008d:
        r12 = r12 + 1;
        goto L_0x0018;
    L_0x0090:
        r2 = r15 + r13;
        r0 = r16;
        r2 = r0.charAt(r2);
        r3 = 10;
        if (r2 == r3) goto L_0x0081;
    L_0x009c:
        r2 = r15 + r13;
        r3 = "\n";
        r0 = r16;
        r0.insert(r2, r3);
        r13 = r13 + 1;
        goto L_0x0081;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(java.lang.CharSequence, android.text.TextPaint, int, int, int, int):android.text.StaticLayout");
    }

    private void didClickedImage() {
        if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.currentMessageObject.type == 13) {
            if (this.buttonState == -1) {
                this.delegate.didPressedImage(this);
            } else if (this.buttonState == 0) {
                didPressedButton(false);
            }
        } else if (this.currentMessageObject.type == 12) {
            this.delegate.didPressedUserAvatar(this, MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)));
        } else if (this.currentMessageObject.type == 8) {
            if (this.buttonState == -1) {
                if (MediaController.getInstance().canAutoplayGifs()) {
                    this.delegate.didPressedImage(this);
                    return;
                }
                this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                this.currentMessageObject.audioProgress = TouchHelperCallback.ALPHA_FULL;
                this.photoImage.setAllowStartAnimation(false);
                this.photoImage.stopAnimation();
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_GIF || this.buttonState == 0) {
                didPressedButton(false);
            }
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (this.buttonState == 0 || this.buttonState == DOCUMENT_ATTACH_TYPE_AUDIO) {
                didPressedButton(false);
            }
        } else if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_VIDEO) {
            this.delegate.didPressedImage(this);
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            if (this.buttonState == -1) {
                this.delegate.didPressedImage(this);
            }
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && this.buttonState == -1) {
            WebPage webPage = this.currentMessageObject.messageOwner.media.webpage;
            if (VERSION.SDK_INT < 16 || webPage.embed_url == null || webPage.embed_url.length() == 0) {
                Browser.openUrl(getContext(), webPage.url);
            } else {
                this.delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.description, webPage.url, webPage.embed_width, webPage.embed_height);
            }
        }
    }

    private void updateSecretTimeText(MessageObject messageObject) {
        if (messageObject != null && !messageObject.isOut()) {
            String str = messageObject.getSecretTimeString();
            if (str != null) {
                this.infoWidth = (int) Math.ceil((double) infoPaint.measureText(str));
                this.infoLayout = new StaticLayout(TextUtils.ellipsize(str, infoPaint, (float) this.infoWidth, TruncateAt.END), infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                invalidate();
            }
        }
    }

    private boolean isPhotoDataChanged(MessageObject object) {
        if (object.type == 0 || object.type == 14) {
            return false;
        }
        if (object.type == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (this.currentUrl == null) {
                return true;
            }
            double lat = object.messageOwner.media.geo.lat;
            double lon = object.messageOwner.media.geo._long;
            Object[] objArr = new Object[DOCUMENT_ATTACH_TYPE_MUSIC];
            objArr[DOCUMENT_ATTACH_TYPE_NONE] = Double.valueOf(lat);
            objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Double.valueOf(lon);
            objArr[DOCUMENT_ATTACH_TYPE_GIF] = Integer.valueOf(Math.min(DOCUMENT_ATTACH_TYPE_GIF, (int) Math.ceil((double) AndroidUtilities.density)));
            objArr[DOCUMENT_ATTACH_TYPE_AUDIO] = Double.valueOf(lat);
            objArr[DOCUMENT_ATTACH_TYPE_VIDEO] = Double.valueOf(lon);
            if (!String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", objArr).equals(this.currentUrl)) {
                return true;
            }
        } else if (this.currentPhotoObject == null || (this.currentPhotoObject.location instanceof TL_fileLocationUnavailable)) {
            return true;
        } else {
            if (this.currentMessageObject != null && this.photoNotSet && FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists()) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserDataChanged() {
        boolean z = false;
        if (this.currentMessageObject != null && !this.hasLinkPreview && this.currentMessageObject.messageOwner.media != null && (this.currentMessageObject.messageOwner.media.webpage instanceof TL_webPage)) {
            return true;
        }
        if (this.currentMessageObject == null || (this.currentUser == null && this.currentChat == null)) {
            return false;
        }
        if (this.lastSendState != this.currentMessageObject.messageOwner.send_state || this.lastDeleteDate != this.currentMessageObject.messageOwner.destroyTime || this.lastViewsCount != this.currentMessageObject.messageOwner.views) {
            return true;
        }
        User newUser = null;
        Chat newChat = null;
        if (this.currentMessageObject.isFromUser()) {
            newUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
        } else if (this.currentMessageObject.messageOwner.from_id < 0) {
            newChat = MessagesController.getInstance().getChat(Integer.valueOf(-this.currentMessageObject.messageOwner.from_id));
        } else if (this.currentMessageObject.messageOwner.post) {
            newChat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
        }
        FileLocation newPhoto = null;
        if (this.isAvatarVisible) {
            if (newUser != null && newUser.photo != null) {
                newPhoto = newUser.photo.photo_small;
            } else if (!(newChat == null || newChat.photo == null)) {
                newPhoto = newChat.photo.photo_small;
            }
        }
        if (this.replyTextLayout == null && this.currentMessageObject.replyMessageObject != null) {
            return true;
        }
        if (this.currentPhoto == null && newPhoto != null) {
            return true;
        }
        if (this.currentPhoto != null && newPhoto == null) {
            return true;
        }
        if (this.currentPhoto != null && newPhoto != null && (this.currentPhoto.local_id != newPhoto.local_id || this.currentPhoto.volume_id != newPhoto.volume_id)) {
            return true;
        }
        FileLocation newReplyPhoto = null;
        if (this.currentMessageObject.replyMessageObject != null) {
            PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
            if (!(photoSize == null || this.currentMessageObject.replyMessageObject.type == 13)) {
                newReplyPhoto = photoSize.location;
            }
        }
        if (this.currentReplyPhoto == null && newReplyPhoto != null) {
            return true;
        }
        String newNameString = null;
        if (this.drawName && this.isChat && !this.currentMessageObject.isOutOwner()) {
            if (newUser != null) {
                newNameString = UserObject.getUserName(newUser);
            } else if (newChat != null) {
                newNameString = newChat.title;
            }
        }
        if (this.currentNameString == null && newNameString != null) {
            return true;
        }
        if (this.currentNameString != null && newNameString == null) {
            return true;
        }
        if (this.currentNameString != null && newNameString != null && !this.currentNameString.equals(newNameString)) {
            return true;
        }
        if (!this.drawForwardedName) {
            return false;
        }
        newNameString = this.currentMessageObject.getForwardedName();
        if ((this.currentForwardNameString == null && newNameString != null) || ((this.currentForwardNameString != null && newNameString == null) || !(this.currentForwardNameString == null || newNameString == null || this.currentForwardNameString.equals(newNameString)))) {
            z = true;
        }
        return z;
    }

    public ImageReceiver getPhotoImage() {
        return this.photoImage;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
        this.replyImageReceiver.onDetachedFromWindow();
        this.photoImage.onDetachedFromWindow();
        MediaController.getInstance().removeLoadingFileObserver(this);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        this.replyImageReceiver.onAttachedToWindow();
        if (!this.drawPhotoImage) {
            updateButtonState(false);
        } else if (this.photoImage.onAttachedToWindow()) {
            updateButtonState(false);
        }
    }

    protected void onLongPress() {
        if (this.pressedLink instanceof URLSpanNoUnderline) {
            if (this.pressedLink.getURL().startsWith("/")) {
                this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
                return;
            }
        } else if (this.pressedLink instanceof URLSpan) {
            this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
            return;
        }
        resetPressedLink(-1);
        if (!(this.buttonPressed == 0 && this.pressedBotButton == -1)) {
            this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
            this.pressedBotButton = -1;
            invalidate();
        }
        if (this.delegate != null) {
            this.delegate.didLongPressed(this);
        }
    }

    public void setCheckPressed(boolean value, boolean pressed) {
        this.isCheckPressed = value;
        this.isPressed = pressed;
        this.radialProgress.swapBackground(getDrawableForCurrentState());
        if (this.useSeekBarWaweform) {
            this.seekBarWaveform.setSelected(isDrawSelectedBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectedBackground());
        }
        invalidate();
    }

    public void setHighlighted(boolean value) {
        if (this.isHighlighted != value) {
            this.isHighlighted = value;
            this.radialProgress.swapBackground(getDrawableForCurrentState());
            if (this.useSeekBarWaweform) {
                this.seekBarWaveform.setSelected(isDrawSelectedBackground());
            } else {
                this.seekBar.setSelected(isDrawSelectedBackground());
            }
            invalidate();
        }
    }

    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        this.radialProgress.swapBackground(getDrawableForCurrentState());
        if (this.useSeekBarWaweform) {
            this.seekBarWaveform.setSelected(isDrawSelectedBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectedBackground());
        }
        invalidate();
    }

    public void onSeekBarDrag(float progress) {
        if (this.currentMessageObject != null) {
            this.currentMessageObject.audioProgress = progress;
            MediaController.getInstance().seekToProgress(this.currentMessageObject, progress);
        }
    }

    private void updateWaveform() {
        if (this.currentMessageObject != null && this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
            for (int a = DOCUMENT_ATTACH_TYPE_NONE; a < this.documentAttach.attributes.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                DocumentAttribute attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                if (attribute instanceof TL_documentAttributeAudio) {
                    if (attribute.waveform == null || attribute.waveform.length == 0) {
                        MediaController.getInstance().generateWaveform(this.currentMessageObject);
                    }
                    this.useSeekBarWaweform = attribute.waveform != null;
                    this.seekBarWaveform.setWaveform(attribute.waveform);
                    return;
                }
            }
        }
    }

    private int createDocumentLayout(int maxWidth, MessageObject messageObject) {
        if (messageObject.type == 0) {
            this.documentAttach = messageObject.messageOwner.media.webpage.document;
        } else {
            this.documentAttach = messageObject.messageOwner.media.document;
        }
        if (this.documentAttach == null) {
            return DOCUMENT_ATTACH_TYPE_NONE;
        }
        int duration;
        int a;
        DocumentAttribute attribute;
        if (MessageObject.isVoiceDocument(this.documentAttach)) {
            this.documentAttachType = DOCUMENT_ATTACH_TYPE_AUDIO;
            duration = DOCUMENT_ATTACH_TYPE_NONE;
            for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.documentAttach.attributes.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                if (attribute instanceof TL_documentAttributeAudio) {
                    duration = attribute.duration;
                    break;
                }
            }
            this.availableTimeWidth = (maxWidth - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil((double) audioTimePaint.measureText("00:00")));
            measureTime(messageObject);
            int minSize = AndroidUtilities.dp(174.0f) + this.timeWidth;
            if (!this.hasLinkPreview) {
                this.backgroundWidth = Math.min(maxWidth, (AndroidUtilities.dp(10.0f) * duration) + minSize);
            }
            if (messageObject.isOutOwner()) {
                this.seekBarWaveform.setColors(Theme.MSG_OUT_VOICE_SEEKBAR_COLOR, Theme.MSG_OUT_VOICE_SEEKBAR_FILL_COLOR, Theme.MSG_OUT_VOICE_SEEKBAR_SELECTED_COLOR);
                this.seekBar.setColors(Theme.MSG_OUT_AUDIO_SEEKBAR_COLOR, Theme.MSG_OUT_AUDIO_SEEKBAR_FILL_COLOR, Theme.MSG_OUT_AUDIO_SEEKBAR_SELECTED_COLOR);
            } else {
                this.seekBarWaveform.setColors(Theme.MSG_IN_VOICE_SEEKBAR_COLOR, Theme.MSG_IN_VOICE_SEEKBAR_FILL_COLOR, Theme.MSG_IN_VOICE_SEEKBAR_SELECTED_COLOR);
                this.seekBar.setColors(Theme.MSG_IN_AUDIO_SEEKBAR_COLOR, Theme.MSG_IN_AUDIO_SEEKBAR_FILL_COLOR, Theme.MSG_IN_AUDIO_SEEKBAR_SELECTED_COLOR);
            }
            this.seekBarWaveform.setMessageObject(messageObject);
            return DOCUMENT_ATTACH_TYPE_NONE;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = DOCUMENT_ATTACH_TYPE_MUSIC;
            if (messageObject.isOutOwner()) {
                this.seekBar.setColors(Theme.MSG_OUT_AUDIO_SEEKBAR_COLOR, Theme.MSG_OUT_AUDIO_SEEKBAR_FILL_COLOR, Theme.MSG_OUT_AUDIO_SEEKBAR_SELECTED_COLOR);
            } else {
                this.seekBar.setColors(Theme.MSG_IN_AUDIO_SEEKBAR_COLOR, Theme.MSG_IN_AUDIO_SEEKBAR_FILL_COLOR, Theme.MSG_IN_AUDIO_SEEKBAR_SELECTED_COLOR);
            }
            maxWidth -= AndroidUtilities.dp(86.0f);
            this.songLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicTitle().replace('\n', ' '), audioTitlePaint, (float) (maxWidth - AndroidUtilities.dp(12.0f)), TruncateAt.END), audioTitlePaint, maxWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
            if (this.songLayout.getLineCount() > 0) {
                this.songX = -((int) Math.ceil((double) this.songLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE)));
            }
            this.performerLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicAuthor().replace('\n', ' '), audioPerformerPaint, (float) maxWidth, TruncateAt.END), audioPerformerPaint, maxWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
            if (this.performerLayout.getLineCount() > 0) {
                this.performerX = -((int) Math.ceil((double) this.performerLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE)));
            }
            duration = DOCUMENT_ATTACH_TYPE_NONE;
            for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.documentAttach.attributes.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                if (attribute instanceof TL_documentAttributeAudio) {
                    duration = attribute.duration;
                    break;
                }
            }
            TextPaint textPaint = audioTimePaint;
            Object[] objArr = new Object[DOCUMENT_ATTACH_TYPE_VIDEO];
            objArr[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(duration / 60);
            objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(duration % 60);
            objArr[DOCUMENT_ATTACH_TYPE_GIF] = Integer.valueOf(duration / 60);
            objArr[DOCUMENT_ATTACH_TYPE_AUDIO] = Integer.valueOf(duration % 60);
            int durationWidth = (int) Math.ceil((double) textPaint.measureText(String.format("%d:%02d / %d:%02d", objArr)));
            this.availableTimeWidth = (this.backgroundWidth - AndroidUtilities.dp(94.0f)) - durationWidth;
            return durationWidth;
        } else if (MessageObject.isVideoDocument(this.documentAttach)) {
            this.documentAttachType = DOCUMENT_ATTACH_TYPE_VIDEO;
            duration = DOCUMENT_ATTACH_TYPE_NONE;
            for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.documentAttach.attributes.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                attribute = (DocumentAttribute) this.documentAttach.attributes.get(a);
                if (attribute instanceof TL_documentAttributeVideo) {
                    duration = attribute.duration;
                    break;
                }
            }
            int minutes = duration / 60;
            int seconds = duration - (minutes * 60);
            Object[] objArr2 = new Object[DOCUMENT_ATTACH_TYPE_AUDIO];
            objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(minutes);
            objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(seconds);
            objArr2[DOCUMENT_ATTACH_TYPE_GIF] = AndroidUtilities.formatFileSize((long) this.documentAttach.size);
            str = String.format("%d:%02d, %s", objArr2);
            this.infoWidth = (int) Math.ceil((double) infoPaint.measureText(str));
            this.infoLayout = new StaticLayout(str, infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
            return DOCUMENT_ATTACH_TYPE_NONE;
        } else {
            int width;
            boolean z = (this.documentAttach.mime_type != null && this.documentAttach.mime_type.toLowerCase().startsWith("image/")) || ((this.documentAttach.thumb instanceof TL_photoSize) && !(this.documentAttach.thumb.location instanceof TL_fileLocationUnavailable));
            this.drawPhotoImage = z;
            if (!this.drawPhotoImage) {
                maxWidth += AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
            }
            this.documentAttachType = DOCUMENT_ATTACH_TYPE_DOCUMENT;
            String name = FileLoader.getDocumentFileName(this.documentAttach);
            if (name == null || name.length() == 0) {
                name = LocaleController.getString("AttachDocument", C0691R.string.AttachDocument);
            }
            this.docTitleLayout = StaticLayoutEx.createStaticLayout(name, docNamePaint, maxWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false, TruncateAt.MIDDLE, maxWidth, this.drawPhotoImage ? DOCUMENT_ATTACH_TYPE_GIF : DOCUMENT_ATTACH_TYPE_DOCUMENT);
            this.docTitleOffsetX = LinearLayoutManager.INVALID_OFFSET;
            if (this.docTitleLayout == null || this.docTitleLayout.getLineCount() <= 0) {
                width = maxWidth;
                this.docTitleOffsetX = DOCUMENT_ATTACH_TYPE_NONE;
            } else {
                int maxLineWidth = DOCUMENT_ATTACH_TYPE_NONE;
                for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.docTitleLayout.getLineCount(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    maxLineWidth = Math.max(maxLineWidth, (int) Math.ceil((double) this.docTitleLayout.getLineWidth(a)));
                    this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int) Math.ceil((double) (-this.docTitleLayout.getLineLeft(a))));
                }
                width = Math.min(maxWidth, maxLineWidth);
            }
            str = AndroidUtilities.formatFileSize((long) this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach);
            this.infoWidth = Math.min(maxWidth - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE), (int) Math.ceil((double) infoPaint.measureText(str)));
            CharSequence str2 = TextUtils.ellipsize(str, infoPaint, (float) this.infoWidth, TruncateAt.END);
            try {
                if (this.infoWidth < 0) {
                    this.infoWidth = AndroidUtilities.dp(10.0f);
                }
                this.infoLayout = new StaticLayout(str2, infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            if (this.drawPhotoImage) {
                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                this.photoImage.setNeedsQualityThumb(true);
                this.photoImage.setShouldGenerateQualityThumb(true);
                this.photoImage.setParentMessageObject(messageObject);
                if (this.currentPhotoObject != null) {
                    this.currentPhotoFilter = "86_86_b";
                    this.photoImage.setImage(null, null, null, null, this.currentPhotoObject.location, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, true);
                } else {
                    this.photoImage.setImageBitmap((BitmapDrawable) null);
                }
            }
            return width;
        }
    }

    private void calcBackgroundWidth(int maxWidth, int timeMore, int maxChildWidth) {
        if (this.hasLinkPreview || maxWidth - this.currentMessageObject.lastLineWidth < timeMore) {
            this.totalHeight += AndroidUtilities.dp(14.0f);
            this.backgroundWidth = Math.max(maxChildWidth, this.currentMessageObject.lastLineWidth) + AndroidUtilities.dp(31.0f);
            this.backgroundWidth = Math.max(this.backgroundWidth, this.timeWidth + AndroidUtilities.dp(31.0f));
            return;
        }
        int diff = maxChildWidth - this.currentMessageObject.lastLineWidth;
        if (diff < 0 || diff > timeMore) {
            this.backgroundWidth = Math.max(maxChildWidth, this.currentMessageObject.lastLineWidth + timeMore) + AndroidUtilities.dp(31.0f);
        } else {
            this.backgroundWidth = ((maxChildWidth + timeMore) - diff) + AndroidUtilities.dp(31.0f);
        }
    }

    public void setHighlightedText(String text) {
        if (this.currentMessageObject.messageOwner.message != null && this.currentMessageObject != null && this.currentMessageObject.type == 0 && !TextUtils.isEmpty(this.currentMessageObject.messageText) && text != null) {
            int start = TextUtils.indexOf(this.currentMessageObject.messageOwner.message.toLowerCase(), text.toLowerCase());
            if (start != -1) {
                int end = start + text.length();
                int c = DOCUMENT_ATTACH_TYPE_NONE;
                while (c < this.currentMessageObject.textLayoutBlocks.size()) {
                    TextLayoutBlock block = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(c);
                    if (start < block.charactersOffset || start >= block.charactersOffset + block.textLayout.getText().length()) {
                        c += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                    } else {
                        this.linkSelectionBlockNum = c;
                        resetUrlPaths(true);
                        try {
                            LinkPath path = obtainNewUrlPath(true);
                            int length = block.textLayout.getText().length();
                            path.setCurrentLayout(block.textLayout, start, 0.0f);
                            block.textLayout.getSelectionPath(start, end - block.charactersOffset, path);
                            if (end >= block.charactersOffset + length) {
                                for (int a = c + DOCUMENT_ATTACH_TYPE_DOCUMENT; a < this.currentMessageObject.textLayoutBlocks.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                    TextLayoutBlock nextBlock = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a);
                                    length = nextBlock.textLayout.getText().length();
                                    path = obtainNewUrlPath(true);
                                    path.setCurrentLayout(nextBlock.textLayout, DOCUMENT_ATTACH_TYPE_NONE, (float) nextBlock.height);
                                    nextBlock.textLayout.getSelectionPath(DOCUMENT_ATTACH_TYPE_NONE, end - nextBlock.charactersOffset, path);
                                    if (end < (block.charactersOffset + length) - 1) {
                                        break;
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                        invalidate();
                        return;
                    }
                }
            } else if (!this.urlPathSelection.isEmpty()) {
                this.linkSelectionBlockNum = -1;
                resetUrlPaths(true);
                invalidate();
            }
        } else if (!this.urlPathSelection.isEmpty()) {
            this.linkSelectionBlockNum = -1;
            resetUrlPaths(true);
            invalidate();
        }
    }

    public void setMessageObject(MessageObject messageObject) {
        int height;
        Throwable e;
        int a;
        boolean authorIsRTL;
        boolean smallImage;
        int maxPhotoWidth;
        ArrayList arrayList;
        int photoSize;
        int maxWidth;
        Document document;
        PhotoSize photoSize2;
        PhotoSize photoSize3;
        int dp;
        int durationWidth;
        float scale;
        ImageReceiver imageReceiver;
        TLObject tLObject;
        String str;
        FileLocation fileLocation;
        boolean photoExist;
        int minutes;
        int seconds;
        Object[] objArr;
        String str2;
        int timeWidthTotal;
        int rows;
        boolean fullWidth;
        int maxButtonWidth;
        int maxButtonsWidth;
        int b;
        BotButton botButton;
        BotButton oldButton;
        boolean messageIdChanged = this.currentMessageObject == null || this.currentMessageObject.getId() != messageObject.getId();
        boolean messageChanged = this.currentMessageObject != messageObject || messageObject.forceUpdate;
        boolean dataChanged = this.currentMessageObject == messageObject && (isUserDataChanged() || this.photoNotSet);
        if (messageChanged || dataChanged || isPhotoDataChanged(messageObject)) {
            int width;
            float f;
            TL_keyboardButtonRow row;
            int buttonsCount;
            int dp2;
            int buttonWidth;
            ChatMessageCell chatMessageCell;
            String key;
            this.currentMessageObject = messageObject;
            this.lastSendState = messageObject.messageOwner.send_state;
            this.lastDeleteDate = messageObject.messageOwner.destroyTime;
            this.lastViewsCount = messageObject.messageOwner.views;
            this.isPressed = false;
            this.isCheckPressed = true;
            this.isAvatarVisible = false;
            this.wasLayout = false;
            this.drawShareButton = checkNeedDrawShareButton(messageObject);
            this.replyNameLayout = null;
            this.replyTextLayout = null;
            this.replyNameWidth = DOCUMENT_ATTACH_TYPE_NONE;
            this.replyTextWidth = DOCUMENT_ATTACH_TYPE_NONE;
            this.viaWidth = DOCUMENT_ATTACH_TYPE_NONE;
            this.viaNameWidth = DOCUMENT_ATTACH_TYPE_NONE;
            this.currentReplyPhoto = null;
            this.currentUser = null;
            this.currentChat = null;
            this.currentViaBotUser = null;
            this.drawNameLayout = false;
            resetPressedLink(-1);
            messageObject.forceUpdate = false;
            this.drawPhotoImage = false;
            this.hasLinkPreview = false;
            this.linkPreviewPressed = false;
            this.buttonPressed = DOCUMENT_ATTACH_TYPE_NONE;
            this.pressedBotButton = -1;
            this.linkPreviewHeight = DOCUMENT_ATTACH_TYPE_NONE;
            this.mediaOffsetY = DOCUMENT_ATTACH_TYPE_NONE;
            this.documentAttachType = DOCUMENT_ATTACH_TYPE_NONE;
            this.documentAttach = null;
            this.descriptionLayout = null;
            this.titleLayout = null;
            this.videoInfoLayout = null;
            this.siteNameLayout = null;
            this.authorLayout = null;
            this.captionLayout = null;
            this.docTitleLayout = null;
            this.drawImageButton = false;
            this.currentPhotoObject = null;
            this.currentPhotoObjectThumb = null;
            this.currentPhotoFilter = null;
            this.infoLayout = null;
            this.cancelLoading = false;
            this.buttonState = -1;
            this.currentUrl = null;
            this.photoNotSet = false;
            this.drawBackground = true;
            this.drawName = false;
            this.useSeekBarWaweform = false;
            this.drawForwardedName = false;
            this.mediaBackground = false;
            this.availableTimeWidth = DOCUMENT_ATTACH_TYPE_NONE;
            this.photoImage.setNeedsQualityThumb(false);
            this.photoImage.setShouldGenerateQualityThumb(false);
            this.photoImage.setParentMessageObject(null);
            this.photoImage.setRoundRadius(AndroidUtilities.dp(3.0f));
            if (messageChanged) {
                this.firstVisibleBlockNum = DOCUMENT_ATTACH_TYPE_NONE;
                this.lastVisibleBlockNum = DOCUMENT_ATTACH_TYPE_NONE;
                this.needNewVisiblePart = true;
            }
            int maxWidth2;
            boolean z;
            DocumentAttribute attribute;
            Object[] objArr2;
            String fileName;
            if (messageObject.type == 0) {
                this.drawForwardedName = true;
                if (AndroidUtilities.isTablet()) {
                    if (this.isChat && !messageObject.isOutOwner() && messageObject.isFromUser()) {
                        maxWidth2 = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(122.0f);
                        this.drawName = true;
                    } else {
                        z = (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isOutOwner()) ? false : true;
                        this.drawName = z;
                        maxWidth2 = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(80.0f);
                    }
                } else if (this.isChat && !messageObject.isOutOwner() && messageObject.isFromUser()) {
                    maxWidth2 = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(122.0f);
                    this.drawName = true;
                } else {
                    maxWidth2 = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(80.0f);
                    z = (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isOutOwner()) ? false : true;
                    this.drawName = z;
                }
                measureTime(messageObject);
                int timeMore = this.timeWidth + AndroidUtilities.dp(6.0f);
                if (messageObject.isOutOwner()) {
                    timeMore += AndroidUtilities.dp(20.5f);
                }
                z = (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && (messageObject.messageOwner.media.webpage instanceof TL_webPage);
                this.hasLinkPreview = z;
                this.backgroundWidth = maxWidth2;
                if (this.hasLinkPreview || maxWidth2 - messageObject.lastLineWidth < timeMore) {
                    this.backgroundWidth = Math.max(this.backgroundWidth, messageObject.lastLineWidth) + AndroidUtilities.dp(31.0f);
                    this.backgroundWidth = Math.max(this.backgroundWidth, this.timeWidth + AndroidUtilities.dp(31.0f));
                } else {
                    int diff = this.backgroundWidth - messageObject.lastLineWidth;
                    if (diff < 0 || diff > timeMore) {
                        this.backgroundWidth = Math.max(this.backgroundWidth, messageObject.lastLineWidth + timeMore) + AndroidUtilities.dp(31.0f);
                    } else {
                        this.backgroundWidth = ((this.backgroundWidth + timeMore) - diff) + AndroidUtilities.dp(31.0f);
                    }
                }
                this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.dp(31.0f);
                setMessageObjectInternal(messageObject);
                this.backgroundWidth = messageObject.textWidth;
                this.totalHeight = (messageObject.textHeight + AndroidUtilities.dp(19.5f)) + this.namesOffset;
                int maxChildWidth = Math.max(Math.max(Math.max(Math.max(this.backgroundWidth, this.nameWidth), this.forwardedNameWidth), this.replyNameWidth), this.replyTextWidth);
                int maxWebWidth = DOCUMENT_ATTACH_TYPE_NONE;
                if (this.hasLinkPreview) {
                    int linkPreviewMaxWidth;
                    int restLines;
                    int restLinesCount;
                    int lineLeft;
                    boolean hasRTL;
                    if (AndroidUtilities.isTablet()) {
                        if (!messageObject.isFromUser() || ((this.currentMessageObject.messageOwner.to_id.channel_id == 0 && this.currentMessageObject.messageOwner.to_id.chat_id == 0) || this.currentMessageObject.isOut())) {
                            linkPreviewMaxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(80.0f);
                        } else {
                            linkPreviewMaxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(122.0f);
                        }
                    } else if (!messageObject.isFromUser() || ((this.currentMessageObject.messageOwner.to_id.channel_id == 0 && this.currentMessageObject.messageOwner.to_id.chat_id == 0) || this.currentMessageObject.isOutOwner())) {
                        linkPreviewMaxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(80.0f);
                    } else {
                        linkPreviewMaxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(122.0f);
                    }
                    if (this.drawShareButton) {
                        linkPreviewMaxWidth -= AndroidUtilities.dp(20.0f);
                    }
                    TL_webPage webPage = (TL_webPage) messageObject.messageOwner.media.webpage;
                    if (!(webPage.site_name == null || webPage.photo == null || !webPage.site_name.toLowerCase().equals("instagram"))) {
                        linkPreviewMaxWidth = Math.max(AndroidUtilities.displaySize.y / DOCUMENT_ATTACH_TYPE_AUDIO, this.currentMessageObject.textWidth);
                    }
                    int additinalWidth = AndroidUtilities.dp(10.0f);
                    int restLinesCount2 = DOCUMENT_ATTACH_TYPE_AUDIO;
                    int additionalHeight = DOCUMENT_ATTACH_TYPE_NONE;
                    linkPreviewMaxWidth -= additinalWidth;
                    if (this.currentMessageObject.photoThumbs == null && webPage.photo != null) {
                        this.currentMessageObject.generateThumbs(true);
                    }
                    z = (webPage.description == null || webPage.type == null || ((!webPage.type.equals(C2DMessaging.EXTRA_APPLICATION_PENDING_INTENT) && !webPage.type.equals(Scopes.PROFILE) && !webPage.type.equals("article")) || this.currentMessageObject.photoThumbs == null)) ? false : true;
                    this.isSmallImage = z;
                    if (webPage.site_name != null) {
                        try {
                            this.siteNameLayout = new StaticLayout(webPage.site_name, replyNamePaint, Math.min((int) Math.ceil((double) replyNamePaint.measureText(webPage.site_name)), linkPreviewMaxWidth), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                            height = this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                            this.linkPreviewHeight += height;
                            this.totalHeight += height;
                            additionalHeight = DOCUMENT_ATTACH_TYPE_NONE + height;
                            width = this.siteNameLayout.getWidth();
                            maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                            maxWebWidth = Math.max(maxWebWidth, width + additinalWidth);
                        } catch (Throwable e2) {
                            FileLog.m13e("tmessages", e2);
                        }
                    }
                    boolean titleIsRTL = false;
                    if (webPage.title != null) {
                        try {
                            this.titleX = ConnectionsManager.DEFAULT_DATACENTER_ID;
                            if (this.linkPreviewHeight != 0) {
                                this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                                this.totalHeight += AndroidUtilities.dp(2.0f);
                            }
                            restLines = DOCUMENT_ATTACH_TYPE_NONE;
                            if (!this.isSmallImage || webPage.description == null) {
                                this.titleLayout = StaticLayoutEx.createStaticLayout(webPage.title, replyNamePaint, linkPreviewMaxWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, (float) AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL), false, TruncateAt.END, linkPreviewMaxWidth, DOCUMENT_ATTACH_TYPE_VIDEO);
                                restLinesCount = DOCUMENT_ATTACH_TYPE_AUDIO;
                            } else {
                                restLines = DOCUMENT_ATTACH_TYPE_AUDIO;
                                this.titleLayout = generateStaticLayout(webPage.title, replyNamePaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.dp(52.0f), DOCUMENT_ATTACH_TYPE_AUDIO, DOCUMENT_ATTACH_TYPE_VIDEO);
                                restLinesCount = DOCUMENT_ATTACH_TYPE_AUDIO - this.titleLayout.getLineCount();
                            }
                            try {
                                height = this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
                                this.linkPreviewHeight += height;
                                this.totalHeight += height;
                                for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.titleLayout.getLineCount(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                    lineLeft = (int) this.titleLayout.getLineLeft(a);
                                    if (lineLeft != 0) {
                                        titleIsRTL = true;
                                    }
                                    if (this.titleX == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                        this.titleX = -lineLeft;
                                    } else {
                                        this.titleX = Math.max(this.titleX, -lineLeft);
                                    }
                                    if (lineLeft != 0) {
                                        width = this.titleLayout.getWidth() - lineLeft;
                                    } else {
                                        width = (int) Math.ceil((double) this.titleLayout.getLineWidth(a));
                                    }
                                    if (a < restLines || (lineLeft != 0 && this.isSmallImage)) {
                                        width += AndroidUtilities.dp(52.0f);
                                    }
                                    maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                                    maxWebWidth = Math.max(maxWebWidth, width + additinalWidth);
                                }
                                restLinesCount2 = restLinesCount;
                            } catch (Exception e3) {
                                e2 = e3;
                            }
                        } catch (Exception e4) {
                            e2 = e4;
                            restLinesCount = DOCUMENT_ATTACH_TYPE_AUDIO;
                            FileLog.m13e("tmessages", e2);
                            restLinesCount2 = restLinesCount;
                            authorIsRTL = false;
                            if (webPage.author == null) {
                                restLinesCount = restLinesCount2;
                            } else {
                                try {
                                    if (this.linkPreviewHeight != 0) {
                                        this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                                        this.totalHeight += AndroidUtilities.dp(2.0f);
                                    }
                                    if (restLinesCount2 == DOCUMENT_ATTACH_TYPE_AUDIO) {
                                    }
                                    this.authorLayout = generateStaticLayout(webPage.author, replyNamePaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.dp(52.0f), restLinesCount2, DOCUMENT_ATTACH_TYPE_DOCUMENT);
                                    restLinesCount = restLinesCount2 - this.authorLayout.getLineCount();
                                    try {
                                        height = this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
                                        this.linkPreviewHeight += height;
                                        this.totalHeight += height;
                                        lineLeft = (int) this.authorLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                                        this.authorX = -lineLeft;
                                        if (lineLeft == 0) {
                                            width = (int) Math.ceil((double) this.authorLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE));
                                        } else {
                                            width = this.authorLayout.getWidth() - lineLeft;
                                            authorIsRTL = true;
                                        }
                                        maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                                        maxWebWidth = Math.max(maxWebWidth, width + additinalWidth);
                                    } catch (Exception e5) {
                                        e2 = e5;
                                        FileLog.m13e("tmessages", e2);
                                        if (webPage.description != null) {
                                            try {
                                                this.descriptionX = DOCUMENT_ATTACH_TYPE_NONE;
                                                this.currentMessageObject.generateLinkDescription();
                                                if (this.linkPreviewHeight != 0) {
                                                    this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                                                    this.totalHeight += AndroidUtilities.dp(2.0f);
                                                }
                                                restLines = DOCUMENT_ATTACH_TYPE_NONE;
                                                if (restLinesCount == DOCUMENT_ATTACH_TYPE_AUDIO) {
                                                }
                                                restLines = restLinesCount;
                                                this.descriptionLayout = generateStaticLayout(messageObject.linkDescription, replyTextPaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.dp(52.0f), restLinesCount, DOCUMENT_ATTACH_TYPE_STICKER);
                                                height = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                                                this.linkPreviewHeight += height;
                                                this.totalHeight += height;
                                                hasRTL = false;
                                                for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.descriptionLayout.getLineCount(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                                    lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                                                    if (lineLeft == 0) {
                                                        hasRTL = true;
                                                        if (this.descriptionX != 0) {
                                                            this.descriptionX = Math.max(this.descriptionX, -lineLeft);
                                                        } else {
                                                            this.descriptionX = -lineLeft;
                                                        }
                                                    }
                                                }
                                                while (a < this.descriptionLayout.getLineCount()) {
                                                    lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                                                    this.descriptionX = DOCUMENT_ATTACH_TYPE_NONE;
                                                    if (lineLeft == 0) {
                                                        width = this.descriptionLayout.getWidth() - lineLeft;
                                                    } else if (hasRTL) {
                                                        width = this.descriptionLayout.getWidth();
                                                    } else {
                                                        width = (int) Math.ceil((double) this.descriptionLayout.getLineWidth(a));
                                                    }
                                                    width += AndroidUtilities.dp(52.0f);
                                                    if (maxWebWidth >= width + additinalWidth) {
                                                        if (titleIsRTL) {
                                                            this.titleX += (width + additinalWidth) - maxWebWidth;
                                                        }
                                                        if (authorIsRTL) {
                                                            this.authorX += (width + additinalWidth) - maxWebWidth;
                                                        }
                                                        maxWebWidth = width + additinalWidth;
                                                    }
                                                    maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                                                }
                                            } catch (Throwable e22) {
                                                FileLog.m13e("tmessages", e22);
                                            }
                                        }
                                        if (webPage.type == null) {
                                        }
                                        smallImage = false;
                                        this.isSmallImage = false;
                                        if (smallImage) {
                                            maxPhotoWidth = AndroidUtilities.dp(48.0f);
                                        } else {
                                            maxPhotoWidth = linkPreviewMaxWidth;
                                        }
                                        if (webPage.document == null) {
                                            if (webPage.photo != null) {
                                                if (webPage.type == null) {
                                                }
                                                this.drawImageButton = z;
                                                arrayList = messageObject.photoThumbs;
                                                if (this.drawImageButton) {
                                                    photoSize = AndroidUtilities.getPhotoSize();
                                                } else {
                                                    photoSize = maxPhotoWidth;
                                                }
                                                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, photoSize, this.drawImageButton);
                                                this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                                                if (this.currentPhotoObjectThumb == this.currentPhotoObject) {
                                                    this.currentPhotoObjectThumb = null;
                                                }
                                            }
                                            maxWidth = maxWidth2;
                                        } else {
                                            document = webPage.document;
                                            if (!MessageObject.isGifDocument(document)) {
                                                if (!MediaController.getInstance().canAutoplayGifs()) {
                                                    messageObject.audioProgress = TouchHelperCallback.ALPHA_FULL;
                                                }
                                                this.photoImage.setAllowStartAnimation(messageObject.audioProgress != TouchHelperCallback.ALPHA_FULL);
                                                this.currentPhotoObject = document.thumb;
                                                while (a < document.attributes.size()) {
                                                    attribute = (DocumentAttribute) document.attributes.get(a);
                                                    if (attribute instanceof TL_documentAttributeImageSize) {
                                                    }
                                                    this.currentPhotoObject.f34w = attribute.f29w;
                                                    this.currentPhotoObject.f33h = attribute.f28h;
                                                    break;
                                                    photoSize2 = this.currentPhotoObject;
                                                    photoSize3 = this.currentPhotoObject;
                                                    dp = AndroidUtilities.dp(150.0f);
                                                    photoSize3.f33h = dp;
                                                    photoSize2.f34w = dp;
                                                    this.documentAttachType = DOCUMENT_ATTACH_TYPE_GIF;
                                                }
                                                photoSize2 = this.currentPhotoObject;
                                                photoSize3 = this.currentPhotoObject;
                                                dp = AndroidUtilities.dp(150.0f);
                                                photoSize3.f33h = dp;
                                                photoSize2.f34w = dp;
                                                this.documentAttachType = DOCUMENT_ATTACH_TYPE_GIF;
                                            } else if (!MessageObject.isVideoDocument(document)) {
                                                this.currentPhotoObject = document.thumb;
                                                while (a < document.attributes.size()) {
                                                    attribute = (DocumentAttribute) document.attributes.get(a);
                                                    if (attribute instanceof TL_documentAttributeVideo) {
                                                        this.currentPhotoObject.f34w = attribute.f29w;
                                                        this.currentPhotoObject.f33h = attribute.f28h;
                                                        break;
                                                        photoSize2 = this.currentPhotoObject;
                                                        photoSize3 = this.currentPhotoObject;
                                                        dp = AndroidUtilities.dp(150.0f);
                                                        photoSize3.f33h = dp;
                                                        photoSize2.f34w = dp;
                                                        createDocumentLayout(DOCUMENT_ATTACH_TYPE_NONE, messageObject);
                                                    } else {
                                                    }
                                                }
                                                photoSize2 = this.currentPhotoObject;
                                                photoSize3 = this.currentPhotoObject;
                                                dp = AndroidUtilities.dp(150.0f);
                                                photoSize3.f33h = dp;
                                                photoSize2.f34w = dp;
                                                createDocumentLayout(DOCUMENT_ATTACH_TYPE_NONE, messageObject);
                                            } else if (MessageObject.isStickerDocument(document)) {
                                                this.currentPhotoObject = document.thumb;
                                                while (a < document.attributes.size()) {
                                                    attribute = (DocumentAttribute) document.attributes.get(a);
                                                    if (attribute instanceof TL_documentAttributeImageSize) {
                                                        this.currentPhotoObject.f34w = attribute.f29w;
                                                        this.currentPhotoObject.f33h = attribute.f28h;
                                                        break;
                                                        photoSize2 = this.currentPhotoObject;
                                                        photoSize3 = this.currentPhotoObject;
                                                        dp = AndroidUtilities.dp(150.0f);
                                                        photoSize3.f33h = dp;
                                                        photoSize2.f34w = dp;
                                                        this.documentAttach = document;
                                                        this.documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
                                                    } else {
                                                    }
                                                }
                                                photoSize2 = this.currentPhotoObject;
                                                photoSize3 = this.currentPhotoObject;
                                                dp = AndroidUtilities.dp(150.0f);
                                                photoSize3.f33h = dp;
                                                photoSize2.f34w = dp;
                                                this.documentAttach = document;
                                                this.documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
                                            } else {
                                                calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                                if (!MessageObject.isStickerDocument(document)) {
                                                    if (this.backgroundWidth < AndroidUtilities.dp(20.0f) + maxWidth2) {
                                                        this.backgroundWidth = AndroidUtilities.dp(20.0f) + maxWidth2;
                                                    }
                                                    if (!MessageObject.isVoiceDocument(document)) {
                                                        createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(10.0f), messageObject);
                                                        this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                                        this.totalHeight += AndroidUtilities.dp(44.0f);
                                                        this.linkPreviewHeight += AndroidUtilities.dp(44.0f);
                                                        calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                                    } else if (MessageObject.isMusicDocument(document)) {
                                                        durationWidth = createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(10.0f), messageObject);
                                                        this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                                        this.totalHeight += AndroidUtilities.dp(56.0f);
                                                        this.linkPreviewHeight += AndroidUtilities.dp(56.0f);
                                                        maxWidth2 -= AndroidUtilities.dp(86.0f);
                                                        maxChildWidth = Math.max(maxChildWidth, (durationWidth + additinalWidth) + AndroidUtilities.dp(94.0f));
                                                        maxChildWidth = (int) Math.max((float) maxChildWidth, (this.songLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE) + ((float) additinalWidth)) + ((float) AndroidUtilities.dp(86.0f)));
                                                        maxChildWidth = (int) Math.max((float) maxChildWidth, (this.performerLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE) + ((float) additinalWidth)) + ((float) AndroidUtilities.dp(86.0f)));
                                                        calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                                    } else {
                                                        createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(168.0f), messageObject);
                                                        this.drawImageButton = true;
                                                        if (this.drawPhotoImage) {
                                                            this.totalHeight += AndroidUtilities.dp(100.0f);
                                                            this.linkPreviewHeight += AndroidUtilities.dp(86.0f);
                                                            this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, this.totalHeight + this.namesOffset, AndroidUtilities.dp(86.0f), AndroidUtilities.dp(86.0f));
                                                        } else {
                                                            this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                                            this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, (this.totalHeight + this.namesOffset) - AndroidUtilities.dp(14.0f), AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                                                            this.totalHeight += AndroidUtilities.dp(64.0f);
                                                            this.linkPreviewHeight += AndroidUtilities.dp(50.0f);
                                                        }
                                                    }
                                                }
                                            }
                                            maxWidth = maxWidth2;
                                        }
                                        if (this.currentPhotoObject == null) {
                                            this.photoImage.setImageBitmap((Drawable) null);
                                            this.linkPreviewHeight -= AndroidUtilities.dp(6.0f);
                                            this.totalHeight += AndroidUtilities.dp(4.0f);
                                        } else {
                                            if (webPage.type == null) {
                                            }
                                            this.drawImageButton = z;
                                            if (this.linkPreviewHeight != 0) {
                                                this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                                                this.totalHeight += AndroidUtilities.dp(2.0f);
                                            }
                                            if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER) {
                                                if (AndroidUtilities.isTablet()) {
                                                    maxPhotoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                                                } else {
                                                    maxPhotoWidth = (int) (((float) AndroidUtilities.displaySize.x) * 0.5f);
                                                }
                                            }
                                            maxChildWidth = Math.max(maxChildWidth, maxPhotoWidth + additinalWidth);
                                            this.currentPhotoObject.size = -1;
                                            if (this.currentPhotoObjectThumb != null) {
                                                this.currentPhotoObjectThumb.size = -1;
                                            }
                                            if (smallImage) {
                                                height = maxPhotoWidth;
                                                width = maxPhotoWidth;
                                            } else {
                                                width = this.currentPhotoObject.f34w;
                                                scale = ((float) width) / ((float) (maxPhotoWidth - AndroidUtilities.dp(2.0f)));
                                                width = (int) (((float) width) / scale);
                                                height = (int) (((float) this.currentPhotoObject.f33h) / scale);
                                                height = AndroidUtilities.displaySize.y / DOCUMENT_ATTACH_TYPE_AUDIO;
                                            }
                                            if (this.isSmallImage) {
                                                if (AndroidUtilities.dp(50.0f) + additionalHeight > this.linkPreviewHeight) {
                                                    this.totalHeight += ((AndroidUtilities.dp(50.0f) + additionalHeight) - this.linkPreviewHeight) + AndroidUtilities.dp(8.0f);
                                                    this.linkPreviewHeight = AndroidUtilities.dp(50.0f) + additionalHeight;
                                                }
                                                this.linkPreviewHeight -= AndroidUtilities.dp(8.0f);
                                            } else {
                                                this.totalHeight += AndroidUtilities.dp(12.0f) + height;
                                                this.linkPreviewHeight += height;
                                            }
                                            this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, DOCUMENT_ATTACH_TYPE_NONE, width, height);
                                            objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                            objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(width);
                                            objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(height);
                                            this.currentPhotoFilter = String.format(Locale.US, "%d_%d", objArr2);
                                            objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                            objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(width);
                                            objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(height);
                                            this.currentPhotoFilterThumb = String.format(Locale.US, "%d_%d_b", objArr2);
                                            if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_STICKER) {
                                                imageReceiver = this.photoImage;
                                                tLObject = this.documentAttach;
                                                str = this.currentPhotoFilter;
                                                if (this.currentPhotoObject == null) {
                                                    fileLocation = null;
                                                } else {
                                                    fileLocation = this.currentPhotoObject.location;
                                                }
                                                imageReceiver.setImage(tLObject, null, str, null, fileLocation, "b1", this.documentAttach.size, "webp", true);
                                            } else if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_VIDEO) {
                                                this.photoImage.setImage(null, null, this.currentPhotoObject.location, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                                            } else if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF) {
                                                photoExist = messageObject.mediaExists;
                                                fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                                                if (!photoExist) {
                                                }
                                                this.photoNotSet = false;
                                                this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb == null ? null : this.currentPhotoObjectThumb.location, this.currentPhotoFilterThumb, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                                            } else {
                                                photoExist = messageObject.mediaExists;
                                                fileName = FileLoader.getAttachFileName(webPage.document);
                                                if (!photoExist) {
                                                }
                                                this.photoNotSet = false;
                                                this.photoImage.setImage(webPage.document, null, this.currentPhotoObject.location, this.currentPhotoFilter, webPage.document.size, null, false);
                                            }
                                            this.drawPhotoImage = true;
                                            minutes = webPage.duration / 60;
                                            seconds = webPage.duration - (minutes * 60);
                                            objArr = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                            objArr[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(minutes);
                                            objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(seconds);
                                            str2 = String.format("%d:%02d", objArr);
                                            this.durationWidth = (int) Math.ceil((double) durationPaint.measureText(str2));
                                            this.videoInfoLayout = new StaticLayout(str2, durationPaint, this.durationWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                                        }
                                        calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                                        maxWidth2 = maxWidth;
                                        width = this.backgroundWidth - AndroidUtilities.dp(31.0f);
                                        this.captionLayout = new StaticLayout(messageObject.caption, MessageObject.getTextPaint(), width - AndroidUtilities.dp(10.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                                        if (this.captionLayout.getLineCount() > 0) {
                                            timeWidthTotal = this.timeWidth + (messageObject.isOutOwner() ? DOCUMENT_ATTACH_TYPE_NONE : AndroidUtilities.dp(20.0f));
                                            this.captionHeight = this.captionLayout.getHeight();
                                            this.totalHeight += this.captionHeight + AndroidUtilities.dp(9.0f);
                                            if (((float) (width - AndroidUtilities.dp(8.0f))) - (this.captionLayout.getLineWidth(this.captionLayout.getLineCount() - 1) + this.captionLayout.getLineLeft(this.captionLayout.getLineCount() - 1)) < ((float) timeWidthTotal)) {
                                                this.totalHeight += AndroidUtilities.dp(14.0f);
                                                this.captionHeight += AndroidUtilities.dp(14.0f);
                                            }
                                        }
                                        this.botButtons.clear();
                                        if (messageIdChanged) {
                                            this.botButtonsByData.clear();
                                        }
                                        if (messageObject.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                            rows = messageObject.messageOwner.reply_markup.rows.size();
                                            photoSize = (AndroidUtilities.dp(48.0f) * rows) + AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                                            this.keyboardHeight = photoSize;
                                            this.substractBackgroundHeight = photoSize;
                                            this.widthForButtons = this.backgroundWidth;
                                            fullWidth = false;
                                            if (messageObject.wantedBotKeyboardWidth > this.widthForButtons) {
                                                if (this.isChat) {
                                                }
                                                maxButtonWidth = -AndroidUtilities.dp(f);
                                                if (AndroidUtilities.isTablet()) {
                                                    maxButtonWidth += AndroidUtilities.getMinTabletSide();
                                                } else {
                                                    maxButtonWidth += Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                                                }
                                                this.widthForButtons = Math.max(this.backgroundWidth, Math.min(messageObject.wantedBotKeyboardWidth, maxButtonWidth));
                                                fullWidth = true;
                                            }
                                            maxButtonsWidth = DOCUMENT_ATTACH_TYPE_NONE;
                                            for (a = DOCUMENT_ATTACH_TYPE_NONE; a < rows; a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                                row = (TL_keyboardButtonRow) messageObject.messageOwner.reply_markup.rows.get(a);
                                                buttonsCount = row.buttons.size();
                                                dp2 = this.widthForButtons - (AndroidUtilities.dp(5.0f) * (buttonsCount - 1));
                                                if (!fullWidth) {
                                                }
                                                buttonWidth = ((dp2 - AndroidUtilities.dp(f)) - AndroidUtilities.dp(2.0f)) / buttonsCount;
                                                for (b = DOCUMENT_ATTACH_TYPE_NONE; b < row.buttons.size(); b += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                                    chatMessageCell = this;
                                                    botButton = new BotButton();
                                                    botButton.button = (KeyboardButton) row.buttons.get(b);
                                                    key = Utilities.bytesToHex(botButton.button.data);
                                                    oldButton = (BotButton) this.botButtonsByData.get(key);
                                                    if (oldButton == null) {
                                                        botButton.lastUpdateTime = System.currentTimeMillis();
                                                    } else {
                                                        botButton.progressAlpha = oldButton.progressAlpha;
                                                        botButton.angle = oldButton.angle;
                                                        botButton.lastUpdateTime = oldButton.lastUpdateTime;
                                                    }
                                                    this.botButtonsByData.put(key, botButton);
                                                    botButton.f11x = (AndroidUtilities.dp(5.0f) + buttonWidth) * b;
                                                    botButton.f12y = (AndroidUtilities.dp(48.0f) * a) + AndroidUtilities.dp(5.0f);
                                                    botButton.width = buttonWidth;
                                                    botButton.height = AndroidUtilities.dp(44.0f);
                                                    botButton.title = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(botButton.button.text, botButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), botButtonPaint, (float) (buttonWidth - AndroidUtilities.dp(10.0f)), TruncateAt.END), botButtonPaint, buttonWidth - AndroidUtilities.dp(10.0f), Alignment.ALIGN_CENTER, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                                                    this.botButtons.add(botButton);
                                                    if (b != row.buttons.size() - 1) {
                                                        maxButtonsWidth = Math.max(maxButtonsWidth, botButton.f11x + botButton.width);
                                                    }
                                                }
                                            }
                                            this.widthForButtons = maxButtonsWidth;
                                        } else {
                                            this.substractBackgroundHeight = DOCUMENT_ATTACH_TYPE_NONE;
                                            this.keyboardHeight = DOCUMENT_ATTACH_TYPE_NONE;
                                        }
                                        updateWaveform();
                                        updateButtonState(dataChanged);
                                    }
                                } catch (Exception e6) {
                                    e22 = e6;
                                    restLinesCount = restLinesCount2;
                                    FileLog.m13e("tmessages", e22);
                                    if (webPage.description != null) {
                                        this.descriptionX = DOCUMENT_ATTACH_TYPE_NONE;
                                        this.currentMessageObject.generateLinkDescription();
                                        if (this.linkPreviewHeight != 0) {
                                            this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                                            this.totalHeight += AndroidUtilities.dp(2.0f);
                                        }
                                        restLines = DOCUMENT_ATTACH_TYPE_NONE;
                                        if (restLinesCount == DOCUMENT_ATTACH_TYPE_AUDIO) {
                                        }
                                        restLines = restLinesCount;
                                        this.descriptionLayout = generateStaticLayout(messageObject.linkDescription, replyTextPaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.dp(52.0f), restLinesCount, DOCUMENT_ATTACH_TYPE_STICKER);
                                        height = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                                        this.linkPreviewHeight += height;
                                        this.totalHeight += height;
                                        hasRTL = false;
                                        for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.descriptionLayout.getLineCount(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                            lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                                            if (lineLeft == 0) {
                                                hasRTL = true;
                                                if (this.descriptionX != 0) {
                                                    this.descriptionX = Math.max(this.descriptionX, -lineLeft);
                                                } else {
                                                    this.descriptionX = -lineLeft;
                                                }
                                            }
                                        }
                                        while (a < this.descriptionLayout.getLineCount()) {
                                            lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                                            this.descriptionX = DOCUMENT_ATTACH_TYPE_NONE;
                                            if (lineLeft == 0) {
                                                width = this.descriptionLayout.getWidth() - lineLeft;
                                            } else if (hasRTL) {
                                                width = (int) Math.ceil((double) this.descriptionLayout.getLineWidth(a));
                                            } else {
                                                width = this.descriptionLayout.getWidth();
                                            }
                                            width += AndroidUtilities.dp(52.0f);
                                            if (maxWebWidth >= width + additinalWidth) {
                                                if (titleIsRTL) {
                                                    this.titleX += (width + additinalWidth) - maxWebWidth;
                                                }
                                                if (authorIsRTL) {
                                                    this.authorX += (width + additinalWidth) - maxWebWidth;
                                                }
                                                maxWebWidth = width + additinalWidth;
                                            }
                                            maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                                        }
                                    }
                                    if (webPage.type == null) {
                                    }
                                    smallImage = false;
                                    this.isSmallImage = false;
                                    if (smallImage) {
                                        maxPhotoWidth = linkPreviewMaxWidth;
                                    } else {
                                        maxPhotoWidth = AndroidUtilities.dp(48.0f);
                                    }
                                    if (webPage.document == null) {
                                        if (webPage.photo != null) {
                                            if (webPage.type == null) {
                                            }
                                            this.drawImageButton = z;
                                            arrayList = messageObject.photoThumbs;
                                            if (this.drawImageButton) {
                                                photoSize = maxPhotoWidth;
                                            } else {
                                                photoSize = AndroidUtilities.getPhotoSize();
                                            }
                                            if (this.drawImageButton) {
                                            }
                                            this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, photoSize, this.drawImageButton);
                                            this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                                            if (this.currentPhotoObjectThumb == this.currentPhotoObject) {
                                                this.currentPhotoObjectThumb = null;
                                            }
                                        }
                                        maxWidth = maxWidth2;
                                    } else {
                                        document = webPage.document;
                                        if (!MessageObject.isGifDocument(document)) {
                                            if (MediaController.getInstance().canAutoplayGifs()) {
                                                messageObject.audioProgress = TouchHelperCallback.ALPHA_FULL;
                                            }
                                            if (messageObject.audioProgress != TouchHelperCallback.ALPHA_FULL) {
                                            }
                                            this.photoImage.setAllowStartAnimation(messageObject.audioProgress != TouchHelperCallback.ALPHA_FULL);
                                            this.currentPhotoObject = document.thumb;
                                            while (a < document.attributes.size()) {
                                                attribute = (DocumentAttribute) document.attributes.get(a);
                                                if (attribute instanceof TL_documentAttributeImageSize) {
                                                }
                                                this.currentPhotoObject.f34w = attribute.f29w;
                                                this.currentPhotoObject.f33h = attribute.f28h;
                                                break;
                                                photoSize2 = this.currentPhotoObject;
                                                photoSize3 = this.currentPhotoObject;
                                                dp = AndroidUtilities.dp(150.0f);
                                                photoSize3.f33h = dp;
                                                photoSize2.f34w = dp;
                                                this.documentAttachType = DOCUMENT_ATTACH_TYPE_GIF;
                                            }
                                            photoSize2 = this.currentPhotoObject;
                                            photoSize3 = this.currentPhotoObject;
                                            dp = AndroidUtilities.dp(150.0f);
                                            photoSize3.f33h = dp;
                                            photoSize2.f34w = dp;
                                            this.documentAttachType = DOCUMENT_ATTACH_TYPE_GIF;
                                        } else if (!MessageObject.isVideoDocument(document)) {
                                            this.currentPhotoObject = document.thumb;
                                            while (a < document.attributes.size()) {
                                                attribute = (DocumentAttribute) document.attributes.get(a);
                                                if (attribute instanceof TL_documentAttributeVideo) {
                                                } else {
                                                    this.currentPhotoObject.f34w = attribute.f29w;
                                                    this.currentPhotoObject.f33h = attribute.f28h;
                                                    break;
                                                    photoSize2 = this.currentPhotoObject;
                                                    photoSize3 = this.currentPhotoObject;
                                                    dp = AndroidUtilities.dp(150.0f);
                                                    photoSize3.f33h = dp;
                                                    photoSize2.f34w = dp;
                                                    createDocumentLayout(DOCUMENT_ATTACH_TYPE_NONE, messageObject);
                                                }
                                            }
                                            photoSize2 = this.currentPhotoObject;
                                            photoSize3 = this.currentPhotoObject;
                                            dp = AndroidUtilities.dp(150.0f);
                                            photoSize3.f33h = dp;
                                            photoSize2.f34w = dp;
                                            createDocumentLayout(DOCUMENT_ATTACH_TYPE_NONE, messageObject);
                                        } else if (MessageObject.isStickerDocument(document)) {
                                            calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                            if (MessageObject.isStickerDocument(document)) {
                                                if (this.backgroundWidth < AndroidUtilities.dp(20.0f) + maxWidth2) {
                                                    this.backgroundWidth = AndroidUtilities.dp(20.0f) + maxWidth2;
                                                }
                                                if (!MessageObject.isVoiceDocument(document)) {
                                                    createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(10.0f), messageObject);
                                                    this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                                    this.totalHeight += AndroidUtilities.dp(44.0f);
                                                    this.linkPreviewHeight += AndroidUtilities.dp(44.0f);
                                                    calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                                } else if (MessageObject.isMusicDocument(document)) {
                                                    createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(168.0f), messageObject);
                                                    this.drawImageButton = true;
                                                    if (this.drawPhotoImage) {
                                                        this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                                        this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, (this.totalHeight + this.namesOffset) - AndroidUtilities.dp(14.0f), AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                                                        this.totalHeight += AndroidUtilities.dp(64.0f);
                                                        this.linkPreviewHeight += AndroidUtilities.dp(50.0f);
                                                    } else {
                                                        this.totalHeight += AndroidUtilities.dp(100.0f);
                                                        this.linkPreviewHeight += AndroidUtilities.dp(86.0f);
                                                        this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, this.totalHeight + this.namesOffset, AndroidUtilities.dp(86.0f), AndroidUtilities.dp(86.0f));
                                                    }
                                                } else {
                                                    durationWidth = createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(10.0f), messageObject);
                                                    this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                                    this.totalHeight += AndroidUtilities.dp(56.0f);
                                                    this.linkPreviewHeight += AndroidUtilities.dp(56.0f);
                                                    maxWidth2 -= AndroidUtilities.dp(86.0f);
                                                    maxChildWidth = Math.max(maxChildWidth, (durationWidth + additinalWidth) + AndroidUtilities.dp(94.0f));
                                                    maxChildWidth = (int) Math.max((float) maxChildWidth, (this.songLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE) + ((float) additinalWidth)) + ((float) AndroidUtilities.dp(86.0f)));
                                                    maxChildWidth = (int) Math.max((float) maxChildWidth, (this.performerLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE) + ((float) additinalWidth)) + ((float) AndroidUtilities.dp(86.0f)));
                                                    calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                                }
                                            }
                                        } else {
                                            this.currentPhotoObject = document.thumb;
                                            while (a < document.attributes.size()) {
                                                attribute = (DocumentAttribute) document.attributes.get(a);
                                                if (attribute instanceof TL_documentAttributeImageSize) {
                                                } else {
                                                    this.currentPhotoObject.f34w = attribute.f29w;
                                                    this.currentPhotoObject.f33h = attribute.f28h;
                                                    break;
                                                    photoSize2 = this.currentPhotoObject;
                                                    photoSize3 = this.currentPhotoObject;
                                                    dp = AndroidUtilities.dp(150.0f);
                                                    photoSize3.f33h = dp;
                                                    photoSize2.f34w = dp;
                                                    this.documentAttach = document;
                                                    this.documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
                                                }
                                            }
                                            photoSize2 = this.currentPhotoObject;
                                            photoSize3 = this.currentPhotoObject;
                                            dp = AndroidUtilities.dp(150.0f);
                                            photoSize3.f33h = dp;
                                            photoSize2.f34w = dp;
                                            this.documentAttach = document;
                                            this.documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
                                        }
                                        maxWidth = maxWidth2;
                                    }
                                    if (this.currentPhotoObject == null) {
                                        this.photoImage.setImageBitmap((Drawable) null);
                                        this.linkPreviewHeight -= AndroidUtilities.dp(6.0f);
                                        this.totalHeight += AndroidUtilities.dp(4.0f);
                                    } else {
                                        if (webPage.type == null) {
                                        }
                                        this.drawImageButton = z;
                                        if (this.linkPreviewHeight != 0) {
                                            this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                                            this.totalHeight += AndroidUtilities.dp(2.0f);
                                        }
                                        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER) {
                                            if (AndroidUtilities.isTablet()) {
                                                maxPhotoWidth = (int) (((float) AndroidUtilities.displaySize.x) * 0.5f);
                                            } else {
                                                maxPhotoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                                            }
                                        }
                                        maxChildWidth = Math.max(maxChildWidth, maxPhotoWidth + additinalWidth);
                                        this.currentPhotoObject.size = -1;
                                        if (this.currentPhotoObjectThumb != null) {
                                            this.currentPhotoObjectThumb.size = -1;
                                        }
                                        if (smallImage) {
                                            width = this.currentPhotoObject.f34w;
                                            scale = ((float) width) / ((float) (maxPhotoWidth - AndroidUtilities.dp(2.0f)));
                                            width = (int) (((float) width) / scale);
                                            height = (int) (((float) this.currentPhotoObject.f33h) / scale);
                                            height = AndroidUtilities.displaySize.y / DOCUMENT_ATTACH_TYPE_AUDIO;
                                        } else {
                                            height = maxPhotoWidth;
                                            width = maxPhotoWidth;
                                        }
                                        if (this.isSmallImage) {
                                            this.totalHeight += AndroidUtilities.dp(12.0f) + height;
                                            this.linkPreviewHeight += height;
                                        } else {
                                            if (AndroidUtilities.dp(50.0f) + additionalHeight > this.linkPreviewHeight) {
                                                this.totalHeight += ((AndroidUtilities.dp(50.0f) + additionalHeight) - this.linkPreviewHeight) + AndroidUtilities.dp(8.0f);
                                                this.linkPreviewHeight = AndroidUtilities.dp(50.0f) + additionalHeight;
                                            }
                                            this.linkPreviewHeight -= AndroidUtilities.dp(8.0f);
                                        }
                                        this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, DOCUMENT_ATTACH_TYPE_NONE, width, height);
                                        objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                        objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(width);
                                        objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(height);
                                        this.currentPhotoFilter = String.format(Locale.US, "%d_%d", objArr2);
                                        objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                        objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(width);
                                        objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(height);
                                        this.currentPhotoFilterThumb = String.format(Locale.US, "%d_%d_b", objArr2);
                                        if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_STICKER) {
                                            imageReceiver = this.photoImage;
                                            tLObject = this.documentAttach;
                                            str = this.currentPhotoFilter;
                                            if (this.currentPhotoObject == null) {
                                                fileLocation = null;
                                            } else {
                                                fileLocation = this.currentPhotoObject.location;
                                            }
                                            imageReceiver.setImage(tLObject, null, str, null, fileLocation, "b1", this.documentAttach.size, "webp", true);
                                        } else if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_VIDEO) {
                                            this.photoImage.setImage(null, null, this.currentPhotoObject.location, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                                        } else if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF) {
                                            photoExist = messageObject.mediaExists;
                                            fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                                            if (photoExist) {
                                            }
                                            this.photoNotSet = false;
                                            if (this.currentPhotoObjectThumb == null) {
                                            }
                                            this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb == null ? null : this.currentPhotoObjectThumb.location, this.currentPhotoFilterThumb, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                                        } else {
                                            photoExist = messageObject.mediaExists;
                                            fileName = FileLoader.getAttachFileName(webPage.document);
                                            if (photoExist) {
                                            }
                                            this.photoNotSet = false;
                                            this.photoImage.setImage(webPage.document, null, this.currentPhotoObject.location, this.currentPhotoFilter, webPage.document.size, null, false);
                                        }
                                        this.drawPhotoImage = true;
                                        minutes = webPage.duration / 60;
                                        seconds = webPage.duration - (minutes * 60);
                                        objArr = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                        objArr[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(minutes);
                                        objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(seconds);
                                        str2 = String.format("%d:%02d", objArr);
                                        this.durationWidth = (int) Math.ceil((double) durationPaint.measureText(str2));
                                        this.videoInfoLayout = new StaticLayout(str2, durationPaint, this.durationWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                                    }
                                    calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                                    maxWidth2 = maxWidth;
                                    width = this.backgroundWidth - AndroidUtilities.dp(31.0f);
                                    this.captionLayout = new StaticLayout(messageObject.caption, MessageObject.getTextPaint(), width - AndroidUtilities.dp(10.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                                    if (this.captionLayout.getLineCount() > 0) {
                                        if (messageObject.isOutOwner()) {
                                        }
                                        timeWidthTotal = this.timeWidth + (messageObject.isOutOwner() ? DOCUMENT_ATTACH_TYPE_NONE : AndroidUtilities.dp(20.0f));
                                        this.captionHeight = this.captionLayout.getHeight();
                                        this.totalHeight += this.captionHeight + AndroidUtilities.dp(9.0f);
                                        if (((float) (width - AndroidUtilities.dp(8.0f))) - (this.captionLayout.getLineWidth(this.captionLayout.getLineCount() - 1) + this.captionLayout.getLineLeft(this.captionLayout.getLineCount() - 1)) < ((float) timeWidthTotal)) {
                                            this.totalHeight += AndroidUtilities.dp(14.0f);
                                            this.captionHeight += AndroidUtilities.dp(14.0f);
                                        }
                                    }
                                    this.botButtons.clear();
                                    if (messageIdChanged) {
                                        this.botButtonsByData.clear();
                                    }
                                    if (messageObject.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                        this.substractBackgroundHeight = DOCUMENT_ATTACH_TYPE_NONE;
                                        this.keyboardHeight = DOCUMENT_ATTACH_TYPE_NONE;
                                    } else {
                                        rows = messageObject.messageOwner.reply_markup.rows.size();
                                        photoSize = (AndroidUtilities.dp(48.0f) * rows) + AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                                        this.keyboardHeight = photoSize;
                                        this.substractBackgroundHeight = photoSize;
                                        this.widthForButtons = this.backgroundWidth;
                                        fullWidth = false;
                                        if (messageObject.wantedBotKeyboardWidth > this.widthForButtons) {
                                            if (this.isChat) {
                                            }
                                            maxButtonWidth = -AndroidUtilities.dp(f);
                                            if (AndroidUtilities.isTablet()) {
                                                maxButtonWidth += Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                                            } else {
                                                maxButtonWidth += AndroidUtilities.getMinTabletSide();
                                            }
                                            this.widthForButtons = Math.max(this.backgroundWidth, Math.min(messageObject.wantedBotKeyboardWidth, maxButtonWidth));
                                            fullWidth = true;
                                        }
                                        maxButtonsWidth = DOCUMENT_ATTACH_TYPE_NONE;
                                        for (a = DOCUMENT_ATTACH_TYPE_NONE; a < rows; a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                            row = (TL_keyboardButtonRow) messageObject.messageOwner.reply_markup.rows.get(a);
                                            buttonsCount = row.buttons.size();
                                            dp2 = this.widthForButtons - (AndroidUtilities.dp(5.0f) * (buttonsCount - 1));
                                            if (fullWidth) {
                                            }
                                            buttonWidth = ((dp2 - AndroidUtilities.dp(f)) - AndroidUtilities.dp(2.0f)) / buttonsCount;
                                            for (b = DOCUMENT_ATTACH_TYPE_NONE; b < row.buttons.size(); b += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                                chatMessageCell = this;
                                                botButton = new BotButton();
                                                botButton.button = (KeyboardButton) row.buttons.get(b);
                                                key = Utilities.bytesToHex(botButton.button.data);
                                                oldButton = (BotButton) this.botButtonsByData.get(key);
                                                if (oldButton == null) {
                                                    botButton.lastUpdateTime = System.currentTimeMillis();
                                                } else {
                                                    botButton.progressAlpha = oldButton.progressAlpha;
                                                    botButton.angle = oldButton.angle;
                                                    botButton.lastUpdateTime = oldButton.lastUpdateTime;
                                                }
                                                this.botButtonsByData.put(key, botButton);
                                                botButton.f11x = (AndroidUtilities.dp(5.0f) + buttonWidth) * b;
                                                botButton.f12y = (AndroidUtilities.dp(48.0f) * a) + AndroidUtilities.dp(5.0f);
                                                botButton.width = buttonWidth;
                                                botButton.height = AndroidUtilities.dp(44.0f);
                                                botButton.title = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(botButton.button.text, botButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), botButtonPaint, (float) (buttonWidth - AndroidUtilities.dp(10.0f)), TruncateAt.END), botButtonPaint, buttonWidth - AndroidUtilities.dp(10.0f), Alignment.ALIGN_CENTER, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                                                this.botButtons.add(botButton);
                                                if (b != row.buttons.size() - 1) {
                                                    maxButtonsWidth = Math.max(maxButtonsWidth, botButton.f11x + botButton.width);
                                                }
                                            }
                                        }
                                        this.widthForButtons = maxButtonsWidth;
                                    }
                                    updateWaveform();
                                    updateButtonState(dataChanged);
                                }
                            }
                            if (webPage.description != null) {
                                this.descriptionX = DOCUMENT_ATTACH_TYPE_NONE;
                                this.currentMessageObject.generateLinkDescription();
                                if (this.linkPreviewHeight != 0) {
                                    this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                                    this.totalHeight += AndroidUtilities.dp(2.0f);
                                }
                                restLines = DOCUMENT_ATTACH_TYPE_NONE;
                                if (restLinesCount == DOCUMENT_ATTACH_TYPE_AUDIO) {
                                }
                                restLines = restLinesCount;
                                this.descriptionLayout = generateStaticLayout(messageObject.linkDescription, replyTextPaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.dp(52.0f), restLinesCount, DOCUMENT_ATTACH_TYPE_STICKER);
                                height = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                                this.linkPreviewHeight += height;
                                this.totalHeight += height;
                                hasRTL = false;
                                for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.descriptionLayout.getLineCount(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                    lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                                    if (lineLeft == 0) {
                                        hasRTL = true;
                                        if (this.descriptionX != 0) {
                                            this.descriptionX = -lineLeft;
                                        } else {
                                            this.descriptionX = Math.max(this.descriptionX, -lineLeft);
                                        }
                                    }
                                }
                                while (a < this.descriptionLayout.getLineCount()) {
                                    lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                                    this.descriptionX = DOCUMENT_ATTACH_TYPE_NONE;
                                    if (lineLeft == 0) {
                                        width = this.descriptionLayout.getWidth() - lineLeft;
                                    } else if (hasRTL) {
                                        width = this.descriptionLayout.getWidth();
                                    } else {
                                        width = (int) Math.ceil((double) this.descriptionLayout.getLineWidth(a));
                                    }
                                    width += AndroidUtilities.dp(52.0f);
                                    if (maxWebWidth >= width + additinalWidth) {
                                        if (titleIsRTL) {
                                            this.titleX += (width + additinalWidth) - maxWebWidth;
                                        }
                                        if (authorIsRTL) {
                                            this.authorX += (width + additinalWidth) - maxWebWidth;
                                        }
                                        maxWebWidth = width + additinalWidth;
                                    }
                                    maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                                }
                            }
                            if (webPage.type == null) {
                            }
                            smallImage = false;
                            this.isSmallImage = false;
                            if (smallImage) {
                                maxPhotoWidth = AndroidUtilities.dp(48.0f);
                            } else {
                                maxPhotoWidth = linkPreviewMaxWidth;
                            }
                            if (webPage.document == null) {
                                document = webPage.document;
                                if (!MessageObject.isGifDocument(document)) {
                                    if (MediaController.getInstance().canAutoplayGifs()) {
                                        messageObject.audioProgress = TouchHelperCallback.ALPHA_FULL;
                                    }
                                    if (messageObject.audioProgress != TouchHelperCallback.ALPHA_FULL) {
                                    }
                                    this.photoImage.setAllowStartAnimation(messageObject.audioProgress != TouchHelperCallback.ALPHA_FULL);
                                    this.currentPhotoObject = document.thumb;
                                    while (a < document.attributes.size()) {
                                        attribute = (DocumentAttribute) document.attributes.get(a);
                                        if (attribute instanceof TL_documentAttributeImageSize) {
                                        }
                                        this.currentPhotoObject.f34w = attribute.f29w;
                                        this.currentPhotoObject.f33h = attribute.f28h;
                                        break;
                                        photoSize2 = this.currentPhotoObject;
                                        photoSize3 = this.currentPhotoObject;
                                        dp = AndroidUtilities.dp(150.0f);
                                        photoSize3.f33h = dp;
                                        photoSize2.f34w = dp;
                                        this.documentAttachType = DOCUMENT_ATTACH_TYPE_GIF;
                                    }
                                    photoSize2 = this.currentPhotoObject;
                                    photoSize3 = this.currentPhotoObject;
                                    dp = AndroidUtilities.dp(150.0f);
                                    photoSize3.f33h = dp;
                                    photoSize2.f34w = dp;
                                    this.documentAttachType = DOCUMENT_ATTACH_TYPE_GIF;
                                } else if (!MessageObject.isVideoDocument(document)) {
                                    this.currentPhotoObject = document.thumb;
                                    while (a < document.attributes.size()) {
                                        attribute = (DocumentAttribute) document.attributes.get(a);
                                        if (attribute instanceof TL_documentAttributeVideo) {
                                            this.currentPhotoObject.f34w = attribute.f29w;
                                            this.currentPhotoObject.f33h = attribute.f28h;
                                            break;
                                            photoSize2 = this.currentPhotoObject;
                                            photoSize3 = this.currentPhotoObject;
                                            dp = AndroidUtilities.dp(150.0f);
                                            photoSize3.f33h = dp;
                                            photoSize2.f34w = dp;
                                            createDocumentLayout(DOCUMENT_ATTACH_TYPE_NONE, messageObject);
                                        } else {
                                        }
                                    }
                                    photoSize2 = this.currentPhotoObject;
                                    photoSize3 = this.currentPhotoObject;
                                    dp = AndroidUtilities.dp(150.0f);
                                    photoSize3.f33h = dp;
                                    photoSize2.f34w = dp;
                                    createDocumentLayout(DOCUMENT_ATTACH_TYPE_NONE, messageObject);
                                } else if (MessageObject.isStickerDocument(document)) {
                                    this.currentPhotoObject = document.thumb;
                                    while (a < document.attributes.size()) {
                                        attribute = (DocumentAttribute) document.attributes.get(a);
                                        if (attribute instanceof TL_documentAttributeImageSize) {
                                            this.currentPhotoObject.f34w = attribute.f29w;
                                            this.currentPhotoObject.f33h = attribute.f28h;
                                            break;
                                            photoSize2 = this.currentPhotoObject;
                                            photoSize3 = this.currentPhotoObject;
                                            dp = AndroidUtilities.dp(150.0f);
                                            photoSize3.f33h = dp;
                                            photoSize2.f34w = dp;
                                            this.documentAttach = document;
                                            this.documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
                                        } else {
                                        }
                                    }
                                    photoSize2 = this.currentPhotoObject;
                                    photoSize3 = this.currentPhotoObject;
                                    dp = AndroidUtilities.dp(150.0f);
                                    photoSize3.f33h = dp;
                                    photoSize2.f34w = dp;
                                    this.documentAttach = document;
                                    this.documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
                                } else {
                                    calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                    if (MessageObject.isStickerDocument(document)) {
                                        if (this.backgroundWidth < AndroidUtilities.dp(20.0f) + maxWidth2) {
                                            this.backgroundWidth = AndroidUtilities.dp(20.0f) + maxWidth2;
                                        }
                                        if (!MessageObject.isVoiceDocument(document)) {
                                            createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(10.0f), messageObject);
                                            this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                            this.totalHeight += AndroidUtilities.dp(44.0f);
                                            this.linkPreviewHeight += AndroidUtilities.dp(44.0f);
                                            calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                        } else if (MessageObject.isMusicDocument(document)) {
                                            durationWidth = createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(10.0f), messageObject);
                                            this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                            this.totalHeight += AndroidUtilities.dp(56.0f);
                                            this.linkPreviewHeight += AndroidUtilities.dp(56.0f);
                                            maxWidth2 -= AndroidUtilities.dp(86.0f);
                                            maxChildWidth = Math.max(maxChildWidth, (durationWidth + additinalWidth) + AndroidUtilities.dp(94.0f));
                                            maxChildWidth = (int) Math.max((float) maxChildWidth, (this.songLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE) + ((float) additinalWidth)) + ((float) AndroidUtilities.dp(86.0f)));
                                            maxChildWidth = (int) Math.max((float) maxChildWidth, (this.performerLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE) + ((float) additinalWidth)) + ((float) AndroidUtilities.dp(86.0f)));
                                            calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                        } else {
                                            createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(168.0f), messageObject);
                                            this.drawImageButton = true;
                                            if (this.drawPhotoImage) {
                                                this.totalHeight += AndroidUtilities.dp(100.0f);
                                                this.linkPreviewHeight += AndroidUtilities.dp(86.0f);
                                                this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, this.totalHeight + this.namesOffset, AndroidUtilities.dp(86.0f), AndroidUtilities.dp(86.0f));
                                            } else {
                                                this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                                this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, (this.totalHeight + this.namesOffset) - AndroidUtilities.dp(14.0f), AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                                                this.totalHeight += AndroidUtilities.dp(64.0f);
                                                this.linkPreviewHeight += AndroidUtilities.dp(50.0f);
                                            }
                                        }
                                    }
                                }
                                maxWidth = maxWidth2;
                            } else {
                                if (webPage.photo != null) {
                                    if (webPage.type == null) {
                                    }
                                    this.drawImageButton = z;
                                    arrayList = messageObject.photoThumbs;
                                    if (this.drawImageButton) {
                                        photoSize = AndroidUtilities.getPhotoSize();
                                    } else {
                                        photoSize = maxPhotoWidth;
                                    }
                                    if (this.drawImageButton) {
                                    }
                                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, photoSize, this.drawImageButton);
                                    this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                                    if (this.currentPhotoObjectThumb == this.currentPhotoObject) {
                                        this.currentPhotoObjectThumb = null;
                                    }
                                }
                                maxWidth = maxWidth2;
                            }
                            if (this.currentPhotoObject == null) {
                                if (webPage.type == null) {
                                }
                                this.drawImageButton = z;
                                if (this.linkPreviewHeight != 0) {
                                    this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                                    this.totalHeight += AndroidUtilities.dp(2.0f);
                                }
                                if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER) {
                                    if (AndroidUtilities.isTablet()) {
                                        maxPhotoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                                    } else {
                                        maxPhotoWidth = (int) (((float) AndroidUtilities.displaySize.x) * 0.5f);
                                    }
                                }
                                maxChildWidth = Math.max(maxChildWidth, maxPhotoWidth + additinalWidth);
                                this.currentPhotoObject.size = -1;
                                if (this.currentPhotoObjectThumb != null) {
                                    this.currentPhotoObjectThumb.size = -1;
                                }
                                if (smallImage) {
                                    height = maxPhotoWidth;
                                    width = maxPhotoWidth;
                                } else {
                                    width = this.currentPhotoObject.f34w;
                                    scale = ((float) width) / ((float) (maxPhotoWidth - AndroidUtilities.dp(2.0f)));
                                    width = (int) (((float) width) / scale);
                                    height = (int) (((float) this.currentPhotoObject.f33h) / scale);
                                    height = AndroidUtilities.displaySize.y / DOCUMENT_ATTACH_TYPE_AUDIO;
                                }
                                if (this.isSmallImage) {
                                    if (AndroidUtilities.dp(50.0f) + additionalHeight > this.linkPreviewHeight) {
                                        this.totalHeight += ((AndroidUtilities.dp(50.0f) + additionalHeight) - this.linkPreviewHeight) + AndroidUtilities.dp(8.0f);
                                        this.linkPreviewHeight = AndroidUtilities.dp(50.0f) + additionalHeight;
                                    }
                                    this.linkPreviewHeight -= AndroidUtilities.dp(8.0f);
                                } else {
                                    this.totalHeight += AndroidUtilities.dp(12.0f) + height;
                                    this.linkPreviewHeight += height;
                                }
                                this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, DOCUMENT_ATTACH_TYPE_NONE, width, height);
                                objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(width);
                                objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(height);
                                this.currentPhotoFilter = String.format(Locale.US, "%d_%d", objArr2);
                                objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(width);
                                objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(height);
                                this.currentPhotoFilterThumb = String.format(Locale.US, "%d_%d_b", objArr2);
                                if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_STICKER) {
                                    imageReceiver = this.photoImage;
                                    tLObject = this.documentAttach;
                                    str = this.currentPhotoFilter;
                                    if (this.currentPhotoObject == null) {
                                        fileLocation = this.currentPhotoObject.location;
                                    } else {
                                        fileLocation = null;
                                    }
                                    imageReceiver.setImage(tLObject, null, str, null, fileLocation, "b1", this.documentAttach.size, "webp", true);
                                } else if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_VIDEO) {
                                    this.photoImage.setImage(null, null, this.currentPhotoObject.location, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                                } else if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF) {
                                    photoExist = messageObject.mediaExists;
                                    fileName = FileLoader.getAttachFileName(webPage.document);
                                    if (photoExist) {
                                    }
                                    this.photoNotSet = false;
                                    this.photoImage.setImage(webPage.document, null, this.currentPhotoObject.location, this.currentPhotoFilter, webPage.document.size, null, false);
                                } else {
                                    photoExist = messageObject.mediaExists;
                                    fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                                    if (photoExist) {
                                    }
                                    this.photoNotSet = false;
                                    if (this.currentPhotoObjectThumb == null) {
                                    }
                                    this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb == null ? null : this.currentPhotoObjectThumb.location, this.currentPhotoFilterThumb, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                                }
                                this.drawPhotoImage = true;
                                minutes = webPage.duration / 60;
                                seconds = webPage.duration - (minutes * 60);
                                objArr = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                objArr[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(minutes);
                                objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(seconds);
                                str2 = String.format("%d:%02d", objArr);
                                this.durationWidth = (int) Math.ceil((double) durationPaint.measureText(str2));
                                this.videoInfoLayout = new StaticLayout(str2, durationPaint, this.durationWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                            } else {
                                this.photoImage.setImageBitmap((Drawable) null);
                                this.linkPreviewHeight -= AndroidUtilities.dp(6.0f);
                                this.totalHeight += AndroidUtilities.dp(4.0f);
                            }
                            calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                            maxWidth2 = maxWidth;
                            try {
                                width = this.backgroundWidth - AndroidUtilities.dp(31.0f);
                                this.captionLayout = new StaticLayout(messageObject.caption, MessageObject.getTextPaint(), width - AndroidUtilities.dp(10.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                                if (this.captionLayout.getLineCount() > 0) {
                                    if (messageObject.isOutOwner()) {
                                    }
                                    timeWidthTotal = this.timeWidth + (messageObject.isOutOwner() ? DOCUMENT_ATTACH_TYPE_NONE : AndroidUtilities.dp(20.0f));
                                    this.captionHeight = this.captionLayout.getHeight();
                                    this.totalHeight += this.captionHeight + AndroidUtilities.dp(9.0f);
                                    if (((float) (width - AndroidUtilities.dp(8.0f))) - (this.captionLayout.getLineWidth(this.captionLayout.getLineCount() - 1) + this.captionLayout.getLineLeft(this.captionLayout.getLineCount() - 1)) < ((float) timeWidthTotal)) {
                                        this.totalHeight += AndroidUtilities.dp(14.0f);
                                        this.captionHeight += AndroidUtilities.dp(14.0f);
                                    }
                                }
                            } catch (Throwable e222) {
                                FileLog.m13e("tmessages", e222);
                            }
                            this.botButtons.clear();
                            if (messageIdChanged) {
                                this.botButtonsByData.clear();
                            }
                            if (messageObject.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                                rows = messageObject.messageOwner.reply_markup.rows.size();
                                photoSize = (AndroidUtilities.dp(48.0f) * rows) + AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                                this.keyboardHeight = photoSize;
                                this.substractBackgroundHeight = photoSize;
                                this.widthForButtons = this.backgroundWidth;
                                fullWidth = false;
                                if (messageObject.wantedBotKeyboardWidth > this.widthForButtons) {
                                    if (this.isChat) {
                                    }
                                    maxButtonWidth = -AndroidUtilities.dp(f);
                                    if (AndroidUtilities.isTablet()) {
                                        maxButtonWidth += AndroidUtilities.getMinTabletSide();
                                    } else {
                                        maxButtonWidth += Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                                    }
                                    this.widthForButtons = Math.max(this.backgroundWidth, Math.min(messageObject.wantedBotKeyboardWidth, maxButtonWidth));
                                    fullWidth = true;
                                }
                                maxButtonsWidth = DOCUMENT_ATTACH_TYPE_NONE;
                                for (a = DOCUMENT_ATTACH_TYPE_NONE; a < rows; a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                    row = (TL_keyboardButtonRow) messageObject.messageOwner.reply_markup.rows.get(a);
                                    buttonsCount = row.buttons.size();
                                    dp2 = this.widthForButtons - (AndroidUtilities.dp(5.0f) * (buttonsCount - 1));
                                    if (fullWidth) {
                                    }
                                    buttonWidth = ((dp2 - AndroidUtilities.dp(f)) - AndroidUtilities.dp(2.0f)) / buttonsCount;
                                    for (b = DOCUMENT_ATTACH_TYPE_NONE; b < row.buttons.size(); b += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                        chatMessageCell = this;
                                        botButton = new BotButton();
                                        botButton.button = (KeyboardButton) row.buttons.get(b);
                                        key = Utilities.bytesToHex(botButton.button.data);
                                        oldButton = (BotButton) this.botButtonsByData.get(key);
                                        if (oldButton == null) {
                                            botButton.progressAlpha = oldButton.progressAlpha;
                                            botButton.angle = oldButton.angle;
                                            botButton.lastUpdateTime = oldButton.lastUpdateTime;
                                        } else {
                                            botButton.lastUpdateTime = System.currentTimeMillis();
                                        }
                                        this.botButtonsByData.put(key, botButton);
                                        botButton.f11x = (AndroidUtilities.dp(5.0f) + buttonWidth) * b;
                                        botButton.f12y = (AndroidUtilities.dp(48.0f) * a) + AndroidUtilities.dp(5.0f);
                                        botButton.width = buttonWidth;
                                        botButton.height = AndroidUtilities.dp(44.0f);
                                        botButton.title = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(botButton.button.text, botButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), botButtonPaint, (float) (buttonWidth - AndroidUtilities.dp(10.0f)), TruncateAt.END), botButtonPaint, buttonWidth - AndroidUtilities.dp(10.0f), Alignment.ALIGN_CENTER, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                                        this.botButtons.add(botButton);
                                        if (b != row.buttons.size() - 1) {
                                            maxButtonsWidth = Math.max(maxButtonsWidth, botButton.f11x + botButton.width);
                                        }
                                    }
                                }
                                this.widthForButtons = maxButtonsWidth;
                            } else {
                                this.substractBackgroundHeight = DOCUMENT_ATTACH_TYPE_NONE;
                                this.keyboardHeight = DOCUMENT_ATTACH_TYPE_NONE;
                            }
                            updateWaveform();
                            updateButtonState(dataChanged);
                        }
                    }
                    authorIsRTL = false;
                    if (webPage.author == null) {
                        if (this.linkPreviewHeight != 0) {
                            this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                            this.totalHeight += AndroidUtilities.dp(2.0f);
                        }
                        if (restLinesCount2 == DOCUMENT_ATTACH_TYPE_AUDIO || (this.isSmallImage && webPage.description != null)) {
                            this.authorLayout = generateStaticLayout(webPage.author, replyNamePaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.dp(52.0f), restLinesCount2, DOCUMENT_ATTACH_TYPE_DOCUMENT);
                            restLinesCount = restLinesCount2 - this.authorLayout.getLineCount();
                        } else {
                            this.authorLayout = new StaticLayout(webPage.author, replyNamePaint, linkPreviewMaxWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                            restLinesCount = restLinesCount2;
                        }
                        height = this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
                        this.linkPreviewHeight += height;
                        this.totalHeight += height;
                        lineLeft = (int) this.authorLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                        this.authorX = -lineLeft;
                        if (lineLeft == 0) {
                            width = this.authorLayout.getWidth() - lineLeft;
                            authorIsRTL = true;
                        } else {
                            width = (int) Math.ceil((double) this.authorLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE));
                        }
                        maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                        maxWebWidth = Math.max(maxWebWidth, width + additinalWidth);
                    } else {
                        restLinesCount = restLinesCount2;
                    }
                    if (webPage.description != null) {
                        this.descriptionX = DOCUMENT_ATTACH_TYPE_NONE;
                        this.currentMessageObject.generateLinkDescription();
                        if (this.linkPreviewHeight != 0) {
                            this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                            this.totalHeight += AndroidUtilities.dp(2.0f);
                        }
                        restLines = DOCUMENT_ATTACH_TYPE_NONE;
                        if (restLinesCount == DOCUMENT_ATTACH_TYPE_AUDIO || this.isSmallImage) {
                            restLines = restLinesCount;
                            this.descriptionLayout = generateStaticLayout(messageObject.linkDescription, replyTextPaint, linkPreviewMaxWidth, linkPreviewMaxWidth - AndroidUtilities.dp(52.0f), restLinesCount, DOCUMENT_ATTACH_TYPE_STICKER);
                        } else {
                            this.descriptionLayout = StaticLayoutEx.createStaticLayout(messageObject.linkDescription, replyTextPaint, linkPreviewMaxWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, (float) AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL), false, TruncateAt.END, linkPreviewMaxWidth, DOCUMENT_ATTACH_TYPE_STICKER);
                        }
                        height = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                        this.linkPreviewHeight += height;
                        this.totalHeight += height;
                        hasRTL = false;
                        for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.descriptionLayout.getLineCount(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                            if (lineLeft == 0) {
                                hasRTL = true;
                                if (this.descriptionX != 0) {
                                    this.descriptionX = -lineLeft;
                                } else {
                                    this.descriptionX = Math.max(this.descriptionX, -lineLeft);
                                }
                            }
                        }
                        for (a = DOCUMENT_ATTACH_TYPE_NONE; a < this.descriptionLayout.getLineCount(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            lineLeft = (int) Math.ceil((double) this.descriptionLayout.getLineLeft(a));
                            if (lineLeft == 0 && this.descriptionX != 0) {
                                this.descriptionX = DOCUMENT_ATTACH_TYPE_NONE;
                            }
                            if (lineLeft == 0) {
                                width = this.descriptionLayout.getWidth() - lineLeft;
                            } else if (hasRTL) {
                                width = this.descriptionLayout.getWidth();
                            } else {
                                width = (int) Math.ceil((double) this.descriptionLayout.getLineWidth(a));
                            }
                            if (a < restLines || !(restLines == 0 || lineLeft == 0 || !this.isSmallImage)) {
                                width += AndroidUtilities.dp(52.0f);
                            }
                            if (maxWebWidth >= width + additinalWidth) {
                                if (titleIsRTL) {
                                    this.titleX += (width + additinalWidth) - maxWebWidth;
                                }
                                if (authorIsRTL) {
                                    this.authorX += (width + additinalWidth) - maxWebWidth;
                                }
                                maxWebWidth = width + additinalWidth;
                            }
                            maxChildWidth = Math.max(maxChildWidth, width + additinalWidth);
                        }
                    }
                    smallImage = webPage.type == null && (webPage.type.equals(C2DMessaging.EXTRA_APPLICATION_PENDING_INTENT) || webPage.type.equals(Scopes.PROFILE) || webPage.type.equals("article"));
                    if (smallImage && (this.descriptionLayout == null || (this.descriptionLayout != null && this.descriptionLayout.getLineCount() == DOCUMENT_ATTACH_TYPE_DOCUMENT))) {
                        smallImage = false;
                        this.isSmallImage = false;
                    }
                    if (smallImage) {
                        maxPhotoWidth = AndroidUtilities.dp(48.0f);
                    } else {
                        maxPhotoWidth = linkPreviewMaxWidth;
                    }
                    if (webPage.document == null) {
                        document = webPage.document;
                        if (!MessageObject.isGifDocument(document)) {
                            if (MediaController.getInstance().canAutoplayGifs()) {
                                messageObject.audioProgress = TouchHelperCallback.ALPHA_FULL;
                            }
                            if (messageObject.audioProgress != TouchHelperCallback.ALPHA_FULL) {
                            }
                            this.photoImage.setAllowStartAnimation(messageObject.audioProgress != TouchHelperCallback.ALPHA_FULL);
                            this.currentPhotoObject = document.thumb;
                            if (this.currentPhotoObject != null && (this.currentPhotoObject.f34w == 0 || this.currentPhotoObject.f33h == 0)) {
                                for (a = DOCUMENT_ATTACH_TYPE_NONE; a < document.attributes.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                    attribute = (DocumentAttribute) document.attributes.get(a);
                                    if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                        this.currentPhotoObject.f34w = attribute.f29w;
                                        this.currentPhotoObject.f33h = attribute.f28h;
                                        break;
                                    }
                                }
                                if (this.currentPhotoObject.f34w == 0 || this.currentPhotoObject.f33h == 0) {
                                    photoSize2 = this.currentPhotoObject;
                                    photoSize3 = this.currentPhotoObject;
                                    dp = AndroidUtilities.dp(150.0f);
                                    photoSize3.f33h = dp;
                                    photoSize2.f34w = dp;
                                }
                            }
                            this.documentAttachType = DOCUMENT_ATTACH_TYPE_GIF;
                        } else if (!MessageObject.isVideoDocument(document)) {
                            this.currentPhotoObject = document.thumb;
                            if (this.currentPhotoObject != null && (this.currentPhotoObject.f34w == 0 || this.currentPhotoObject.f33h == 0)) {
                                for (a = DOCUMENT_ATTACH_TYPE_NONE; a < document.attributes.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                    attribute = (DocumentAttribute) document.attributes.get(a);
                                    if (attribute instanceof TL_documentAttributeVideo) {
                                        this.currentPhotoObject.f34w = attribute.f29w;
                                        this.currentPhotoObject.f33h = attribute.f28h;
                                        break;
                                    }
                                }
                                if (this.currentPhotoObject.f34w == 0 || this.currentPhotoObject.f33h == 0) {
                                    photoSize2 = this.currentPhotoObject;
                                    photoSize3 = this.currentPhotoObject;
                                    dp = AndroidUtilities.dp(150.0f);
                                    photoSize3.f33h = dp;
                                    photoSize2.f34w = dp;
                                }
                            }
                            createDocumentLayout(DOCUMENT_ATTACH_TYPE_NONE, messageObject);
                        } else if (MessageObject.isStickerDocument(document)) {
                            this.currentPhotoObject = document.thumb;
                            if (this.currentPhotoObject != null && (this.currentPhotoObject.f34w == 0 || this.currentPhotoObject.f33h == 0)) {
                                for (a = DOCUMENT_ATTACH_TYPE_NONE; a < document.attributes.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                                    attribute = (DocumentAttribute) document.attributes.get(a);
                                    if (attribute instanceof TL_documentAttributeImageSize) {
                                        this.currentPhotoObject.f34w = attribute.f29w;
                                        this.currentPhotoObject.f33h = attribute.f28h;
                                        break;
                                    }
                                }
                                if (this.currentPhotoObject.f34w == 0 || this.currentPhotoObject.f33h == 0) {
                                    photoSize2 = this.currentPhotoObject;
                                    photoSize3 = this.currentPhotoObject;
                                    dp = AndroidUtilities.dp(150.0f);
                                    photoSize3.f33h = dp;
                                    photoSize2.f34w = dp;
                                }
                            }
                            this.documentAttach = document;
                            this.documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
                        } else {
                            calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                            if (MessageObject.isStickerDocument(document)) {
                                if (this.backgroundWidth < AndroidUtilities.dp(20.0f) + maxWidth2) {
                                    this.backgroundWidth = AndroidUtilities.dp(20.0f) + maxWidth2;
                                }
                                if (!MessageObject.isVoiceDocument(document)) {
                                    createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(10.0f), messageObject);
                                    this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                    this.totalHeight += AndroidUtilities.dp(44.0f);
                                    this.linkPreviewHeight += AndroidUtilities.dp(44.0f);
                                    calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                } else if (MessageObject.isMusicDocument(document)) {
                                    durationWidth = createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(10.0f), messageObject);
                                    this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                    this.totalHeight += AndroidUtilities.dp(56.0f);
                                    this.linkPreviewHeight += AndroidUtilities.dp(56.0f);
                                    maxWidth2 -= AndroidUtilities.dp(86.0f);
                                    maxChildWidth = Math.max(maxChildWidth, (durationWidth + additinalWidth) + AndroidUtilities.dp(94.0f));
                                    if (this.songLayout != null && this.songLayout.getLineCount() > 0) {
                                        maxChildWidth = (int) Math.max((float) maxChildWidth, (this.songLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE) + ((float) additinalWidth)) + ((float) AndroidUtilities.dp(86.0f)));
                                    }
                                    if (this.performerLayout != null && this.performerLayout.getLineCount() > 0) {
                                        maxChildWidth = (int) Math.max((float) maxChildWidth, (this.performerLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE) + ((float) additinalWidth)) + ((float) AndroidUtilities.dp(86.0f)));
                                    }
                                    calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                                } else {
                                    createDocumentLayout(this.backgroundWidth - AndroidUtilities.dp(168.0f), messageObject);
                                    this.drawImageButton = true;
                                    if (this.drawPhotoImage) {
                                        this.totalHeight += AndroidUtilities.dp(100.0f);
                                        this.linkPreviewHeight += AndroidUtilities.dp(86.0f);
                                        this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, this.totalHeight + this.namesOffset, AndroidUtilities.dp(86.0f), AndroidUtilities.dp(86.0f));
                                    } else {
                                        this.mediaOffsetY = (this.currentMessageObject.textHeight + AndroidUtilities.dp(8.0f)) + this.linkPreviewHeight;
                                        this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, (this.totalHeight + this.namesOffset) - AndroidUtilities.dp(14.0f), AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                                        this.totalHeight += AndroidUtilities.dp(64.0f);
                                        this.linkPreviewHeight += AndroidUtilities.dp(50.0f);
                                    }
                                }
                            }
                        }
                        maxWidth = maxWidth2;
                    } else {
                        if (webPage.photo != null) {
                            z = webPage.type == null && webPage.type.equals("photo");
                            this.drawImageButton = z;
                            arrayList = messageObject.photoThumbs;
                            if (this.drawImageButton) {
                                photoSize = AndroidUtilities.getPhotoSize();
                            } else {
                                photoSize = maxPhotoWidth;
                            }
                            if (this.drawImageButton) {
                            }
                            this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(arrayList, photoSize, this.drawImageButton);
                            this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                            if (this.currentPhotoObjectThumb == this.currentPhotoObject) {
                                this.currentPhotoObjectThumb = null;
                            }
                        }
                        maxWidth = maxWidth2;
                    }
                    if (!(this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC || this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT)) {
                        if (this.currentPhotoObject == null) {
                            z = webPage.type == null && (webPage.type.equals("photo") || ((webPage.type.equals("document") && this.documentAttachType != DOCUMENT_ATTACH_TYPE_STICKER) || webPage.type.equals("gif") || this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO));
                            this.drawImageButton = z;
                            if (this.linkPreviewHeight != 0) {
                                this.linkPreviewHeight += AndroidUtilities.dp(2.0f);
                                this.totalHeight += AndroidUtilities.dp(2.0f);
                            }
                            if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER) {
                                if (AndroidUtilities.isTablet()) {
                                    maxPhotoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                                } else {
                                    maxPhotoWidth = (int) (((float) AndroidUtilities.displaySize.x) * 0.5f);
                                }
                            }
                            maxChildWidth = Math.max(maxChildWidth, maxPhotoWidth + additinalWidth);
                            this.currentPhotoObject.size = -1;
                            if (this.currentPhotoObjectThumb != null) {
                                this.currentPhotoObjectThumb.size = -1;
                            }
                            if (smallImage) {
                                height = maxPhotoWidth;
                                width = maxPhotoWidth;
                            } else {
                                width = this.currentPhotoObject.f34w;
                                scale = ((float) width) / ((float) (maxPhotoWidth - AndroidUtilities.dp(2.0f)));
                                width = (int) (((float) width) / scale);
                                height = (int) (((float) this.currentPhotoObject.f33h) / scale);
                                if ((webPage.site_name == null || !(webPage.site_name == null || webPage.site_name.toLowerCase().equals("instagram") || this.documentAttachType != 0)) && height > AndroidUtilities.displaySize.y / DOCUMENT_ATTACH_TYPE_AUDIO) {
                                    height = AndroidUtilities.displaySize.y / DOCUMENT_ATTACH_TYPE_AUDIO;
                                }
                            }
                            if (this.isSmallImage) {
                                if (AndroidUtilities.dp(50.0f) + additionalHeight > this.linkPreviewHeight) {
                                    this.totalHeight += ((AndroidUtilities.dp(50.0f) + additionalHeight) - this.linkPreviewHeight) + AndroidUtilities.dp(8.0f);
                                    this.linkPreviewHeight = AndroidUtilities.dp(50.0f) + additionalHeight;
                                }
                                this.linkPreviewHeight -= AndroidUtilities.dp(8.0f);
                            } else {
                                this.totalHeight += AndroidUtilities.dp(12.0f) + height;
                                this.linkPreviewHeight += height;
                            }
                            this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, DOCUMENT_ATTACH_TYPE_NONE, width, height);
                            objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                            objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(width);
                            objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(height);
                            this.currentPhotoFilter = String.format(Locale.US, "%d_%d", objArr2);
                            objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                            objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(width);
                            objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(height);
                            this.currentPhotoFilterThumb = String.format(Locale.US, "%d_%d_b", objArr2);
                            if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_STICKER) {
                                imageReceiver = this.photoImage;
                                tLObject = this.documentAttach;
                                str = this.currentPhotoFilter;
                                if (this.currentPhotoObject == null) {
                                    fileLocation = this.currentPhotoObject.location;
                                } else {
                                    fileLocation = null;
                                }
                                imageReceiver.setImage(tLObject, null, str, null, fileLocation, "b1", this.documentAttach.size, "webp", true);
                            } else if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_VIDEO) {
                                this.photoImage.setImage(null, null, this.currentPhotoObject.location, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                            } else if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF) {
                                photoExist = messageObject.mediaExists;
                                fileName = FileLoader.getAttachFileName(webPage.document);
                                if (photoExist || MediaController.getInstance().canDownloadMedia(32) || FileLoader.getInstance().isLoadingFile(fileName)) {
                                    this.photoNotSet = false;
                                    this.photoImage.setImage(webPage.document, null, this.currentPhotoObject.location, this.currentPhotoFilter, webPage.document.size, null, false);
                                } else {
                                    this.photoNotSet = true;
                                    this.photoImage.setImage(null, null, this.currentPhotoObject.location, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                                }
                            } else {
                                photoExist = messageObject.mediaExists;
                                fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                                if (photoExist || MediaController.getInstance().canDownloadMedia(DOCUMENT_ATTACH_TYPE_DOCUMENT) || FileLoader.getInstance().isLoadingFile(fileName)) {
                                    this.photoNotSet = false;
                                    if (this.currentPhotoObjectThumb == null) {
                                    }
                                    this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb == null ? null : this.currentPhotoObjectThumb.location, this.currentPhotoFilterThumb, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                                } else {
                                    this.photoNotSet = true;
                                    if (this.currentPhotoObjectThumb != null) {
                                        imageReceiver = this.photoImage;
                                        FileLocation fileLocation2 = this.currentPhotoObjectThumb.location;
                                        objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                        objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(width);
                                        objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(height);
                                        imageReceiver.setImage(null, null, fileLocation2, String.format(Locale.US, "%d_%d_b", objArr2), DOCUMENT_ATTACH_TYPE_NONE, null, false);
                                    } else {
                                        this.photoImage.setImageBitmap((Drawable) null);
                                    }
                                }
                            }
                            this.drawPhotoImage = true;
                            if (!(webPage.type == null || !webPage.type.equals(MimeTypes.BASE_TYPE_VIDEO) || webPage.duration == 0)) {
                                minutes = webPage.duration / 60;
                                seconds = webPage.duration - (minutes * 60);
                                objArr = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                                objArr[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(minutes);
                                objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(seconds);
                                str2 = String.format("%d:%02d", objArr);
                                this.durationWidth = (int) Math.ceil((double) durationPaint.measureText(str2));
                                this.videoInfoLayout = new StaticLayout(str2, durationPaint, this.durationWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                            }
                        } else {
                            this.photoImage.setImageBitmap((Drawable) null);
                            this.linkPreviewHeight -= AndroidUtilities.dp(6.0f);
                            this.totalHeight += AndroidUtilities.dp(4.0f);
                        }
                        calcBackgroundWidth(maxWidth, timeMore, maxChildWidth);
                    }
                    maxWidth2 = maxWidth;
                } else {
                    this.photoImage.setImageBitmap((Drawable) null);
                    calcBackgroundWidth(maxWidth2, timeMore, maxChildWidth);
                }
            } else if (messageObject.type == 12) {
                Drawable drawable;
                this.drawName = false;
                this.drawForwardedName = true;
                this.drawPhotoImage = true;
                this.photoImage.setRoundRadius(AndroidUtilities.dp(22.0f));
                if (AndroidUtilities.isTablet()) {
                    dp2 = AndroidUtilities.getMinTabletSide();
                    f = (this.isChat && messageObject.isFromUser() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(dp2 - AndroidUtilities.dp(f), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET));
                } else {
                    dp2 = AndroidUtilities.displaySize.x;
                    f = (this.isChat && messageObject.isFromUser() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(dp2 - AndroidUtilities.dp(f), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET));
                }
                this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.dp(31.0f);
                User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.media.user_id));
                maxWidth2 = getMaxNameWidth() - AndroidUtilities.dp(110.0f);
                if (maxWidth2 < 0) {
                    maxWidth = AndroidUtilities.dp(10.0f);
                } else {
                    maxWidth = maxWidth2;
                }
                TLObject currentPhoto = null;
                if (user != null) {
                    if (user.photo != null) {
                        currentPhoto = user.photo.photo_small;
                    }
                    this.contactAvatarDrawable.setInfo(user);
                }
                ImageReceiver imageReceiver2 = this.photoImage;
                String str3 = "50_50";
                if (user != null) {
                    drawable = this.contactAvatarDrawable;
                } else {
                    drawable = Theme.contactDrawable[messageObject.isOutOwner() ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE];
                }
                imageReceiver2.setImage(currentPhoto, str3, drawable, null, false);
                String phone = messageObject.messageOwner.media.phone_number;
                if (phone == null || phone.length() == 0) {
                    phone = LocaleController.getString("NumberUnknown", C0691R.string.NumberUnknown);
                } else {
                    phone = PhoneFormat.getInstance().format(phone);
                }
                CharSequence currentNameString = ContactsController.formatName(messageObject.messageOwner.media.first_name, messageObject.messageOwner.media.last_name).replace('\n', ' ');
                if (currentNameString.length() == 0) {
                    currentNameString = phone;
                }
                this.titleLayout = new StaticLayout(TextUtils.ellipsize(currentNameString, contactNamePaint, (float) maxWidth, TruncateAt.END), contactNamePaint, maxWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(phone.replace('\n', ' '), contactPhonePaint, (float) maxWidth, TruncateAt.END), contactPhonePaint, maxWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                setMessageObjectInternal(messageObject);
                if (this.drawForwardedName && messageObject.isForwarded()) {
                    this.namesOffset += AndroidUtilities.dp(5.0f);
                } else if (this.drawNameLayout && messageObject.messageOwner.reply_to_msg_id == 0) {
                    this.namesOffset += AndroidUtilities.dp(7.0f);
                }
                this.totalHeight = AndroidUtilities.dp(70.0f) + this.namesOffset;
                if (this.docTitleLayout.getLineCount() > 0 && (this.backgroundWidth - AndroidUtilities.dp(110.0f)) - ((int) Math.ceil((double) this.docTitleLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE))) < this.timeWidth) {
                    this.totalHeight += AndroidUtilities.dp(8.0f);
                }
            } else if (messageObject.type == DOCUMENT_ATTACH_TYPE_GIF) {
                this.drawForwardedName = true;
                if (AndroidUtilities.isTablet()) {
                    dp2 = AndroidUtilities.getMinTabletSide();
                    f = (this.isChat && messageObject.isFromUser() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(dp2 - AndroidUtilities.dp(f), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET));
                } else {
                    dp2 = AndroidUtilities.displaySize.x;
                    f = (this.isChat && messageObject.isFromUser() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(dp2 - AndroidUtilities.dp(f), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET));
                }
                createDocumentLayout(this.backgroundWidth, messageObject);
                setMessageObjectInternal(messageObject);
                this.totalHeight = AndroidUtilities.dp(70.0f) + this.namesOffset;
            } else if (messageObject.type == 14) {
                if (AndroidUtilities.isTablet()) {
                    dp2 = AndroidUtilities.getMinTabletSide();
                    f = (this.isChat && messageObject.isFromUser() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(dp2 - AndroidUtilities.dp(f), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET));
                } else {
                    dp2 = AndroidUtilities.displaySize.x;
                    f = (this.isChat && messageObject.isFromUser() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                    this.backgroundWidth = Math.min(dp2 - AndroidUtilities.dp(f), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET));
                }
                createDocumentLayout(this.backgroundWidth, messageObject);
                setMessageObjectInternal(messageObject);
                this.totalHeight = AndroidUtilities.dp(82.0f) + this.namesOffset;
            } else {
                z = (messageObject.messageOwner.fwd_from == null || messageObject.type == 13) ? false : true;
                this.drawForwardedName = z;
                this.mediaBackground = messageObject.type != 9;
                this.drawImageButton = true;
                this.drawPhotoImage = true;
                int photoWidth = DOCUMENT_ATTACH_TYPE_NONE;
                int i = DOCUMENT_ATTACH_TYPE_NONE;
                int additionHeight = DOCUMENT_ATTACH_TYPE_NONE;
                if (!(messageObject.audioProgress == 2.0f || MediaController.getInstance().canAutoplayGifs() || messageObject.type != 8)) {
                    messageObject.audioProgress = TouchHelperCallback.ALPHA_FULL;
                }
                this.photoImage.setAllowStartAnimation(messageObject.audioProgress == 0.0f);
                this.photoImage.setForcePreview(messageObject.isSecretPhoto());
                if (messageObject.type == 9) {
                    if (AndroidUtilities.isTablet()) {
                        dp2 = AndroidUtilities.getMinTabletSide();
                        f = (this.isChat && messageObject.isFromUser() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                        this.backgroundWidth = Math.min(dp2 - AndroidUtilities.dp(f), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET));
                    } else {
                        dp2 = AndroidUtilities.displaySize.x;
                        f = (this.isChat && messageObject.isFromUser() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                        this.backgroundWidth = Math.min(dp2 - AndroidUtilities.dp(f), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET));
                    }
                    if (checkNeedDrawShareButton(messageObject)) {
                        this.backgroundWidth -= AndroidUtilities.dp(20.0f);
                    }
                    maxWidth2 = this.backgroundWidth - AndroidUtilities.dp(138.0f);
                    createDocumentLayout(maxWidth2, messageObject);
                    if (!TextUtils.isEmpty(messageObject.caption)) {
                        maxWidth2 += AndroidUtilities.dp(86.0f);
                    }
                    if (this.drawPhotoImage) {
                        photoWidth = AndroidUtilities.dp(86.0f);
                        i = AndroidUtilities.dp(86.0f);
                    } else {
                        photoWidth = AndroidUtilities.dp(56.0f);
                        i = AndroidUtilities.dp(56.0f);
                        maxWidth2 += AndroidUtilities.dp(TextUtils.isEmpty(messageObject.caption) ? 51.0f : 21.0f);
                    }
                    this.availableTimeWidth = maxWidth2;
                    if (!this.drawPhotoImage && TextUtils.isEmpty(messageObject.caption) && this.infoLayout.getLineCount() > 0) {
                        measureTime(messageObject);
                        if ((this.backgroundWidth - AndroidUtilities.dp(122.0f)) - ((int) Math.ceil((double) this.infoLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE))) < this.timeWidth) {
                            i += AndroidUtilities.dp(8.0f);
                        }
                    }
                } else if (messageObject.type == DOCUMENT_ATTACH_TYPE_VIDEO) {
                    Drawable drawable2;
                    double lat = messageObject.messageOwner.media.geo.lat;
                    double lon = messageObject.messageOwner.media.geo._long;
                    if (messageObject.messageOwner.media.title == null || messageObject.messageOwner.media.title.length() <= 0) {
                        this.availableTimeWidth = AndroidUtilities.dp(186.0f);
                        photoWidth = AndroidUtilities.dp(200.0f);
                        i = AndroidUtilities.dp(100.0f);
                        this.backgroundWidth = AndroidUtilities.dp(12.0f) + photoWidth;
                        objArr2 = new Object[DOCUMENT_ATTACH_TYPE_MUSIC];
                        objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Double.valueOf(lat);
                        objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Double.valueOf(lon);
                        objArr2[DOCUMENT_ATTACH_TYPE_GIF] = Integer.valueOf(Math.min(DOCUMENT_ATTACH_TYPE_GIF, (int) Math.ceil((double) AndroidUtilities.density)));
                        objArr2[DOCUMENT_ATTACH_TYPE_AUDIO] = Double.valueOf(lat);
                        objArr2[DOCUMENT_ATTACH_TYPE_VIDEO] = Double.valueOf(lon);
                        this.currentUrl = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=200x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", objArr2);
                    } else {
                        if (AndroidUtilities.isTablet()) {
                            dp2 = AndroidUtilities.getMinTabletSide();
                            f = (this.isChat && messageObject.isFromUser() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                            this.backgroundWidth = Math.min(dp2 - AndroidUtilities.dp(f), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET));
                        } else {
                            dp2 = AndroidUtilities.displaySize.x;
                            f = (this.isChat && messageObject.isFromUser() && !messageObject.isOutOwner()) ? 102.0f : 50.0f;
                            this.backgroundWidth = Math.min(dp2 - AndroidUtilities.dp(f), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET));
                        }
                        if (checkNeedDrawShareButton(messageObject)) {
                            this.backgroundWidth -= AndroidUtilities.dp(20.0f);
                        }
                        maxWidth2 = this.backgroundWidth - AndroidUtilities.dp(123.0f);
                        this.docTitleLayout = StaticLayoutEx.createStaticLayout(messageObject.messageOwner.media.title, locationTitlePaint, maxWidth2, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false, TruncateAt.END, maxWidth2, DOCUMENT_ATTACH_TYPE_GIF);
                        int lineCount = this.docTitleLayout.getLineCount();
                        if (messageObject.messageOwner.media.address == null || messageObject.messageOwner.media.address.length() <= 0) {
                            this.infoLayout = null;
                        } else {
                            this.infoLayout = StaticLayoutEx.createStaticLayout(messageObject.messageOwner.media.address, locationAddressPaint, maxWidth2, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false, TruncateAt.END, maxWidth2, Math.min(DOCUMENT_ATTACH_TYPE_AUDIO, 3 - lineCount));
                        }
                        this.mediaBackground = false;
                        this.availableTimeWidth = maxWidth2;
                        photoWidth = AndroidUtilities.dp(86.0f);
                        i = AndroidUtilities.dp(86.0f);
                        objArr2 = new Object[DOCUMENT_ATTACH_TYPE_MUSIC];
                        objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Double.valueOf(lat);
                        objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Double.valueOf(lon);
                        objArr2[DOCUMENT_ATTACH_TYPE_GIF] = Integer.valueOf(Math.min(DOCUMENT_ATTACH_TYPE_GIF, (int) Math.ceil((double) AndroidUtilities.density)));
                        objArr2[DOCUMENT_ATTACH_TYPE_AUDIO] = Double.valueOf(lat);
                        objArr2[DOCUMENT_ATTACH_TYPE_VIDEO] = Double.valueOf(lon);
                        this.currentUrl = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", objArr2);
                    }
                    r16 = this.photoImage;
                    String str4 = this.currentUrl;
                    if (messageObject.isOutOwner()) {
                        drawable2 = Theme.geoOutDrawable;
                    } else {
                        drawable2 = Theme.geoInDrawable;
                    }
                    r16.setImage(str4, null, drawable2, null, (int) DOCUMENT_ATTACH_TYPE_NONE);
                } else if (messageObject.type == 13) {
                    float maxWidth3;
                    float maxHeight;
                    this.drawBackground = false;
                    for (a = DOCUMENT_ATTACH_TYPE_NONE; a < messageObject.messageOwner.media.document.attributes.size(); a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        attribute = (DocumentAttribute) messageObject.messageOwner.media.document.attributes.get(a);
                        if (attribute instanceof TL_documentAttributeImageSize) {
                            photoWidth = attribute.f29w;
                            i = attribute.f28h;
                            break;
                        }
                    }
                    if (AndroidUtilities.isTablet()) {
                        maxWidth3 = ((float) AndroidUtilities.getMinTabletSide()) * 0.4f;
                        maxHeight = maxWidth3;
                    } else {
                        maxWidth3 = ((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f;
                        maxHeight = maxWidth3;
                    }
                    if (photoWidth == 0) {
                        i = (int) maxHeight;
                        photoWidth = i + AndroidUtilities.dp(100.0f);
                    }
                    i = (int) (((float) i) * (maxWidth3 / ((float) photoWidth)));
                    photoWidth = (int) maxWidth3;
                    if (((float) i) > maxHeight) {
                        photoWidth = (int) (((float) photoWidth) * (maxHeight / ((float) i)));
                        i = (int) maxHeight;
                    }
                    this.documentAttachType = DOCUMENT_ATTACH_TYPE_STICKER;
                    this.availableTimeWidth = photoWidth - AndroidUtilities.dp(14.0f);
                    this.backgroundWidth = AndroidUtilities.dp(12.0f) + photoWidth;
                    this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                    if (messageObject.attachPathExists) {
                        r16 = this.photoImage;
                        r18 = messageObject.messageOwner.attachPath;
                        objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                        objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(photoWidth);
                        objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(i);
                        String format = String.format(Locale.US, "%d_%d", objArr2);
                        if (this.currentPhotoObjectThumb != null) {
                            r21 = this.currentPhotoObjectThumb.location;
                        } else {
                            r21 = null;
                        }
                        r16.setImage(null, r18, format, null, r21, "b1", messageObject.messageOwner.media.document.size, "webp", true);
                    } else if (messageObject.messageOwner.media.document.id != 0) {
                        r16 = this.photoImage;
                        r17 = messageObject.messageOwner.media.document;
                        objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                        objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf(photoWidth);
                        objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf(i);
                        r16.setImage(r17, null, String.format(Locale.US, "%d_%d", objArr2), null, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, "b1", messageObject.messageOwner.media.document.size, "webp", true);
                    }
                } else {
                    float scale2;
                    float hScale;
                    if (AndroidUtilities.isTablet()) {
                        photoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
                        maxPhotoWidth = photoWidth;
                    } else {
                        photoWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
                        maxPhotoWidth = photoWidth;
                    }
                    i = photoWidth + AndroidUtilities.dp(100.0f);
                    if (checkNeedDrawShareButton(messageObject)) {
                        maxPhotoWidth -= AndroidUtilities.dp(20.0f);
                        photoWidth -= AndroidUtilities.dp(20.0f);
                    }
                    if (photoWidth > AndroidUtilities.getPhotoSize()) {
                        photoWidth = AndroidUtilities.getPhotoSize();
                    }
                    if (i > AndroidUtilities.getPhotoSize()) {
                        i = AndroidUtilities.getPhotoSize();
                    }
                    if (messageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        updateSecretTimeText(messageObject);
                        this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 80);
                    } else if (messageObject.type == DOCUMENT_ATTACH_TYPE_AUDIO) {
                        createDocumentLayout(DOCUMENT_ATTACH_TYPE_NONE, messageObject);
                        this.photoImage.setNeedsQualityThumb(true);
                        this.photoImage.setShouldGenerateQualityThumb(true);
                        this.photoImage.setParentMessageObject(messageObject);
                    } else if (messageObject.type == 8) {
                        str2 = AndroidUtilities.formatFileSize((long) messageObject.messageOwner.media.document.size);
                        this.infoWidth = (int) Math.ceil((double) infoPaint.measureText(str2));
                        this.infoLayout = new StaticLayout(str2, infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                        this.photoImage.setNeedsQualityThumb(true);
                        this.photoImage.setShouldGenerateQualityThumb(true);
                        this.photoImage.setParentMessageObject(messageObject);
                    }
                    if (messageObject.caption != null) {
                        this.mediaBackground = false;
                    }
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                    int w = DOCUMENT_ATTACH_TYPE_NONE;
                    int h = DOCUMENT_ATTACH_TYPE_NONE;
                    if (this.currentPhotoObject != null && this.currentPhotoObject == this.currentPhotoObjectThumb) {
                        this.currentPhotoObjectThumb = null;
                    }
                    if (this.currentPhotoObject != null) {
                        scale = ((float) this.currentPhotoObject.f34w) / ((float) photoWidth);
                        w = (int) (((float) this.currentPhotoObject.f34w) / scale);
                        h = (int) (((float) this.currentPhotoObject.f33h) / scale);
                        if (w == 0) {
                            w = AndroidUtilities.dp(150.0f);
                        }
                        if (h == 0) {
                            h = AndroidUtilities.dp(150.0f);
                        }
                        if (h > i) {
                            scale2 = (float) h;
                            h = i;
                            w = (int) (((float) w) / (r0 / ((float) h)));
                        } else if (h < AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN)) {
                            h = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN);
                            hScale = ((float) this.currentPhotoObject.f33h) / ((float) h);
                            if (((float) this.currentPhotoObject.f34w) / hScale < ((float) photoWidth)) {
                                w = (int) (((float) this.currentPhotoObject.f34w) / hScale);
                            }
                        }
                    }
                    if ((w == 0 || h == 0) && messageObject.type == 8) {
                        a = DOCUMENT_ATTACH_TYPE_NONE;
                        while (a < messageObject.messageOwner.media.document.attributes.size()) {
                            attribute = (DocumentAttribute) messageObject.messageOwner.media.document.attributes.get(a);
                            if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                scale = ((float) attribute.f29w) / ((float) photoWidth);
                                w = (int) (((float) attribute.f29w) / scale);
                                h = (int) (((float) attribute.f28h) / scale);
                                if (h > i) {
                                    scale2 = (float) h;
                                    h = i;
                                    w = (int) (((float) w) / (r0 / ((float) h)));
                                } else if (h < AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN)) {
                                    h = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN);
                                    hScale = ((float) attribute.f28h) / ((float) h);
                                    if (((float) attribute.f29w) / hScale < ((float) photoWidth)) {
                                        w = (int) (((float) attribute.f29w) / hScale);
                                    }
                                }
                            } else {
                                a += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                            }
                        }
                    }
                    if (w == 0 || h == 0) {
                        h = AndroidUtilities.dp(150.0f);
                        w = h;
                    }
                    if (messageObject.type == DOCUMENT_ATTACH_TYPE_AUDIO && w < this.infoWidth + AndroidUtilities.dp(40.0f)) {
                        w = this.infoWidth + AndroidUtilities.dp(40.0f);
                    }
                    this.availableTimeWidth = maxPhotoWidth - AndroidUtilities.dp(14.0f);
                    measureTime(messageObject);
                    timeWidthTotal = this.timeWidth + AndroidUtilities.dp((float) ((messageObject.isOutOwner() ? 20 : DOCUMENT_ATTACH_TYPE_NONE) + 14));
                    if (w < timeWidthTotal) {
                        w = timeWidthTotal;
                    }
                    if (messageObject.isSecretPhoto()) {
                        if (AndroidUtilities.isTablet()) {
                            h = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                            w = h;
                        } else {
                            h = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f);
                            w = h;
                        }
                    }
                    photoWidth = w;
                    i = h;
                    this.backgroundWidth = AndroidUtilities.dp(12.0f) + w;
                    if (!this.mediaBackground) {
                        this.backgroundWidth += AndroidUtilities.dp(9.0f);
                    }
                    if (messageObject.caption != null) {
                        try {
                            this.captionLayout = new StaticLayout(messageObject.caption, MessageObject.getTextPaint(), photoWidth - AndroidUtilities.dp(10.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                            if (this.captionLayout.getLineCount() > 0) {
                                this.captionHeight = this.captionLayout.getHeight();
                                additionHeight = DOCUMENT_ATTACH_TYPE_NONE + (this.captionHeight + AndroidUtilities.dp(9.0f));
                                if (((float) (photoWidth - AndroidUtilities.dp(8.0f))) - (this.captionLayout.getLineWidth(this.captionLayout.getLineCount() - 1) + this.captionLayout.getLineLeft(this.captionLayout.getLineCount() - 1)) < ((float) timeWidthTotal)) {
                                    additionHeight += AndroidUtilities.dp(14.0f);
                                }
                            }
                        } catch (Throwable e2222) {
                            FileLog.m13e("tmessages", e2222);
                        }
                    }
                    objArr2 = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                    objArr2[DOCUMENT_ATTACH_TYPE_NONE] = Integer.valueOf((int) (((float) w) / AndroidUtilities.density));
                    objArr2[DOCUMENT_ATTACH_TYPE_DOCUMENT] = Integer.valueOf((int) (((float) h) / AndroidUtilities.density));
                    this.currentPhotoFilter = String.format(Locale.US, "%d_%d", objArr2);
                    if ((messageObject.photoThumbs != null && messageObject.photoThumbs.size() > DOCUMENT_ATTACH_TYPE_DOCUMENT) || messageObject.type == DOCUMENT_ATTACH_TYPE_AUDIO || messageObject.type == 8) {
                        if (messageObject.isSecretPhoto()) {
                            this.currentPhotoFilter += "_b2";
                        } else {
                            this.currentPhotoFilter += "_b";
                        }
                    }
                    boolean noSize = false;
                    if (messageObject.type == DOCUMENT_ATTACH_TYPE_AUDIO || messageObject.type == 8) {
                        noSize = true;
                    }
                    if (!(this.currentPhotoObject == null || noSize || this.currentPhotoObject.size != 0)) {
                        this.currentPhotoObject.size = -1;
                    }
                    if (messageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        if (this.currentPhotoObject != null) {
                            photoExist = true;
                            fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                            if (messageObject.mediaExists) {
                                MediaController.getInstance().removeLoadingFileObserver(this);
                            } else {
                                photoExist = false;
                            }
                            if (photoExist || MediaController.getInstance().canDownloadMedia(DOCUMENT_ATTACH_TYPE_DOCUMENT) || FileLoader.getInstance().isLoadingFile(fileName)) {
                                FileLocation fileLocation3;
                                int i2;
                                r16 = this.photoImage;
                                r17 = this.currentPhotoObject.location;
                                r18 = this.currentPhotoFilter;
                                if (this.currentPhotoObjectThumb != null) {
                                    fileLocation3 = this.currentPhotoObjectThumb.location;
                                } else {
                                    fileLocation3 = null;
                                }
                                String str5 = this.currentPhotoFilter;
                                if (noSize) {
                                    i2 = DOCUMENT_ATTACH_TYPE_NONE;
                                } else {
                                    i2 = this.currentPhotoObject.size;
                                }
                                r16.setImage(r17, r18, fileLocation3, str5, i2, null, false);
                            } else {
                                this.photoNotSet = true;
                                if (this.currentPhotoObjectThumb != null) {
                                    this.photoImage.setImage(null, null, this.currentPhotoObjectThumb.location, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                                } else {
                                    this.photoImage.setImageBitmap((Drawable) null);
                                }
                            }
                        } else {
                            this.photoImage.setImageBitmap((BitmapDrawable) null);
                        }
                    } else if (messageObject.type == 8) {
                        fileName = FileLoader.getAttachFileName(messageObject.messageOwner.media.document);
                        int localFile = DOCUMENT_ATTACH_TYPE_NONE;
                        if (messageObject.attachPathExists) {
                            MediaController.getInstance().removeLoadingFileObserver(this);
                            localFile = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                        } else if (messageObject.mediaExists) {
                            localFile = DOCUMENT_ATTACH_TYPE_GIF;
                        }
                        if (messageObject.isSending() || (localFile == 0 && !((MediaController.getInstance().canDownloadMedia(32) && MessageObject.isNewGifDocument(messageObject.messageOwner.media.document)) || FileLoader.getInstance().isLoadingFile(fileName)))) {
                            this.photoNotSet = true;
                            this.photoImage.setImage(null, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                        } else if (localFile == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            r16 = this.photoImage;
                            if (messageObject.isSendError()) {
                                r18 = null;
                            } else {
                                r18 = messageObject.messageOwner.attachPath;
                            }
                            if (this.currentPhotoObject != null) {
                                r21 = this.currentPhotoObject.location;
                            } else {
                                r21 = null;
                            }
                            r16.setImage(null, r18, null, null, r21, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                        } else {
                            this.photoImage.setImage(messageObject.messageOwner.media.document, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilter, messageObject.messageOwner.media.document.size, null, false);
                        }
                    } else {
                        this.photoImage.setImage(null, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilter, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                    }
                }
                setMessageObjectInternal(messageObject);
                if (this.drawForwardedName) {
                    this.namesOffset += AndroidUtilities.dp(5.0f);
                } else if (this.drawNameLayout && messageObject.messageOwner.reply_to_msg_id == 0) {
                    this.namesOffset += AndroidUtilities.dp(7.0f);
                }
                invalidate();
                this.photoImage.setImageCoords(DOCUMENT_ATTACH_TYPE_NONE, AndroidUtilities.dp(7.0f) + this.namesOffset, photoWidth, i);
                this.totalHeight = ((AndroidUtilities.dp(14.0f) + i) + this.namesOffset) + additionHeight;
            }
            if (!(this.captionLayout != null || messageObject.caption == null || messageObject.type == 13)) {
                width = this.backgroundWidth - AndroidUtilities.dp(31.0f);
                this.captionLayout = new StaticLayout(messageObject.caption, MessageObject.getTextPaint(), width - AndroidUtilities.dp(10.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                if (this.captionLayout.getLineCount() > 0) {
                    if (messageObject.isOutOwner()) {
                    }
                    timeWidthTotal = this.timeWidth + (messageObject.isOutOwner() ? DOCUMENT_ATTACH_TYPE_NONE : AndroidUtilities.dp(20.0f));
                    this.captionHeight = this.captionLayout.getHeight();
                    this.totalHeight += this.captionHeight + AndroidUtilities.dp(9.0f);
                    if (((float) (width - AndroidUtilities.dp(8.0f))) - (this.captionLayout.getLineWidth(this.captionLayout.getLineCount() - 1) + this.captionLayout.getLineLeft(this.captionLayout.getLineCount() - 1)) < ((float) timeWidthTotal)) {
                        this.totalHeight += AndroidUtilities.dp(14.0f);
                        this.captionHeight += AndroidUtilities.dp(14.0f);
                    }
                }
            }
            this.botButtons.clear();
            if (messageIdChanged) {
                this.botButtonsByData.clear();
            }
            if (messageObject.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                rows = messageObject.messageOwner.reply_markup.rows.size();
                photoSize = (AndroidUtilities.dp(48.0f) * rows) + AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                this.keyboardHeight = photoSize;
                this.substractBackgroundHeight = photoSize;
                this.widthForButtons = this.backgroundWidth;
                fullWidth = false;
                if (messageObject.wantedBotKeyboardWidth > this.widthForButtons) {
                    f = (this.isChat || !messageObject.isFromUser() || messageObject.isOutOwner()) ? 10.0f : 62.0f;
                    maxButtonWidth = -AndroidUtilities.dp(f);
                    if (AndroidUtilities.isTablet()) {
                        maxButtonWidth += AndroidUtilities.getMinTabletSide();
                    } else {
                        maxButtonWidth += Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                    }
                    this.widthForButtons = Math.max(this.backgroundWidth, Math.min(messageObject.wantedBotKeyboardWidth, maxButtonWidth));
                    fullWidth = true;
                }
                maxButtonsWidth = DOCUMENT_ATTACH_TYPE_NONE;
                for (a = DOCUMENT_ATTACH_TYPE_NONE; a < rows; a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    row = (TL_keyboardButtonRow) messageObject.messageOwner.reply_markup.rows.get(a);
                    buttonsCount = row.buttons.size();
                    dp2 = this.widthForButtons - (AndroidUtilities.dp(5.0f) * (buttonsCount - 1));
                    f = (fullWidth || !this.mediaBackground) ? 9.0f : 0.0f;
                    buttonWidth = ((dp2 - AndroidUtilities.dp(f)) - AndroidUtilities.dp(2.0f)) / buttonsCount;
                    for (b = DOCUMENT_ATTACH_TYPE_NONE; b < row.buttons.size(); b += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        chatMessageCell = this;
                        botButton = new BotButton();
                        botButton.button = (KeyboardButton) row.buttons.get(b);
                        key = Utilities.bytesToHex(botButton.button.data);
                        oldButton = (BotButton) this.botButtonsByData.get(key);
                        if (oldButton == null) {
                            botButton.progressAlpha = oldButton.progressAlpha;
                            botButton.angle = oldButton.angle;
                            botButton.lastUpdateTime = oldButton.lastUpdateTime;
                        } else {
                            botButton.lastUpdateTime = System.currentTimeMillis();
                        }
                        this.botButtonsByData.put(key, botButton);
                        botButton.f11x = (AndroidUtilities.dp(5.0f) + buttonWidth) * b;
                        botButton.f12y = (AndroidUtilities.dp(48.0f) * a) + AndroidUtilities.dp(5.0f);
                        botButton.width = buttonWidth;
                        botButton.height = AndroidUtilities.dp(44.0f);
                        botButton.title = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(botButton.button.text, botButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), botButtonPaint, (float) (buttonWidth - AndroidUtilities.dp(10.0f)), TruncateAt.END), botButtonPaint, buttonWidth - AndroidUtilities.dp(10.0f), Alignment.ALIGN_CENTER, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                        this.botButtons.add(botButton);
                        if (b != row.buttons.size() - 1) {
                            maxButtonsWidth = Math.max(maxButtonsWidth, botButton.f11x + botButton.width);
                        }
                    }
                }
                this.widthForButtons = maxButtonsWidth;
            } else {
                this.substractBackgroundHeight = DOCUMENT_ATTACH_TYPE_NONE;
                this.keyboardHeight = DOCUMENT_ATTACH_TYPE_NONE;
            }
        }
        updateWaveform();
        updateButtonState(dataChanged);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), this.totalHeight + this.keyboardHeight);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.currentMessageObject == null) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        if (changed || !this.wasLayout) {
            this.layoutWidth = getMeasuredWidth();
            this.layoutHeight = getMeasuredHeight() - this.substractBackgroundHeight;
            if (this.timeTextWidth < 0) {
                this.timeTextWidth = AndroidUtilities.dp(10.0f);
            }
            this.timeLayout = new StaticLayout(this.currentTimeString, timePaint, this.timeTextWidth + AndroidUtilities.dp(6.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
            int dp;
            int dp2;
            if (this.mediaBackground) {
                if (this.currentMessageObject.isOutOwner()) {
                    this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(42.0f);
                } else {
                    dp = (this.backgroundWidth - AndroidUtilities.dp(4.0f)) - this.timeWidth;
                    dp2 = (this.isChat && this.currentMessageObject.isFromUser()) ? AndroidUtilities.dp(48.0f) : DOCUMENT_ATTACH_TYPE_NONE;
                    this.timeX = dp2 + dp;
                }
            } else if (this.currentMessageObject.isOutOwner()) {
                this.timeX = (this.layoutWidth - this.timeWidth) - AndroidUtilities.dp(38.5f);
            } else {
                dp = (this.backgroundWidth - AndroidUtilities.dp(9.0f)) - this.timeWidth;
                if (this.isChat && this.currentMessageObject.isFromUser()) {
                    dp2 = AndroidUtilities.dp(48.0f);
                } else {
                    dp2 = DOCUMENT_ATTACH_TYPE_NONE;
                }
                this.timeX = dp2 + dp;
            }
            if ((this.currentMessageObject.messageOwner.flags & MessagesController.UPDATE_MASK_PHONE) != 0) {
                this.viewsLayout = new StaticLayout(this.currentViewsString, timePaint, this.viewsTextWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
            } else {
                this.viewsLayout = null;
            }
            if (this.isAvatarVisible) {
                this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0f), this.layoutHeight - AndroidUtilities.dp(44.0f), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            }
            this.wasLayout = true;
        }
        if (this.currentMessageObject.type == 0) {
            this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
        }
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
            if (this.currentMessageObject.isOutOwner()) {
                this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(57.0f);
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
            } else if (this.isChat && this.currentMessageObject.isFromUser()) {
                this.seekBarX = AndroidUtilities.dp(114.0f);
                this.buttonX = AndroidUtilities.dp(71.0f);
                this.timeAudioX = AndroidUtilities.dp(124.0f);
            } else {
                this.seekBarX = AndroidUtilities.dp(66.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            this.seekBarWaveform.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : DOCUMENT_ATTACH_TYPE_NONE) + 92)), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
            this.seekBar.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : DOCUMENT_ATTACH_TYPE_NONE) + 72)), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
            this.seekBarY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
            updateAudioProgress();
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (this.currentMessageObject.isOutOwner()) {
                this.seekBarX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(56.0f);
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
                this.timeAudioX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(67.0f);
            } else if (this.isChat && this.currentMessageObject.isFromUser()) {
                this.seekBarX = AndroidUtilities.dp(113.0f);
                this.buttonX = AndroidUtilities.dp(71.0f);
                this.timeAudioX = AndroidUtilities.dp(124.0f);
            } else {
                this.seekBarX = AndroidUtilities.dp(65.0f);
                this.buttonX = AndroidUtilities.dp(23.0f);
                this.timeAudioX = AndroidUtilities.dp(76.0f);
            }
            if (this.hasLinkPreview) {
                this.seekBarX += AndroidUtilities.dp(10.0f);
                this.buttonX += AndroidUtilities.dp(10.0f);
                this.timeAudioX += AndroidUtilities.dp(10.0f);
            }
            this.seekBar.setSize(this.backgroundWidth - AndroidUtilities.dp((float) ((this.hasLinkPreview ? 10 : DOCUMENT_ATTACH_TYPE_NONE) + 65)), AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
            this.seekBarY = (AndroidUtilities.dp(29.0f) + this.namesOffset) + this.mediaOffsetY;
            this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
            updateAudioProgress();
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT && !this.drawPhotoImage) {
            if (this.currentMessageObject.isOutOwner()) {
                this.buttonX = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
            } else if (this.isChat && this.currentMessageObject.isFromUser()) {
                this.buttonX = AndroidUtilities.dp(71.0f);
            } else {
                this.buttonX = AndroidUtilities.dp(23.0f);
            }
            if (this.hasLinkPreview) {
                this.buttonX += AndroidUtilities.dp(10.0f);
            }
            this.buttonY = (AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0f), this.buttonY + AndroidUtilities.dp(44.0f));
            this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0f), this.buttonY - AndroidUtilities.dp(10.0f), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        } else if (this.currentMessageObject.type == 12) {
            if (this.currentMessageObject.isOutOwner()) {
                x = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(14.0f);
            } else if (this.isChat && this.currentMessageObject.isFromUser()) {
                x = AndroidUtilities.dp(72.0f);
            } else {
                x = AndroidUtilities.dp(23.0f);
            }
            this.photoImage.setImageCoords(x, AndroidUtilities.dp(13.0f) + this.namesOffset, AndroidUtilities.dp(44.0f), AndroidUtilities.dp(44.0f));
        } else {
            if (this.currentMessageObject.isOutOwner()) {
                if (this.mediaBackground) {
                    x = (this.layoutWidth - this.backgroundWidth) - AndroidUtilities.dp(3.0f);
                } else {
                    x = (this.layoutWidth - this.backgroundWidth) + AndroidUtilities.dp(6.0f);
                }
            } else if (this.isChat && this.currentMessageObject.isFromUser()) {
                x = AndroidUtilities.dp(63.0f);
            } else {
                x = AndroidUtilities.dp(15.0f);
            }
            this.photoImage.setImageCoords(x, this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
            this.buttonX = (int) (((float) x) + (((float) (this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f))) / 2.0f));
            this.buttonY = ((int) (((float) AndroidUtilities.dp(7.0f)) + (((float) (this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f))) / 2.0f))) + this.namesOffset;
            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0f), this.buttonY + AndroidUtilities.dp(48.0f));
            this.deleteProgressRect.set((float) (this.buttonX + AndroidUtilities.dp(3.0f)), (float) (this.buttonY + AndroidUtilities.dp(3.0f)), (float) (this.buttonX + AndroidUtilities.dp(45.0f)), (float) (this.buttonY + AndroidUtilities.dp(45.0f)));
        }
    }

    private void drawContent(Canvas canvas) {
        int a;
        int b;
        int imageX;
        RadialProgress radialProgress;
        int i;
        Drawable menuDrawable;
        if (this.needNewVisiblePart && this.currentMessageObject.type == 0) {
            getLocalVisibleRect(this.scrollRect);
            setVisiblePart(this.scrollRect.top, this.scrollRect.bottom - this.scrollRect.top);
            this.needNewVisiblePart = false;
        }
        this.photoImage.setPressed(isDrawSelectedBackground());
        this.photoImage.setVisible(!PhotoViewer.getInstance().isShowingImage(this.currentMessageObject), false);
        this.radialProgress.setHideCurrentDrawable(false);
        this.radialProgress.setProgressColor(Theme.MSG_MEDIA_PROGRESS_COLOR);
        boolean imageDrawn = false;
        if (this.currentMessageObject.type == 0 && this.currentMessageObject.textLayoutBlocks != null && !this.currentMessageObject.textLayoutBlocks.isEmpty()) {
            if (this.currentMessageObject.isOutOwner()) {
                this.textX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
            } else {
                this.textX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0f);
            }
            this.textY = AndroidUtilities.dp(10.0f) + this.namesOffset;
            if (this.firstVisibleBlockNum >= 0) {
                a = this.firstVisibleBlockNum;
                while (a <= this.lastVisibleBlockNum && a < this.currentMessageObject.textLayoutBlocks.size()) {
                    TextLayoutBlock block = (TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(a);
                    canvas.save();
                    canvas.translate((float) (this.textX - ((int) Math.ceil((double) block.textXOffset))), ((float) this.textY) + block.textYOffset);
                    if (this.pressedLink != null && a == this.linkBlockNum) {
                        for (b = DOCUMENT_ATTACH_TYPE_NONE; b < this.urlPath.size(); b += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            canvas.drawPath((Path) this.urlPath.get(b), urlPaint);
                        }
                    }
                    if (a == this.linkSelectionBlockNum && !this.urlPathSelection.isEmpty()) {
                        for (b = DOCUMENT_ATTACH_TYPE_NONE; b < this.urlPathSelection.size(); b += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            canvas.drawPath((Path) this.urlPathSelection.get(b), urlSelectionPaint);
                        }
                    }
                    try {
                        block.textLayout.draw(canvas);
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                    canvas.restore();
                    a += DOCUMENT_ATTACH_TYPE_DOCUMENT;
                }
            }
            if (this.hasLinkPreview) {
                int startY = (this.textY + this.currentMessageObject.textHeight) + AndroidUtilities.dp(8.0f);
                int linkX = this.textX + AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                int linkPreviewY = startY;
                int smallImageStartY = DOCUMENT_ATTACH_TYPE_NONE;
                replyLinePaint.setColor(this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_WEB_PREVIEW_LINE_COLOR : Theme.MSG_IN_WEB_PREVIEW_LINE_COLOR);
                canvas.drawRect((float) linkX, (float) (linkPreviewY - AndroidUtilities.dp(3.0f)), (float) (AndroidUtilities.dp(2.0f) + linkX), (float) ((this.linkPreviewHeight + linkPreviewY) + AndroidUtilities.dp(3.0f)), replyLinePaint);
                if (this.siteNameLayout != null) {
                    replyNamePaint.setColor(this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_SITE_NAME_TEXT_COLOR : Theme.MSG_IN_SITE_NAME_TEXT_COLOR);
                    canvas.save();
                    canvas.translate((float) (AndroidUtilities.dp(10.0f) + linkX), (float) (linkPreviewY - AndroidUtilities.dp(3.0f)));
                    this.siteNameLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
                }
                replyNamePaint.setColor(Theme.MSG_TEXT_COLOR);
                replyTextPaint.setColor(Theme.MSG_TEXT_COLOR);
                if (this.titleLayout != null) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.dp(2.0f);
                    }
                    smallImageStartY = linkPreviewY - AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                    canvas.save();
                    canvas.translate((float) ((AndroidUtilities.dp(10.0f) + linkX) + this.titleX), (float) (linkPreviewY - AndroidUtilities.dp(3.0f)));
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
                }
                if (this.authorLayout != null) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.dp(2.0f);
                    }
                    if (smallImageStartY == 0) {
                        smallImageStartY = linkPreviewY - AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                    }
                    canvas.save();
                    canvas.translate((float) ((AndroidUtilities.dp(10.0f) + linkX) + this.authorX), (float) (linkPreviewY - AndroidUtilities.dp(3.0f)));
                    this.authorLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
                }
                if (this.descriptionLayout != null) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.dp(2.0f);
                    }
                    if (smallImageStartY == 0) {
                        smallImageStartY = linkPreviewY - AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                    }
                    this.descriptionY = linkPreviewY - AndroidUtilities.dp(3.0f);
                    canvas.save();
                    canvas.translate((float) ((AndroidUtilities.dp(10.0f) + linkX) + this.descriptionX), (float) this.descriptionY);
                    if (this.pressedLink != null && this.linkBlockNum == -10) {
                        for (b = DOCUMENT_ATTACH_TYPE_NONE; b < this.urlPath.size(); b += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            canvas.drawPath((Path) this.urlPath.get(b), urlPaint);
                        }
                    }
                    this.descriptionLayout.draw(canvas);
                    canvas.restore();
                    linkPreviewY += this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                }
                if (this.drawPhotoImage) {
                    if (linkPreviewY != startY) {
                        linkPreviewY += AndroidUtilities.dp(2.0f);
                    }
                    if (this.isSmallImage) {
                        this.photoImage.setImageCoords((this.backgroundWidth + linkX) - AndroidUtilities.dp(81.0f), smallImageStartY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                    } else {
                        this.photoImage.setImageCoords(AndroidUtilities.dp(10.0f) + linkX, linkPreviewY, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
                        if (this.drawImageButton) {
                            int size = AndroidUtilities.dp(48.0f);
                            this.buttonX = (int) (((float) this.photoImage.getImageX()) + (((float) (this.photoImage.getImageWidth() - size)) / 2.0f));
                            this.buttonY = (int) (((float) this.photoImage.getImageY()) + (((float) (this.photoImage.getImageHeight() - size)) / 2.0f));
                            this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0f), this.buttonY + AndroidUtilities.dp(48.0f));
                        }
                    }
                    imageDrawn = this.photoImage.draw(canvas);
                    if (this.videoInfoLayout != null) {
                        int x = ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(8.0f)) - this.durationWidth;
                        int y = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) - AndroidUtilities.dp(19.0f);
                        Theme.timeBackgroundDrawable.setBounds(x - AndroidUtilities.dp(4.0f), y - AndroidUtilities.dp(1.5f), (this.durationWidth + x) + AndroidUtilities.dp(4.0f), AndroidUtilities.dp(14.5f) + y);
                        Theme.timeBackgroundDrawable.draw(canvas);
                        canvas.save();
                        canvas.translate((float) x, (float) y);
                        this.videoInfoLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            }
            this.drawTime = true;
        } else if (this.drawPhotoImage) {
            imageDrawn = this.photoImage.draw(canvas);
            this.drawTime = this.photoImage.getVisible();
        }
        if (this.buttonState == -1 && this.currentMessageObject.isSecretPhoto()) {
            int drawable = DOCUMENT_ATTACH_TYPE_VIDEO;
            if (this.currentMessageObject.messageOwner.destroyTime != 0) {
                if (this.currentMessageObject.isOutOwner()) {
                    drawable = DOCUMENT_ATTACH_TYPE_STICKER;
                } else {
                    drawable = DOCUMENT_ATTACH_TYPE_MUSIC;
                }
            }
            setDrawableBounds(Theme.photoStatesDrawables[drawable][this.buttonPressed], this.buttonX, this.buttonY);
            Theme.photoStatesDrawables[drawable][this.buttonPressed].setAlpha((int) (255.0f * (TouchHelperCallback.ALPHA_FULL - this.radialProgress.getAlpha())));
            Theme.photoStatesDrawables[drawable][this.buttonPressed].draw(canvas);
            if (!(this.currentMessageObject.isOutOwner() || this.currentMessageObject.messageOwner.destroyTime == 0)) {
                float progress = ((float) Math.max(0, (((long) this.currentMessageObject.messageOwner.destroyTime) * 1000) - (System.currentTimeMillis() + ((long) (ConnectionsManager.getInstance().getTimeDifference() * 1000))))) / (((float) this.currentMessageObject.messageOwner.ttl) * 1000.0f);
                canvas.drawArc(this.deleteProgressRect, -90.0f, -360.0f * progress, true, deleteProgressPaint);
                if (progress != 0.0f) {
                    int offset = AndroidUtilities.dp(2.0f);
                    invalidate(((int) this.deleteProgressRect.left) - offset, ((int) this.deleteProgressRect.top) - offset, ((int) this.deleteProgressRect.right) + (offset * DOCUMENT_ATTACH_TYPE_GIF), ((int) this.deleteProgressRect.bottom) + (offset * DOCUMENT_ATTACH_TYPE_GIF));
                }
                updateSecretTimeText(this.currentMessageObject);
            }
        }
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF || this.currentMessageObject.type == 8) {
            if (this.photoImage.getVisible()) {
                Drawable drawable2 = Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_AUDIO];
                imageX = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                this.otherX = imageX;
                int imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                this.otherY = imageY;
                setDrawableBounds(drawable2, imageX, imageY);
                Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_AUDIO].draw(canvas);
            }
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (this.currentMessageObject.isOutOwner()) {
                audioTitlePaint.setColor(Theme.MSG_OUT_AUDIO_TITLE_TEXT_COLOR);
                audioPerformerPaint.setColor(Theme.MSG_OUT_AUDIO_PERFORMER_TEXT_COLOR);
                audioTimePaint.setColor(Theme.MSG_OUT_AUDIO_DURATION_TEXT_COLOR);
                radialProgress = this.radialProgress;
                i = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_OUT_AUDIO_SELECTED_PROGRESS_COLOR : Theme.MSG_OUT_AUDIO_PROGRESS_COLOR;
                radialProgress.setProgressColor(i);
            } else {
                audioTitlePaint.setColor(Theme.MSG_IN_AUDIO_TITLE_TEXT_COLOR);
                audioPerformerPaint.setColor(Theme.MSG_IN_AUDIO_PERFORMER_TEXT_COLOR);
                audioTimePaint.setColor(Theme.MSG_IN_AUDIO_DURATION_TEXT_COLOR);
                radialProgress = this.radialProgress;
                i = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_IN_AUDIO_SELECTED_PROGRESS_COLOR : Theme.MSG_IN_AUDIO_PROGRESS_COLOR;
                radialProgress.setProgressColor(i);
            }
            this.radialProgress.draw(canvas);
            canvas.save();
            canvas.translate((float) (this.timeAudioX + this.songX), (float) ((AndroidUtilities.dp(13.0f) + this.namesOffset) + this.mediaOffsetY));
            this.songLayout.draw(canvas);
            canvas.restore();
            canvas.save();
            if (MediaController.getInstance().isPlayingAudio(this.currentMessageObject)) {
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
            } else {
                canvas.translate((float) (this.timeAudioX + this.performerX), (float) ((AndroidUtilities.dp(35.0f) + this.namesOffset) + this.mediaOffsetY));
                this.performerLayout.draw(canvas);
            }
            canvas.restore();
            canvas.save();
            canvas.translate((float) this.timeAudioX, (float) ((AndroidUtilities.dp(57.0f) + this.namesOffset) + this.mediaOffsetY));
            this.durationLayout.draw(canvas);
            canvas.restore();
            if (this.currentMessageObject.isOutOwner()) {
                menuDrawable = Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_DOCUMENT];
            } else {
                menuDrawable = Theme.docMenuDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_GIF : DOCUMENT_ATTACH_TYPE_NONE];
            }
            i = (this.backgroundWidth + this.buttonX) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
            this.otherX = i;
            imageX = this.buttonY - AndroidUtilities.dp(5.0f);
            this.otherY = imageX;
            setDrawableBounds(menuDrawable, i, imageX);
            menuDrawable.draw(canvas);
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO) {
            if (this.currentMessageObject.isOutOwner()) {
                audioTimePaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_AUDIO_DURATION_SELECTED_TEXT_COLOR : Theme.MSG_OUT_AUDIO_DURATION_TEXT_COLOR);
                radialProgress = this.radialProgress;
                i = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_OUT_AUDIO_SELECTED_PROGRESS_COLOR : Theme.MSG_OUT_AUDIO_PROGRESS_COLOR;
                radialProgress.setProgressColor(i);
            } else {
                audioTimePaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_AUDIO_DURATION_SELECTED_TEXT_COLOR : Theme.MSG_IN_AUDIO_DURATION_TEXT_COLOR);
                radialProgress = this.radialProgress;
                i = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_IN_AUDIO_SELECTED_PROGRESS_COLOR : Theme.MSG_IN_AUDIO_PROGRESS_COLOR;
                radialProgress.setProgressColor(i);
            }
            this.radialProgress.draw(canvas);
            canvas.save();
            if (this.useSeekBarWaweform) {
                canvas.translate((float) (this.seekBarX + AndroidUtilities.dp(13.0f)), (float) this.seekBarY);
                this.seekBarWaveform.draw(canvas);
            } else {
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
            }
            canvas.restore();
            canvas.save();
            canvas.translate((float) this.timeAudioX, (float) ((AndroidUtilities.dp(44.0f) + this.namesOffset) + this.mediaOffsetY));
            this.durationLayout.draw(canvas);
            canvas.restore();
            if (this.currentMessageObject.type != 0 && this.currentMessageObject.messageOwner.to_id.channel_id == 0 && this.currentMessageObject.isContentUnread()) {
                docBackPaint.setColor(this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_VOICE_SEEKBAR_FILL_COLOR : Theme.MSG_IN_VOICE_SEEKBAR_FILL_COLOR);
                canvas.drawCircle((float) ((this.timeAudioX + this.timeWidthAudio) + AndroidUtilities.dp(6.0f)), (float) ((AndroidUtilities.dp(51.0f) + this.namesOffset) + this.mediaOffsetY), (float) AndroidUtilities.dp(3.0f), docBackPaint);
            }
        }
        if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (this.photoImage.getVisible()) {
                if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
                    drawable2 = Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_AUDIO];
                    imageX = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) - AndroidUtilities.dp(14.0f);
                    this.otherX = imageX;
                    imageY = this.photoImage.getImageY() + AndroidUtilities.dp(8.1f);
                    this.otherY = imageY;
                    setDrawableBounds(drawable2, imageX, imageY);
                    Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_AUDIO].draw(canvas);
                }
                if (this.infoLayout != null && (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.buttonState == 0 || this.buttonState == DOCUMENT_ATTACH_TYPE_AUDIO || this.currentMessageObject.isSecretPhoto())) {
                    infoPaint.setColor(Theme.MSG_MEDIA_INFO_TEXT_COLOR);
                    setDrawableBounds(Theme.timeBackgroundDrawable, AndroidUtilities.dp(4.0f) + this.photoImage.getImageX(), AndroidUtilities.dp(4.0f) + this.photoImage.getImageY(), AndroidUtilities.dp(8.0f) + this.infoWidth, AndroidUtilities.dp(16.5f));
                    Theme.timeBackgroundDrawable.draw(canvas);
                    canvas.save();
                    canvas.translate((float) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f)), (float) (this.photoImage.getImageY() + AndroidUtilities.dp(5.5f)));
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            }
        } else if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (this.docTitleLayout != null) {
                if (this.currentMessageObject.isOutOwner()) {
                    locationTitlePaint.setColor(Theme.MSG_OUT_VENUE_NAME_TEXT_COLOR);
                    locationAddressPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_VENUE_INFO_SELECTED_TEXT_COLOR : Theme.MSG_OUT_VENUE_INFO_TEXT_COLOR);
                } else {
                    locationTitlePaint.setColor(Theme.MSG_IN_VENUE_NAME_TEXT_COLOR);
                    locationAddressPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_VENUE_INFO_SELECTED_TEXT_COLOR : Theme.MSG_IN_VENUE_INFO_TEXT_COLOR);
                }
                canvas.save();
                canvas.translate((float) (((this.docTitleOffsetX + this.photoImage.getImageX()) + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f)));
                this.docTitleLayout.draw(canvas);
                canvas.restore();
                if (this.infoLayout != null) {
                    canvas.save();
                    canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f)), (float) ((this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f)));
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            }
        } else if (this.currentMessageObject.type == 12) {
            contactNamePaint.setColor(this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_CONTACT_NAME_TEXT_COLOR : Theme.MSG_IN_CONTACT_NAME_TEXT_COLOR);
            contactPhonePaint.setColor(this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_CONTACT_PHONE_TEXT_COLOR : Theme.MSG_IN_CONTACT_PHONE_TEXT_COLOR);
            if (this.titleLayout != null) {
                canvas.save();
                canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(16.0f) + this.namesOffset));
                this.titleLayout.draw(canvas);
                canvas.restore();
            }
            if (this.docTitleLayout != null) {
                canvas.save();
                canvas.translate((float) ((this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(9.0f)), (float) (AndroidUtilities.dp(39.0f) + this.namesOffset));
                this.docTitleLayout.draw(canvas);
                canvas.restore();
            }
            if (this.currentMessageObject.isOutOwner()) {
                menuDrawable = Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_DOCUMENT];
            } else {
                menuDrawable = Theme.docMenuDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_GIF : DOCUMENT_ATTACH_TYPE_NONE];
            }
            i = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(48.0f);
            this.otherX = i;
            imageX = this.photoImage.getImageY() - AndroidUtilities.dp(5.0f);
            this.otherY = imageX;
            setDrawableBounds(menuDrawable, i, imageX);
            menuDrawable.draw(canvas);
        }
        if (this.captionLayout != null) {
            canvas.save();
            float f;
            if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO || this.currentMessageObject.type == 8) {
                i = this.photoImage.getImageX() + AndroidUtilities.dp(5.0f);
                this.captionX = i;
                f = (float) i;
                imageX = (this.photoImage.getImageY() + this.photoImage.getImageHeight()) + AndroidUtilities.dp(6.0f);
                this.captionY = imageX;
                canvas.translate(f, (float) imageX);
            } else {
                i = AndroidUtilities.dp(this.currentMessageObject.isOutOwner() ? 11.0f : 17.0f) + this.currentBackgroundDrawable.getBounds().left;
                this.captionX = i;
                f = (float) i;
                imageX = (this.totalHeight - this.captionHeight) - AndroidUtilities.dp(10.0f);
                this.captionY = imageX;
                canvas.translate(f, (float) imageX);
            }
            if (this.pressedLink != null) {
                for (b = DOCUMENT_ATTACH_TYPE_NONE; b < this.urlPath.size(); b += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    canvas.drawPath((Path) this.urlPath.get(b), urlPaint);
                }
            }
            try {
                this.captionLayout.draw(canvas);
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
            canvas.restore();
        }
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            int titleY;
            int subtitleY;
            if (this.currentMessageObject.isOutOwner()) {
                docNamePaint.setColor(Theme.MSG_OUT_FILE_NAME_TEXT_COLOR);
                infoPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_FILE_INFO_SELECTED_TEXT_COLOR : Theme.MSG_OUT_FILE_INFO_TEXT_COLOR);
                docBackPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_FILE_BACKGROUND_SELECTED_COLOR : Theme.MSG_OUT_FILE_BACKGROUND_COLOR);
                menuDrawable = Theme.docMenuDrawable[DOCUMENT_ATTACH_TYPE_DOCUMENT];
            } else {
                docNamePaint.setColor(Theme.MSG_IN_FILE_NAME_TEXT_COLOR);
                infoPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_FILE_INFO_SELECTED_TEXT_COLOR : Theme.MSG_IN_FILE_INFO_TEXT_COLOR);
                docBackPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_FILE_BACKGROUND_SELECTED_COLOR : Theme.MSG_IN_FILE_BACKGROUND_COLOR);
                menuDrawable = Theme.docMenuDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_GIF : DOCUMENT_ATTACH_TYPE_NONE];
            }
            if (this.drawPhotoImage) {
                if (this.currentMessageObject.type == 0) {
                    i = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(56.0f);
                    this.otherX = i;
                    imageX = this.photoImage.getImageY() + AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                    this.otherY = imageX;
                    setDrawableBounds(menuDrawable, i, imageX);
                } else {
                    i = (this.photoImage.getImageX() + this.backgroundWidth) - AndroidUtilities.dp(40.0f);
                    this.otherX = i;
                    imageX = this.photoImage.getImageY() + AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                    this.otherY = imageX;
                    setDrawableBounds(menuDrawable, i, imageX);
                }
                x = (this.photoImage.getImageX() + this.photoImage.getImageWidth()) + AndroidUtilities.dp(10.0f);
                titleY = this.photoImage.getImageY() + AndroidUtilities.dp(8.0f);
                subtitleY = (this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1)) + AndroidUtilities.dp(13.0f);
                if (this.buttonState >= 0 && this.buttonState < DOCUMENT_ATTACH_TYPE_VIDEO) {
                    if (imageDrawn) {
                        this.radialProgress.swapBackground(Theme.photoStatesDrawables[this.buttonState][this.buttonPressed]);
                    } else {
                        int image = this.buttonState;
                        if (this.buttonState == 0) {
                            image = this.currentMessageObject.isOutOwner() ? 7 : 10;
                        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                            image = this.currentMessageObject.isOutOwner() ? 8 : 11;
                        }
                        radialProgress = this.radialProgress;
                        Drawable[] drawableArr = Theme.photoStatesDrawables[image];
                        i = (isDrawSelectedBackground() || this.buttonPressed != 0) ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE;
                        radialProgress.swapBackground(drawableArr[i]);
                    }
                }
                if (imageDrawn) {
                    if (this.buttonState == -1) {
                        this.radialProgress.setHideCurrentDrawable(true);
                    }
                    this.radialProgress.setProgressColor(Theme.MSG_MEDIA_PROGRESS_COLOR);
                } else {
                    this.rect.set((float) this.photoImage.getImageX(), (float) this.photoImage.getImageY(), (float) (this.photoImage.getImageX() + this.photoImage.getImageWidth()), (float) (this.photoImage.getImageY() + this.photoImage.getImageHeight()));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), docBackPaint);
                    if (this.currentMessageObject.isOutOwner()) {
                        radialProgress = this.radialProgress;
                        if (isDrawSelectedBackground()) {
                            i = Theme.MSG_OUT_FILE_PROGRESS_SELECTED_COLOR;
                        } else {
                            i = Theme.MSG_OUT_FILE_PROGRESS_COLOR;
                        }
                        radialProgress.setProgressColor(i);
                    } else {
                        this.radialProgress.setProgressColor(isDrawSelectedBackground() ? Theme.MSG_IN_FILE_PROGRESS_SELECTED_COLOR : Theme.MSG_IN_FILE_PROGRESS_COLOR);
                    }
                }
            } else {
                i = (this.backgroundWidth + this.buttonX) - AndroidUtilities.dp(this.currentMessageObject.type == 0 ? 58.0f : 48.0f);
                this.otherX = i;
                imageX = this.buttonY - AndroidUtilities.dp(5.0f);
                this.otherY = imageX;
                setDrawableBounds(menuDrawable, i, imageX);
                x = this.buttonX + AndroidUtilities.dp(53.0f);
                titleY = this.buttonY + AndroidUtilities.dp(4.0f);
                subtitleY = this.buttonY + AndroidUtilities.dp(27.0f);
                if (this.currentMessageObject.isOutOwner()) {
                    radialProgress = this.radialProgress;
                    if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                        i = Theme.MSG_OUT_AUDIO_SELECTED_PROGRESS_COLOR;
                    } else {
                        i = Theme.MSG_OUT_AUDIO_PROGRESS_COLOR;
                    }
                    radialProgress.setProgressColor(i);
                } else {
                    radialProgress = this.radialProgress;
                    i = (isDrawSelectedBackground() || this.buttonPressed != 0) ? Theme.MSG_IN_AUDIO_SELECTED_PROGRESS_COLOR : Theme.MSG_IN_AUDIO_PROGRESS_COLOR;
                    radialProgress.setProgressColor(i);
                }
            }
            menuDrawable.draw(canvas);
            try {
                if (this.docTitleLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.docTitleOffsetX + x), (float) titleY);
                    this.docTitleLayout.draw(canvas);
                    canvas.restore();
                }
            } catch (Throwable e22) {
                FileLog.m13e("tmessages", e22);
            }
            try {
                if (this.infoLayout != null) {
                    canvas.save();
                    canvas.translate((float) x, (float) subtitleY);
                    this.infoLayout.draw(canvas);
                    canvas.restore();
                }
            } catch (Throwable e222) {
                FileLog.m13e("tmessages", e222);
            }
        }
        if (this.drawImageButton && this.photoImage.getVisible()) {
            this.radialProgress.draw(canvas);
        }
        if (!this.botButtons.isEmpty()) {
            int addX;
            if (this.currentMessageObject.isOutOwner()) {
                addX = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                addX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(this.mediaBackground ? TouchHelperCallback.ALPHA_FULL : 7.0f);
            }
            a = DOCUMENT_ATTACH_TYPE_NONE;
            while (a < this.botButtons.size()) {
                BotButton button = (BotButton) this.botButtons.get(a);
                y = (button.f12y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                Theme.systemDrawable.setColorFilter(a == this.pressedBotButton ? Theme.colorPressedFilter : Theme.colorFilter);
                Theme.systemDrawable.setBounds(button.f11x + addX, y, (button.f11x + addX) + button.width, button.height + y);
                Theme.systemDrawable.draw(canvas);
                canvas.save();
                canvas.translate((float) ((button.f11x + addX) + AndroidUtilities.dp(5.0f)), (float) (((AndroidUtilities.dp(44.0f) - button.title.getLineBottom(button.title.getLineCount() - 1)) / DOCUMENT_ATTACH_TYPE_GIF) + y));
                button.title.draw(canvas);
                canvas.restore();
                if (button.button instanceof TL_keyboardButtonUrl) {
                    setDrawableBounds(Theme.botLink, (((button.f11x + button.width) - AndroidUtilities.dp(3.0f)) - Theme.botLink.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y);
                    Theme.botLink.draw(canvas);
                } else if (button.button instanceof TL_keyboardButtonSwitchInline) {
                    setDrawableBounds(Theme.botInline, (((button.f11x + button.width) - AndroidUtilities.dp(3.0f)) - Theme.botInline.getIntrinsicWidth()) + addX, AndroidUtilities.dp(3.0f) + y);
                    Theme.botInline.draw(canvas);
                } else if ((button.button instanceof TL_keyboardButtonCallback) || (button.button instanceof TL_keyboardButtonRequestGeoLocation)) {
                    boolean drawProgress = ((button.button instanceof TL_keyboardButtonCallback) && SendMessagesHelper.getInstance().isSendingCallback(this.currentMessageObject, button.button)) || ((button.button instanceof TL_keyboardButtonRequestGeoLocation) && SendMessagesHelper.getInstance().isSendingCurrentLocation(this.currentMessageObject, button.button));
                    if (drawProgress || !(drawProgress || button.progressAlpha == 0.0f)) {
                        botProgressPaint.setAlpha(Math.min(NalUnitUtil.EXTENDED_SAR, (int) (button.progressAlpha * 255.0f)));
                        x = ((button.f11x + button.width) - AndroidUtilities.dp(12.0f)) + addX;
                        this.rect.set((float) x, (float) (AndroidUtilities.dp(4.0f) + y), (float) (AndroidUtilities.dp(8.0f) + x), (float) (AndroidUtilities.dp(12.0f) + y));
                        canvas.drawArc(this.rect, (float) button.angle, 220.0f, false, botProgressPaint);
                        invalidate(((int) this.rect.left) - AndroidUtilities.dp(2.0f), ((int) this.rect.top) - AndroidUtilities.dp(2.0f), ((int) this.rect.right) + AndroidUtilities.dp(2.0f), ((int) this.rect.bottom) + AndroidUtilities.dp(2.0f));
                        long newTime = System.currentTimeMillis();
                        if (Math.abs(button.lastUpdateTime - System.currentTimeMillis()) < 1000) {
                            long delta = newTime - button.lastUpdateTime;
                            BotButton.access$716(button, ((float) (360 * delta)) / 2000.0f);
                            BotButton.access$720(button, (button.angle / 360) * 360);
                            if (drawProgress) {
                                if (button.progressAlpha < TouchHelperCallback.ALPHA_FULL) {
                                    BotButton.access$616(button, ((float) delta) / 200.0f);
                                    if (button.progressAlpha > TouchHelperCallback.ALPHA_FULL) {
                                        button.progressAlpha = TouchHelperCallback.ALPHA_FULL;
                                    }
                                }
                            } else if (button.progressAlpha > 0.0f) {
                                BotButton.access$624(button, ((float) delta) / 200.0f);
                                if (button.progressAlpha < 0.0f) {
                                    button.progressAlpha = 0.0f;
                                }
                            }
                        }
                        button.lastUpdateTime = newTime;
                    }
                }
                a += DOCUMENT_ATTACH_TYPE_DOCUMENT;
            }
        }
    }

    private Drawable getDrawableForCurrentState() {
        int i = DOCUMENT_ATTACH_TYPE_AUDIO;
        int i2 = DOCUMENT_ATTACH_TYPE_NONE;
        int i3 = DOCUMENT_ATTACH_TYPE_DOCUMENT;
        if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC) {
            Drawable[] drawableArr;
            if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT || this.drawPhotoImage) {
                this.radialProgress.setAlphaForPrevious(true);
                if (this.buttonState < 0 || this.buttonState >= DOCUMENT_ATTACH_TYPE_VIDEO) {
                    if (this.buttonState == -1 && this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        drawableArr = Theme.photoStatesDrawables[this.currentMessageObject.isOutOwner() ? 9 : 12];
                        if (!isDrawSelectedBackground()) {
                            i3 = DOCUMENT_ATTACH_TYPE_NONE;
                        }
                        return drawableArr[i3];
                    }
                } else if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    return Theme.photoStatesDrawables[this.buttonState][this.buttonPressed];
                } else {
                    int image = this.buttonState;
                    if (this.buttonState == 0) {
                        image = this.currentMessageObject.isOutOwner() ? 7 : 10;
                    } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        image = this.currentMessageObject.isOutOwner() ? 8 : 11;
                    }
                    drawableArr = Theme.photoStatesDrawables[image];
                    if (isDrawSelectedBackground() || this.buttonPressed != 0) {
                        i2 = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                    }
                    return drawableArr[i2];
                }
            }
            this.radialProgress.setAlphaForPrevious(false);
            if (this.buttonState == -1) {
                Drawable[][] drawableArr2 = Theme.fileStatesDrawable;
                if (!this.currentMessageObject.isOutOwner()) {
                    i = 8;
                }
                drawableArr = drawableArr2[i];
                if (!isDrawSelectedBackground()) {
                    i3 = DOCUMENT_ATTACH_TYPE_NONE;
                }
                return drawableArr[i3];
            } else if (this.buttonState == 0) {
                drawableArr = Theme.fileStatesDrawable[this.currentMessageObject.isOutOwner() ? DOCUMENT_ATTACH_TYPE_GIF : 7];
                if (!isDrawSelectedBackground()) {
                    i3 = DOCUMENT_ATTACH_TYPE_NONE;
                }
                return drawableArr[i3];
            } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                drawableArr = Theme.fileStatesDrawable[this.currentMessageObject.isOutOwner() ? DOCUMENT_ATTACH_TYPE_VIDEO : 9];
                if (!isDrawSelectedBackground()) {
                    i3 = DOCUMENT_ATTACH_TYPE_NONE;
                }
                return drawableArr[i3];
            }
            return null;
        } else if (this.buttonState == -1) {
            return null;
        } else {
            this.radialProgress.setAlphaForPrevious(false);
            Drawable[] drawableArr3 = Theme.fileStatesDrawable[this.currentMessageObject.isOutOwner() ? this.buttonState : this.buttonState + DOCUMENT_ATTACH_TYPE_MUSIC];
            i = (isDrawSelectedBackground() || this.buttonPressed != 0) ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE;
            return drawableArr3[i];
        }
    }

    private int getMaxNameWidth() {
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_STICKER) {
            int maxWidth;
            if (AndroidUtilities.isTablet()) {
                if (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.isFromUser()) {
                    maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(42.0f);
                } else {
                    maxWidth = AndroidUtilities.getMinTabletSide();
                }
            } else if (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.isFromUser()) {
                maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(42.0f);
            } else {
                maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            }
            return (maxWidth - this.backgroundWidth) - AndroidUtilities.dp(57.0f);
        }
        return this.backgroundWidth - AndroidUtilities.dp(this.mediaBackground ? 22.0f : 31.0f);
    }

    public void updateButtonState(boolean animated) {
        String fileName = null;
        boolean fileExists = false;
        if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            if (this.currentPhotoObject != null) {
                fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                fileExists = this.currentMessageObject.mediaExists;
            } else {
                return;
            }
        } else if (this.currentMessageObject.type == 8 || this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO || this.currentMessageObject.type == 9 || this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (this.currentMessageObject.attachPathExists) {
                fileName = this.currentMessageObject.messageOwner.attachPath;
                fileExists = true;
            } else if (!this.currentMessageObject.isSendError() || this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                fileName = this.currentMessageObject.getFileName();
                fileExists = this.currentMessageObject.mediaExists;
            }
        } else if (this.documentAttachType != 0) {
            fileName = FileLoader.getAttachFileName(this.documentAttach);
            fileExists = this.currentMessageObject.mediaExists;
        } else if (this.currentPhotoObject != null) {
            fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
            fileExists = this.currentMessageObject.mediaExists;
        }
        if (fileName == null || fileName.length() == 0) {
            this.radialProgress.setBackground(null, false, false);
            return;
        }
        boolean fromBot = this.currentMessageObject.messageOwner.params != null && this.currentMessageObject.messageOwner.params.containsKey("query_id");
        Float progress;
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if ((this.currentMessageObject.isOut() && this.currentMessageObject.isSending()) || (this.currentMessageObject.isSendError() && fromBot)) {
                MediaController.getInstance().addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
                this.buttonState = DOCUMENT_ATTACH_TYPE_VIDEO;
                this.radialProgress.setBackground(getDrawableForCurrentState(), !fromBot, animated);
                if (fromBot) {
                    this.radialProgress.setProgress(0.0f, false);
                } else {
                    float floatValue;
                    progress = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
                    if (progress == null && SendMessagesHelper.getInstance().isSendingMessage(this.currentMessageObject.getId())) {
                        progress = Float.valueOf(TouchHelperCallback.ALPHA_FULL);
                    }
                    RadialProgress radialProgress = this.radialProgress;
                    if (progress != null) {
                        floatValue = progress.floatValue();
                    } else {
                        floatValue = 0.0f;
                    }
                    radialProgress.setProgress(floatValue, false);
                }
            } else if (fileExists) {
                MediaController.getInstance().removeLoadingFileObserver(this);
                boolean playing = MediaController.getInstance().isPlayingAudio(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isAudioPaused())) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                } else {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
            } else {
                MediaController.getInstance().addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (FileLoader.getInstance().isLoadingFile(fileName)) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_VIDEO;
                    progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                } else {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                }
            }
            updateAudioProgress();
        } else if (this.currentMessageObject.type != 0 || this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
            if (!this.currentMessageObject.isOut() || !this.currentMessageObject.isSending()) {
                if (!(this.currentMessageObject.messageOwner.attachPath == null || this.currentMessageObject.messageOwner.attachPath.length() == 0)) {
                    MediaController.getInstance().removeLoadingFileObserver(this);
                }
                if (fileExists) {
                    MediaController.getInstance().removeLoadingFileObserver(this);
                    if (this.currentMessageObject.type == 8 && !this.photoImage.isAllowStartAnimation()) {
                        this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                    } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
                        this.buttonState = DOCUMENT_ATTACH_TYPE_AUDIO;
                    } else {
                        this.buttonState = -1;
                    }
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                    if (this.photoNotSet) {
                        setMessageObject(this.currentMessageObject);
                    }
                    invalidate();
                    return;
                }
                MediaController.getInstance().addLoadingFileObserver(fileName, this.currentMessageObject, this);
                setProgress = 0.0f;
                progressVisible = false;
                if (FileLoader.getInstance().isLoadingFile(fileName)) {
                    progressVisible = true;
                    this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                    progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                } else if (this.cancelLoading || !((this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT && MediaController.getInstance().canDownloadMedia(DOCUMENT_ATTACH_TYPE_DOCUMENT)) || (this.currentMessageObject.type == 8 && MediaController.getInstance().canDownloadMedia(32) && MessageObject.isNewGifDocument(this.currentMessageObject.messageOwner.media.document)))) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                } else {
                    progressVisible = true;
                    this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), progressVisible, animated);
                this.radialProgress.setProgress(setProgress, false);
                invalidate();
            } else if (this.currentMessageObject.messageOwner.attachPath != null && this.currentMessageObject.messageOwner.attachPath.length() > 0) {
                MediaController.getInstance().addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
                boolean needProgress = this.currentMessageObject.messageOwner.attachPath == null || !this.currentMessageObject.messageOwner.attachPath.startsWith("http");
                HashMap<String, String> params = this.currentMessageObject.messageOwner.params;
                if (this.currentMessageObject.messageOwner.message == null || params == null || !(params.containsKey(UpdateFragment.FRAGMENT_URL) || params.containsKey("bot"))) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                } else {
                    needProgress = false;
                    this.buttonState = -1;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), needProgress, animated);
                if (needProgress) {
                    progress = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
                    if (progress == null && SendMessagesHelper.getInstance().isSendingMessage(this.currentMessageObject.getId())) {
                        progress = Float.valueOf(TouchHelperCallback.ALPHA_FULL);
                    }
                    this.radialProgress.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
                } else {
                    this.radialProgress.setProgress(0.0f, false);
                }
                invalidate();
            }
        } else if (this.currentPhotoObject != null && this.drawImageButton) {
            if (fileExists) {
                MediaController.getInstance().removeLoadingFileObserver(this);
                if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_GIF || this.photoImage.isAllowStartAnimation()) {
                    this.buttonState = -1;
                } else {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                invalidate();
                return;
            }
            MediaController.getInstance().addLoadingFileObserver(fileName, this.currentMessageObject, this);
            setProgress = 0.0f;
            progressVisible = false;
            if (FileLoader.getInstance().isLoadingFile(fileName)) {
                progressVisible = true;
                this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                progress = ImageLoader.getInstance().getFileProgress(fileName);
                setProgress = progress != null ? progress.floatValue() : 0.0f;
            } else if (this.cancelLoading || !((this.documentAttachType == 0 && MediaController.getInstance().canDownloadMedia(DOCUMENT_ATTACH_TYPE_DOCUMENT)) || (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && MediaController.getInstance().canDownloadMedia(32)))) {
                this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
            } else {
                progressVisible = true;
                this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
            }
            this.radialProgress.setProgress(setProgress, false);
            this.radialProgress.setBackground(getDrawableForCurrentState(), progressVisible, animated);
            invalidate();
        }
    }

    private void didPressedButton(boolean animated) {
        if (this.buttonState == 0) {
            if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC) {
                this.cancelLoading = false;
                this.radialProgress.setProgress(0.0f, false);
                if (this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    FileLocation fileLocation;
                    ImageReceiver imageReceiver = this.photoImage;
                    TLObject tLObject = this.currentPhotoObject.location;
                    String str = this.currentPhotoFilter;
                    if (this.currentPhotoObjectThumb != null) {
                        fileLocation = this.currentPhotoObjectThumb.location;
                    } else {
                        fileLocation = null;
                    }
                    imageReceiver.setImage(tLObject, str, fileLocation, this.currentPhotoFilter, this.currentPhotoObject.size, null, false);
                } else if (this.currentMessageObject.type == 8) {
                    this.currentMessageObject.audioProgress = 2.0f;
                    this.photoImage.setImage(this.currentMessageObject.messageOwner.media.document, null, this.currentPhotoObject != null ? this.currentPhotoObject.location : null, this.currentPhotoFilter, this.currentMessageObject.messageOwner.media.document.size, null, false);
                } else if (this.currentMessageObject.type == 9) {
                    FileLoader.getInstance().loadFile(this.currentMessageObject.messageOwner.media.document, false, false);
                } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO) {
                    FileLoader.getInstance().loadFile(this.documentAttach, true, false);
                } else if (this.currentMessageObject.type != 0 || this.documentAttachType == 0) {
                    this.photoImage.setImage(this.currentPhotoObject.location, this.currentPhotoFilter, this.currentPhotoObjectThumb != null ? this.currentPhotoObjectThumb.location : null, this.currentPhotoFilterThumb, DOCUMENT_ATTACH_TYPE_NONE, null, false);
                } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF) {
                    this.photoImage.setImage(this.currentMessageObject.messageOwner.media.webpage.document, null, this.currentPhotoObject.location, this.currentPhotoFilter, this.currentMessageObject.messageOwner.media.webpage.document.size, null, false);
                    this.currentMessageObject.audioProgress = 2.0f;
                } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    FileLoader.getInstance().loadFile(this.currentMessageObject.messageOwner.media.webpage.document, false, false);
                }
                this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                invalidate();
            } else if (this.delegate.needPlayAudio(this.currentMessageObject)) {
                this.buttonState = DOCUMENT_ATTACH_TYPE_DOCUMENT;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                if (MediaController.getInstance().pauseAudio(this.currentMessageObject)) {
                    this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            } else if (this.currentMessageObject.isOut() && this.currentMessageObject.isSending()) {
                this.delegate.didPressedCancelSendButton(this);
            } else {
                this.cancelLoading = true;
                if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_VIDEO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                    FileLoader.getInstance().cancelLoadFile(this.documentAttach);
                } else if (this.currentMessageObject.type == 0 || this.currentMessageObject.type == DOCUMENT_ATTACH_TYPE_DOCUMENT || this.currentMessageObject.type == 8) {
                    this.photoImage.cancelLoadImage();
                } else if (this.currentMessageObject.type == 9) {
                    FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.messageOwner.media.document);
                }
                this.buttonState = DOCUMENT_ATTACH_TYPE_NONE;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                invalidate();
            }
        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_GIF) {
            if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance().loadFile(this.documentAttach, true, false);
                this.buttonState = DOCUMENT_ATTACH_TYPE_VIDEO;
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
                invalidate();
                return;
            }
            this.photoImage.setAllowStartAnimation(true);
            this.photoImage.startAnimation();
            this.currentMessageObject.audioProgress = 0.0f;
            this.buttonState = -1;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
        } else if (this.buttonState == DOCUMENT_ATTACH_TYPE_AUDIO) {
            this.delegate.didPressedImage(this);
        } else if (this.buttonState != DOCUMENT_ATTACH_TYPE_VIDEO) {
        } else {
            if (this.documentAttachType != DOCUMENT_ATTACH_TYPE_AUDIO && this.documentAttachType != DOCUMENT_ATTACH_TYPE_MUSIC) {
                return;
            }
            if ((!this.currentMessageObject.isOut() || !this.currentMessageObject.isSending()) && !this.currentMessageObject.isSendError()) {
                FileLoader.getInstance().cancelLoadFile(this.documentAttach);
                this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            } else if (this.delegate != null) {
                this.delegate.didPressedCancelSendButton(this);
            }
        }
    }

    public void onFailedDownload(String fileName) {
        boolean z = this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC;
        updateButtonState(z);
    }

    public void onSuccessDownload(String fileName) {
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            updateButtonState(true);
            updateWaveform();
            return;
        }
        this.radialProgress.setProgress(TouchHelperCallback.ALPHA_FULL, true);
        if (this.currentMessageObject.type != 0) {
            if (!this.photoNotSet || (this.currentMessageObject.type == 8 && this.currentMessageObject.audioProgress != TouchHelperCallback.ALPHA_FULL)) {
                if (this.currentMessageObject.type != 8 || this.currentMessageObject.audioProgress == TouchHelperCallback.ALPHA_FULL) {
                    updateButtonState(true);
                } else {
                    this.photoNotSet = false;
                    this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
                    didPressedButton(true);
                }
            }
            if (this.photoNotSet) {
                setMessageObject(this.currentMessageObject);
            }
        } else if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_GIF && this.currentMessageObject.audioProgress != TouchHelperCallback.ALPHA_FULL) {
            this.buttonState = DOCUMENT_ATTACH_TYPE_GIF;
            didPressedButton(true);
        } else if (this.photoNotSet) {
            setMessageObject(this.currentMessageObject);
        } else {
            updateButtonState(true);
        }
    }

    public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
        if (this.currentMessageObject != null && set && !thumb && !this.currentMessageObject.mediaExists && !this.currentMessageObject.attachPathExists) {
            this.currentMessageObject.mediaExists = true;
            updateButtonState(true);
        }
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, true);
        if (this.documentAttachType == DOCUMENT_ATTACH_TYPE_AUDIO || this.documentAttachType == DOCUMENT_ATTACH_TYPE_MUSIC) {
            if (this.buttonState != DOCUMENT_ATTACH_TYPE_VIDEO) {
                updateButtonState(false);
            }
        } else if (this.buttonState != DOCUMENT_ATTACH_TYPE_DOCUMENT) {
            updateButtonState(false);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        this.radialProgress.setProgress(progress, true);
    }

    public void onProvideStructure(ViewStructure structure) {
        super.onProvideStructure(structure);
        if (this.allowAssistant && VERSION.SDK_INT >= 23) {
            if (this.currentMessageObject.messageText != null && this.currentMessageObject.messageText.length() > 0) {
                structure.setText(this.currentMessageObject.messageText);
            } else if (this.currentMessageObject.caption != null && this.currentMessageObject.caption.length() > 0) {
                structure.setText(this.currentMessageObject.caption);
            }
        }
    }

    public void setDelegate(ChatMessageCellDelegate chatMessageCellDelegate) {
        this.delegate = chatMessageCellDelegate;
    }

    public void setAllowAssistant(boolean value) {
        this.allowAssistant = value;
    }

    private void measureTime(MessageObject messageObject) {
        String timeString;
        boolean hasSign = !messageObject.isOutOwner() && messageObject.messageOwner.from_id > 0 && messageObject.messageOwner.post;
        User signUser = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
        if (hasSign && signUser == null) {
            hasSign = false;
        }
        User author = null;
        if (this.currentMessageObject.isFromUser()) {
            author = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
        }
        if (messageObject.messageOwner.via_bot_id == 0 && messageObject.messageOwner.via_bot_name == null && ((author == null || !author.bot) && (messageObject.messageOwner.flags & TLRPC.MESSAGE_FLAG_EDITED) != 0)) {
            timeString = LocaleController.getString("EditedMessage", C0691R.string.EditedMessage) + " " + LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000);
        } else {
            timeString = LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000);
        }
        if (hasSign) {
            this.currentTimeString = ", " + timeString;
        } else {
            this.currentTimeString = timeString;
        }
        int ceil = (int) Math.ceil((double) timePaint.measureText(this.currentTimeString));
        this.timeWidth = ceil;
        this.timeTextWidth = ceil;
        if ((messageObject.messageOwner.flags & MessagesController.UPDATE_MASK_PHONE) != 0) {
            Object[] objArr = new Object[DOCUMENT_ATTACH_TYPE_DOCUMENT];
            objArr[DOCUMENT_ATTACH_TYPE_NONE] = LocaleController.formatShortNumber(Math.max(DOCUMENT_ATTACH_TYPE_DOCUMENT, messageObject.messageOwner.views), null);
            this.currentViewsString = String.format("%s", objArr);
            this.viewsTextWidth = (int) Math.ceil((double) timePaint.measureText(this.currentViewsString));
            this.timeWidth += (this.viewsTextWidth + Theme.viewsCountDrawable[DOCUMENT_ATTACH_TYPE_NONE].getIntrinsicWidth()) + AndroidUtilities.dp(10.0f);
        }
        if (hasSign) {
            if (this.availableTimeWidth == 0) {
                this.availableTimeWidth = AndroidUtilities.dp(1000.0f);
            }
            CharSequence name = ContactsController.formatName(signUser.first_name, signUser.last_name).replace('\n', ' ');
            int widthForSign = this.availableTimeWidth - this.timeWidth;
            int width = (int) Math.ceil((double) timePaint.measureText(name, DOCUMENT_ATTACH_TYPE_NONE, name.length()));
            if (width > widthForSign) {
                name = TextUtils.ellipsize(name, timePaint, (float) widthForSign, TruncateAt.END);
                width = widthForSign;
            }
            this.currentTimeString = name + this.currentTimeString;
            this.timeTextWidth += width;
            this.timeWidth += width;
        }
    }

    private boolean isDrawSelectedBackground() {
        return (isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted);
    }

    private boolean checkNeedDrawShareButton(MessageObject messageObject) {
        if (messageObject.type == 13) {
            return false;
        }
        if (messageObject.messageOwner.fwd_from != null && messageObject.messageOwner.fwd_from.channel_id != 0 && !messageObject.isOut()) {
            return true;
        }
        if (messageObject.isFromUser()) {
            if ((messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || messageObject.messageOwner.media == null || ((messageObject.messageOwner.media instanceof TL_messageMediaWebPage) && !(messageObject.messageOwner.media.webpage instanceof TL_webPage))) {
                return false;
            }
            User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.from_id));
            if (user != null && user.bot) {
                return true;
            }
            if (messageObject.isMegagroup() && !messageObject.isOut()) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(messageObject.messageOwner.to_id.channel_id));
                if (chat == null || chat.username == null || chat.username.length() <= 0 || (messageObject.messageOwner.media instanceof TL_messageMediaContact) || (messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                    return false;
                }
                return true;
            }
        } else if ((messageObject.messageOwner.from_id < 0 || messageObject.messageOwner.post) && messageObject.messageOwner.to_id.channel_id != 0 && ((messageObject.messageOwner.via_bot_id == 0 && messageObject.messageOwner.reply_to_msg_id == 0) || messageObject.type != 13)) {
            return true;
        }
        return false;
    }

    private void setMessageObjectInternal(MessageObject messageObject) {
        Object[] objArr;
        String name;
        if ((messageObject.messageOwner.flags & MessagesController.UPDATE_MASK_PHONE) != 0) {
            if (this.currentMessageObject.isContentUnread() && !this.currentMessageObject.isOut()) {
                MessagesController.getInstance().addToViewsQueue(this.currentMessageObject.messageOwner, false);
                this.currentMessageObject.setContentIsRead();
            } else if (!this.currentMessageObject.viewsReloaded) {
                MessagesController.getInstance().addToViewsQueue(this.currentMessageObject.messageOwner, true);
                this.currentMessageObject.viewsReloaded = true;
            }
        }
        if (this.currentMessageObject.isFromUser()) {
            this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
        } else if (this.currentMessageObject.messageOwner.from_id < 0) {
            this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-this.currentMessageObject.messageOwner.from_id));
        } else if (this.currentMessageObject.messageOwner.post) {
            this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
        }
        if (this.isChat && !messageObject.isOutOwner() && messageObject.isFromUser()) {
            this.isAvatarVisible = true;
            if (this.currentUser != null) {
                if (this.currentUser.photo != null) {
                    this.currentPhoto = this.currentUser.photo.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(this.currentUser);
            } else if (this.currentChat != null) {
                if (this.currentChat.photo != null) {
                    this.currentPhoto = this.currentChat.photo.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(this.currentChat);
            } else {
                this.currentPhoto = null;
                this.avatarDrawable.setInfo(messageObject.messageOwner.from_id, null, null, false);
            }
            this.avatarImage.setImage(this.currentPhoto, "50_50", this.avatarDrawable, null, false);
        }
        measureTime(messageObject);
        this.namesOffset = DOCUMENT_ATTACH_TYPE_NONE;
        String viaUsername = null;
        CharSequence viaString = null;
        if (messageObject.messageOwner.via_bot_id != 0) {
            User botUser = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.via_bot_id));
            if (!(botUser == null || botUser.username == null || botUser.username.length() <= 0)) {
                viaUsername = "@" + botUser.username;
                objArr = new Object[DOCUMENT_ATTACH_TYPE_DOCUMENT];
                objArr[DOCUMENT_ATTACH_TYPE_NONE] = viaUsername;
                viaString = AndroidUtilities.replaceTags(String.format(" via <b>%s</b>", objArr));
                this.viaWidth = (int) Math.ceil((double) replyNamePaint.measureText(viaString, DOCUMENT_ATTACH_TYPE_NONE, viaString.length()));
                this.currentViaBotUser = botUser;
            }
        } else if (messageObject.messageOwner.via_bot_name != null && messageObject.messageOwner.via_bot_name.length() > 0) {
            viaUsername = "@" + messageObject.messageOwner.via_bot_name;
            objArr = new Object[DOCUMENT_ATTACH_TYPE_DOCUMENT];
            objArr[DOCUMENT_ATTACH_TYPE_NONE] = viaUsername;
            viaString = AndroidUtilities.replaceTags(String.format(" via <b>%s</b>", objArr));
            this.viaWidth = (int) Math.ceil((double) replyNamePaint.measureText(viaString, DOCUMENT_ATTACH_TYPE_NONE, viaString.length()));
        }
        boolean authorName = this.drawName && this.isChat && !this.currentMessageObject.isOutOwner();
        boolean viaBot = (messageObject.messageOwner.fwd_from == null || messageObject.type == 14) && viaUsername != null;
        if (authorName || viaBot) {
            this.drawNameLayout = true;
            this.nameWidth = getMaxNameWidth();
            if (this.nameWidth < 0) {
                this.nameWidth = AndroidUtilities.dp(100.0f);
            }
            if (!authorName) {
                this.currentNameString = TtmlNode.ANONYMOUS_REGION_ID;
            } else if (this.currentUser != null) {
                this.currentNameString = UserObject.getUserName(this.currentUser);
            } else if (this.currentChat != null) {
                this.currentNameString = this.currentChat.title;
            } else {
                this.currentNameString = "DELETED";
            }
            CharSequence nameStringFinal = TextUtils.ellipsize(this.currentNameString.replace('\n', ' '), namePaint, (float) (this.nameWidth - (viaBot ? this.viaWidth : DOCUMENT_ATTACH_TYPE_NONE)), TruncateAt.END);
            if (viaBot) {
                int color;
                this.viaNameWidth = (int) Math.ceil((double) namePaint.measureText(nameStringFinal, DOCUMENT_ATTACH_TYPE_NONE, nameStringFinal.length()));
                if (this.viaNameWidth != 0) {
                    this.viaNameWidth += AndroidUtilities.dp(4.0f);
                }
                if (this.currentMessageObject.type == 13) {
                    color = Theme.MSG_STICKER_VIA_BOT_NAME_TEXT_COLOR;
                } else {
                    color = this.currentMessageObject.isOutOwner() ? Theme.MSG_OUT_VIA_BOT_NAME_TEXT_COLOR : Theme.MSG_IN_VIA_BOT_NAME_TEXT_COLOR;
                }
                SpannableStringBuilder spannableStringBuilder;
                if (this.currentNameString.length() > 0) {
                    objArr = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                    objArr[DOCUMENT_ATTACH_TYPE_NONE] = nameStringFinal;
                    objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = viaUsername;
                    spannableStringBuilder = new SpannableStringBuilder(String.format("%s via %s", objArr));
                    spannableStringBuilder.setSpan(new TypefaceSpan(Typeface.DEFAULT, DOCUMENT_ATTACH_TYPE_NONE, color), nameStringFinal.length() + DOCUMENT_ATTACH_TYPE_DOCUMENT, nameStringFinal.length() + DOCUMENT_ATTACH_TYPE_VIDEO, 33);
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), DOCUMENT_ATTACH_TYPE_NONE, color), nameStringFinal.length() + DOCUMENT_ATTACH_TYPE_MUSIC, spannableStringBuilder.length(), 33);
                    nameStringFinal = spannableStringBuilder;
                } else {
                    objArr = new Object[DOCUMENT_ATTACH_TYPE_DOCUMENT];
                    objArr[DOCUMENT_ATTACH_TYPE_NONE] = viaUsername;
                    spannableStringBuilder = new SpannableStringBuilder(String.format("via %s", objArr));
                    spannableStringBuilder.setSpan(new TypefaceSpan(Typeface.DEFAULT, DOCUMENT_ATTACH_TYPE_NONE, color), DOCUMENT_ATTACH_TYPE_NONE, DOCUMENT_ATTACH_TYPE_VIDEO, 33);
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), DOCUMENT_ATTACH_TYPE_NONE, color), DOCUMENT_ATTACH_TYPE_VIDEO, spannableStringBuilder.length(), 33);
                    Object nameStringFinal2 = spannableStringBuilder;
                }
                nameStringFinal = TextUtils.ellipsize(nameStringFinal, namePaint, (float) this.nameWidth, TruncateAt.END);
            }
            try {
                this.nameLayout = new StaticLayout(nameStringFinal, namePaint, this.nameWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                if (this.nameLayout == null || this.nameLayout.getLineCount() <= 0) {
                    this.nameWidth = DOCUMENT_ATTACH_TYPE_NONE;
                    if (this.currentNameString.length() == 0) {
                        this.currentNameString = null;
                    }
                } else {
                    this.nameWidth = (int) Math.ceil((double) this.nameLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE));
                    if (messageObject.type != 13) {
                        this.namesOffset += AndroidUtilities.dp(19.0f);
                    }
                    this.nameOffsetX = this.nameLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                    if (this.currentNameString.length() == 0) {
                        this.currentNameString = null;
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        } else {
            this.currentNameString = null;
            this.nameLayout = null;
            this.nameWidth = DOCUMENT_ATTACH_TYPE_NONE;
        }
        this.currentForwardUser = null;
        this.currentForwardNameString = null;
        this.currentForwardChannel = null;
        this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] = null;
        this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_DOCUMENT] = null;
        this.forwardedNameWidth = DOCUMENT_ATTACH_TYPE_NONE;
        if (this.drawForwardedName && messageObject.isForwarded()) {
            if (messageObject.messageOwner.fwd_from.channel_id != 0) {
                this.currentForwardChannel = MessagesController.getInstance().getChat(Integer.valueOf(messageObject.messageOwner.fwd_from.channel_id));
            }
            if (messageObject.messageOwner.fwd_from.from_id != 0) {
                this.currentForwardUser = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.fwd_from.from_id));
            }
            if (!(this.currentForwardUser == null && this.currentForwardChannel == null)) {
                CharSequence lastLine;
                if (this.currentForwardChannel != null) {
                    if (this.currentForwardUser != null) {
                        objArr = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                        objArr[DOCUMENT_ATTACH_TYPE_NONE] = this.currentForwardChannel.title;
                        objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = UserObject.getUserName(this.currentForwardUser);
                        this.currentForwardNameString = String.format("%s (%s)", objArr);
                    } else {
                        this.currentForwardNameString = this.currentForwardChannel.title;
                    }
                } else if (this.currentForwardUser != null) {
                    this.currentForwardNameString = UserObject.getUserName(this.currentForwardUser);
                }
                this.forwardedNameWidth = getMaxNameWidth();
                name = TextUtils.ellipsize(this.currentForwardNameString.replace('\n', ' '), replyNamePaint, (float) ((this.forwardedNameWidth - ((int) Math.ceil((double) forwardNamePaint.measureText(LocaleController.getString("From", C0691R.string.From) + " ")))) - this.viaWidth), TruncateAt.END);
                if (viaString != null) {
                    this.viaNameWidth = (int) Math.ceil((double) forwardNamePaint.measureText(LocaleController.getString("From", C0691R.string.From) + " " + name));
                    objArr = new Object[DOCUMENT_ATTACH_TYPE_AUDIO];
                    objArr[DOCUMENT_ATTACH_TYPE_NONE] = LocaleController.getString("From", C0691R.string.From);
                    objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = name;
                    objArr[DOCUMENT_ATTACH_TYPE_GIF] = viaUsername;
                    lastLine = AndroidUtilities.replaceTags(String.format("%s <b>%s</b> via <b>%s</b>", objArr));
                } else {
                    objArr = new Object[DOCUMENT_ATTACH_TYPE_GIF];
                    objArr[DOCUMENT_ATTACH_TYPE_NONE] = LocaleController.getString("From", C0691R.string.From);
                    objArr[DOCUMENT_ATTACH_TYPE_DOCUMENT] = name;
                    lastLine = AndroidUtilities.replaceTags(String.format("%s <b>%s</b>", objArr));
                }
                try {
                    this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_DOCUMENT] = new StaticLayout(TextUtils.ellipsize(lastLine, forwardNamePaint, (float) this.forwardedNameWidth, TruncateAt.END), forwardNamePaint, this.forwardedNameWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                    this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] = new StaticLayout(TextUtils.ellipsize(AndroidUtilities.replaceTags(LocaleController.getString("ForwardedMessage", C0691R.string.ForwardedMessage)), forwardNamePaint, (float) this.forwardedNameWidth, TruncateAt.END), forwardNamePaint, this.forwardedNameWidth + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                    this.forwardedNameWidth = Math.max((int) Math.ceil((double) this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE].getLineWidth(DOCUMENT_ATTACH_TYPE_NONE)), (int) Math.ceil((double) this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_DOCUMENT].getLineWidth(DOCUMENT_ATTACH_TYPE_NONE)));
                    this.forwardNameOffsetX[DOCUMENT_ATTACH_TYPE_NONE] = this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE].getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                    this.forwardNameOffsetX[DOCUMENT_ATTACH_TYPE_DOCUMENT] = this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_DOCUMENT].getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                    this.namesOffset += AndroidUtilities.dp(36.0f);
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
            }
        }
        if (messageObject.isReply()) {
            CharSequence stringFinalName;
            this.namesOffset += AndroidUtilities.dp(42.0f);
            if (messageObject.type != 0) {
                if (messageObject.type == 13) {
                    this.namesOffset -= AndroidUtilities.dp(42.0f);
                } else {
                    this.namesOffset += AndroidUtilities.dp(5.0f);
                }
            }
            int maxWidth = getMaxNameWidth();
            if (messageObject.type != 13) {
                maxWidth -= AndroidUtilities.dp(10.0f);
            }
            CharSequence stringFinalText = null;
            if (messageObject.replyMessageObject != null) {
                PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.replyMessageObject.photoThumbs, 80);
                if (photoSize == null || messageObject.replyMessageObject.type == 13 || ((messageObject.type == 13 && !AndroidUtilities.isTablet()) || messageObject.replyMessageObject.isSecretMedia())) {
                    this.replyImageReceiver.setImageBitmap((Drawable) null);
                    this.needReplyImage = false;
                } else {
                    this.currentReplyPhoto = photoSize.location;
                    this.replyImageReceiver.setImage(photoSize.location, "50_50", null, null, true);
                    this.needReplyImage = true;
                    maxWidth -= AndroidUtilities.dp(44.0f);
                }
                name = null;
                if (messageObject.replyMessageObject.isFromUser()) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(messageObject.replyMessageObject.messageOwner.from_id));
                    if (user != null) {
                        name = UserObject.getUserName(user);
                    }
                } else if (messageObject.replyMessageObject.messageOwner.from_id < 0) {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(-messageObject.replyMessageObject.messageOwner.from_id));
                    if (chat != null) {
                        name = chat.title;
                    }
                } else {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(messageObject.replyMessageObject.messageOwner.to_id.channel_id));
                    if (chat != null) {
                        name = chat.title;
                    }
                }
                if (name != null) {
                    stringFinalName = TextUtils.ellipsize(name.replace('\n', ' '), replyNamePaint, (float) maxWidth, TruncateAt.END);
                } else {
                    stringFinalName = null;
                }
                if (messageObject.replyMessageObject.messageText != null && messageObject.replyMessageObject.messageText.length() > 0) {
                    String mess = messageObject.replyMessageObject.messageText.toString();
                    if (mess.length() > 150) {
                        mess = mess.substring(DOCUMENT_ATTACH_TYPE_NONE, 150);
                    }
                    stringFinalText = TextUtils.ellipsize(Emoji.replaceEmoji(mess.replace('\n', ' '), replyTextPaint.getFontMetricsInt(), AndroidUtilities.dp(14.0f), false), replyTextPaint, (float) maxWidth, TruncateAt.END);
                }
            } else {
                stringFinalName = null;
            }
            if (stringFinalName == null) {
                stringFinalName = LocaleController.getString("Loading", C0691R.string.Loading);
            }
            try {
                this.replyNameLayout = new StaticLayout(stringFinalName, replyNamePaint, maxWidth + AndroidUtilities.dp(6.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                if (this.replyNameLayout.getLineCount() > 0) {
                    this.replyNameWidth = AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : DOCUMENT_ATTACH_TYPE_NONE) + 12)) + ((int) Math.ceil((double) this.replyNameLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE)));
                    this.replyNameOffset = this.replyNameLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                }
            } catch (Throwable e22) {
                FileLog.m13e("tmessages", e22);
            }
            if (stringFinalText != null) {
                try {
                    this.replyTextLayout = new StaticLayout(stringFinalText, replyTextPaint, maxWidth + AndroidUtilities.dp(6.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                    if (this.replyTextLayout.getLineCount() > 0) {
                        this.replyTextWidth = AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : DOCUMENT_ATTACH_TYPE_NONE) + 12)) + ((int) Math.ceil((double) this.replyTextLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE)));
                        this.replyTextOffset = this.replyTextLayout.getLineLeft(DOCUMENT_ATTACH_TYPE_NONE);
                    }
                } catch (Throwable e222) {
                    FileLog.m13e("tmessages", e222);
                }
            }
        }
        requestLayout();
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentMessageObject != null) {
            if (this.wasLayout) {
                Drawable drawable;
                int i;
                int dp;
                int dp2;
                if (this.isAvatarVisible) {
                    this.avatarImage.draw(canvas);
                }
                if (this.mediaBackground) {
                    timePaint.setColor(Theme.MSG_MEDIA_TIME_TEXT_COLOR);
                } else if (this.currentMessageObject.isOutOwner()) {
                    timePaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_TIME_SELECTED_TEXT_COLOR : Theme.MSG_OUT_TIME_TEXT_COLOR);
                } else {
                    timePaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_TIME_SELECTED_TEXT_COLOR : Theme.MSG_IN_TIME_TEXT_COLOR);
                }
                int i2;
                if (this.currentMessageObject.isOutOwner()) {
                    if (isDrawSelectedBackground()) {
                        if (this.mediaBackground) {
                            this.currentBackgroundDrawable = Theme.backgroundMediaDrawableOutSelected;
                        } else {
                            this.currentBackgroundDrawable = Theme.backgroundDrawableOutSelected;
                        }
                    } else if (this.mediaBackground) {
                        this.currentBackgroundDrawable = Theme.backgroundMediaDrawableOut;
                    } else {
                        this.currentBackgroundDrawable = Theme.backgroundDrawableOut;
                    }
                    drawable = this.currentBackgroundDrawable;
                    i = this.layoutWidth - this.backgroundWidth;
                    if (this.mediaBackground) {
                        dp = AndroidUtilities.dp(9.0f);
                    } else {
                        dp = DOCUMENT_ATTACH_TYPE_NONE;
                    }
                    i -= dp;
                    dp2 = AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                    i2 = this.backgroundWidth;
                    if (this.mediaBackground) {
                        dp = DOCUMENT_ATTACH_TYPE_NONE;
                    } else {
                        dp = AndroidUtilities.dp(3.0f);
                    }
                    setDrawableBounds(drawable, i, dp2, i2 - dp, this.layoutHeight - AndroidUtilities.dp(2.0f));
                } else {
                    if (isDrawSelectedBackground()) {
                        if (this.mediaBackground) {
                            this.currentBackgroundDrawable = Theme.backgroundMediaDrawableInSelected;
                        } else {
                            this.currentBackgroundDrawable = Theme.backgroundDrawableInSelected;
                        }
                    } else if (this.mediaBackground) {
                        this.currentBackgroundDrawable = Theme.backgroundMediaDrawableIn;
                    } else {
                        this.currentBackgroundDrawable = Theme.backgroundDrawableIn;
                    }
                    if (this.isChat && this.currentMessageObject.isFromUser()) {
                        drawable = this.currentBackgroundDrawable;
                        if (this.mediaBackground) {
                            dp = 9;
                        } else {
                            dp = DOCUMENT_ATTACH_TYPE_AUDIO;
                        }
                        i = AndroidUtilities.dp((float) (dp + 48));
                        dp2 = AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL);
                        i2 = this.backgroundWidth;
                        if (this.mediaBackground) {
                            dp = DOCUMENT_ATTACH_TYPE_NONE;
                        } else {
                            dp = AndroidUtilities.dp(3.0f);
                        }
                        setDrawableBounds(drawable, i, dp2, i2 - dp, this.layoutHeight - AndroidUtilities.dp(2.0f));
                    } else {
                        setDrawableBounds(this.currentBackgroundDrawable, !this.mediaBackground ? AndroidUtilities.dp(3.0f) : AndroidUtilities.dp(9.0f), AndroidUtilities.dp(TouchHelperCallback.ALPHA_FULL), this.backgroundWidth - (this.mediaBackground ? DOCUMENT_ATTACH_TYPE_NONE : AndroidUtilities.dp(3.0f)), this.layoutHeight - AndroidUtilities.dp(2.0f));
                    }
                }
                if (this.drawBackground && this.currentBackgroundDrawable != null) {
                    this.currentBackgroundDrawable.draw(canvas);
                }
                drawContent(canvas);
                if (this.drawShareButton) {
                    Theme.shareDrawable.setColorFilter(this.sharePressed ? Theme.colorPressedFilter : Theme.colorFilter);
                    if (this.currentMessageObject.isOutOwner()) {
                        this.shareStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0f)) - Theme.shareDrawable.getIntrinsicWidth();
                    } else {
                        this.shareStartX = this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0f);
                    }
                    Drawable drawable2 = Theme.shareDrawable;
                    i = this.shareStartX;
                    dp2 = this.layoutHeight - AndroidUtilities.dp(41.0f);
                    this.shareStartY = dp2;
                    setDrawableBounds(drawable2, i, dp2);
                    Theme.shareDrawable.draw(canvas);
                    setDrawableBounds(Theme.shareIconDrawable, this.shareStartX + AndroidUtilities.dp(9.0f), this.shareStartY + AndroidUtilities.dp(9.0f));
                    Theme.shareIconDrawable.draw(canvas);
                }
                if (this.drawNameLayout && this.nameLayout != null) {
                    canvas.save();
                    if (this.currentMessageObject.type == 13) {
                        namePaint.setColor(Theme.MSG_STICKER_NAME_TEXT_COLOR);
                        if (this.currentMessageObject.isOutOwner()) {
                            this.nameX = (float) AndroidUtilities.dp(28.0f);
                        } else {
                            this.nameX = (float) (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(22.0f));
                        }
                        this.nameY = (float) (this.layoutHeight - AndroidUtilities.dp(38.0f));
                        Theme.systemDrawable.setColorFilter(Theme.colorFilter);
                        Theme.systemDrawable.setBounds(((int) this.nameX) - AndroidUtilities.dp(12.0f), ((int) this.nameY) - AndroidUtilities.dp(5.0f), (((int) this.nameX) + AndroidUtilities.dp(12.0f)) + this.nameWidth, ((int) this.nameY) + AndroidUtilities.dp(22.0f));
                        Theme.systemDrawable.draw(canvas);
                    } else {
                        if (this.mediaBackground || this.currentMessageObject.isOutOwner()) {
                            this.nameX = ((float) (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f))) - this.nameOffsetX;
                        } else {
                            this.nameX = ((float) (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0f))) - this.nameOffsetX;
                        }
                        if (this.currentUser != null) {
                            namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentUser.id));
                        } else if (this.currentChat != null) {
                            namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentChat.id));
                        } else {
                            namePaint.setColor(AvatarDrawable.getNameColorForId(DOCUMENT_ATTACH_TYPE_NONE));
                        }
                        this.nameY = (float) AndroidUtilities.dp(10.0f);
                    }
                    canvas.translate(this.nameX, this.nameY);
                    this.nameLayout.draw(canvas);
                    canvas.restore();
                }
                if (!(!this.drawForwardedName || this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] == null || this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_DOCUMENT] == null)) {
                    this.forwardNameY = AndroidUtilities.dp((float) ((this.drawNameLayout ? 19 : DOCUMENT_ATTACH_TYPE_NONE) + 10));
                    if (this.currentMessageObject.isOutOwner()) {
                        forwardNamePaint.setColor(Theme.MSG_OUT_FORDWARDED_NAME_TEXT_COLOR);
                        this.forwardNameX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
                    } else {
                        forwardNamePaint.setColor(Theme.MSG_IN_FORDWARDED_NAME_TEXT_COLOR);
                        if (this.mediaBackground) {
                            this.forwardNameX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0f);
                        } else {
                            this.forwardNameX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(17.0f);
                        }
                    }
                    for (int a = DOCUMENT_ATTACH_TYPE_NONE; a < DOCUMENT_ATTACH_TYPE_GIF; a += DOCUMENT_ATTACH_TYPE_DOCUMENT) {
                        canvas.save();
                        canvas.translate(((float) this.forwardNameX) - this.forwardNameOffsetX[a], (float) (this.forwardNameY + (AndroidUtilities.dp(16.0f) * a)));
                        this.forwardedNameLayout[a].draw(canvas);
                        canvas.restore();
                    }
                }
                if (this.currentMessageObject.isReply()) {
                    if (this.currentMessageObject.type == 13) {
                        replyLinePaint.setColor(Theme.MSG_STICKER_REPLY_LINE_COLOR);
                        replyNamePaint.setColor(Theme.MSG_STICKER_REPLY_NAME_TEXT_COLOR);
                        replyTextPaint.setColor(Theme.MSG_STICKER_REPLY_MESSAGE_TEXT_COLOR);
                        if (this.currentMessageObject.isOutOwner()) {
                            this.replyStartX = AndroidUtilities.dp(23.0f);
                        } else {
                            this.replyStartX = this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(17.0f);
                        }
                        this.replyStartY = this.layoutHeight - AndroidUtilities.dp(58.0f);
                        if (this.nameLayout != null) {
                            this.replyStartY -= AndroidUtilities.dp(31.0f);
                        }
                        int backWidth = Math.max(this.replyNameWidth, this.replyTextWidth) + AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : DOCUMENT_ATTACH_TYPE_NONE) + 14));
                        Theme.systemDrawable.setColorFilter(Theme.colorFilter);
                        Theme.systemDrawable.setBounds(this.replyStartX - AndroidUtilities.dp(7.0f), this.replyStartY - AndroidUtilities.dp(6.0f), (this.replyStartX - AndroidUtilities.dp(7.0f)) + backWidth, this.replyStartY + AndroidUtilities.dp(41.0f));
                        Theme.systemDrawable.draw(canvas);
                    } else {
                        if (this.currentMessageObject.isOutOwner()) {
                            replyLinePaint.setColor(Theme.MSG_OUT_REPLY_LINE_COLOR);
                            replyNamePaint.setColor(Theme.MSG_OUT_REPLY_NAME_TEXT_COLOR);
                            if (this.currentMessageObject.replyMessageObject == null || this.currentMessageObject.replyMessageObject.type != 0) {
                                replyTextPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_OUT_REPLY_MEDIA_MESSAGE_SELETED_TEXT_COLOR : Theme.MSG_OUT_REPLY_MEDIA_MESSAGE_TEXT_COLOR);
                            } else {
                                replyTextPaint.setColor(Theme.MSG_OUT_REPLY_MESSAGE_TEXT_COLOR);
                            }
                            this.replyStartX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12.0f);
                        } else {
                            replyLinePaint.setColor(Theme.MSG_IN_REPLY_LINE_COLOR);
                            replyNamePaint.setColor(Theme.MSG_IN_REPLY_NAME_TEXT_COLOR);
                            if (this.currentMessageObject.replyMessageObject == null || this.currentMessageObject.replyMessageObject.type != 0) {
                                replyTextPaint.setColor(isDrawSelectedBackground() ? Theme.MSG_IN_REPLY_MEDIA_MESSAGE_SELETED_TEXT_COLOR : Theme.MSG_IN_REPLY_MEDIA_MESSAGE_TEXT_COLOR);
                            } else {
                                replyTextPaint.setColor(Theme.MSG_IN_REPLY_MESSAGE_TEXT_COLOR);
                            }
                            if (this.mediaBackground) {
                                this.replyStartX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12.0f);
                            } else {
                                this.replyStartX = this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(18.0f);
                            }
                        }
                        if (!this.drawForwardedName || this.forwardedNameLayout[DOCUMENT_ATTACH_TYPE_NONE] == null) {
                            dp = DOCUMENT_ATTACH_TYPE_NONE;
                        } else {
                            dp = 36;
                        }
                        i = dp + 12;
                        if (!this.drawNameLayout || this.nameLayout == null) {
                            dp = DOCUMENT_ATTACH_TYPE_NONE;
                        } else {
                            dp = 20;
                        }
                        this.replyStartY = AndroidUtilities.dp((float) (dp + i));
                    }
                    canvas.drawRect((float) this.replyStartX, (float) this.replyStartY, (float) (this.replyStartX + AndroidUtilities.dp(2.0f)), (float) (this.replyStartY + AndroidUtilities.dp(35.0f)), replyLinePaint);
                    if (this.needReplyImage) {
                        this.replyImageReceiver.setImageCoords(this.replyStartX + AndroidUtilities.dp(10.0f), this.replyStartY, AndroidUtilities.dp(35.0f), AndroidUtilities.dp(35.0f));
                        this.replyImageReceiver.draw(canvas);
                    }
                    if (this.replyNameLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : DOCUMENT_ATTACH_TYPE_NONE) + 10))) + (((float) this.replyStartX) - this.replyNameOffset), (float) this.replyStartY);
                        this.replyNameLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (this.replyTextLayout != null) {
                        canvas.save();
                        canvas.translate(((float) AndroidUtilities.dp((float) ((this.needReplyImage ? 44 : DOCUMENT_ATTACH_TYPE_NONE) + 10))) + (((float) this.replyStartX) - this.replyTextOffset), (float) (this.replyStartY + AndroidUtilities.dp(19.0f)));
                        this.replyTextLayout.draw(canvas);
                        canvas.restore();
                    }
                }
                if (this.drawTime || !this.mediaBackground) {
                    int additionalX;
                    if (this.mediaBackground) {
                        if (this.currentMessageObject.type == 13) {
                            drawable = Theme.timeStickerBackgroundDrawable;
                        } else {
                            drawable = Theme.timeBackgroundDrawable;
                        }
                        setDrawableBounds(drawable, this.timeX - AndroidUtilities.dp(4.0f), this.layoutHeight - AndroidUtilities.dp(27.0f), this.timeWidth + AndroidUtilities.dp((float) ((this.currentMessageObject.isOutOwner() ? 20 : DOCUMENT_ATTACH_TYPE_NONE) + 8)), AndroidUtilities.dp(17.0f));
                        drawable.draw(canvas);
                        additionalX = DOCUMENT_ATTACH_TYPE_NONE;
                        if ((this.currentMessageObject.messageOwner.flags & MessagesController.UPDATE_MASK_PHONE) != 0) {
                            additionalX = (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE));
                            if (this.currentMessageObject.isSending()) {
                                if (!this.currentMessageObject.isOutOwner()) {
                                    setDrawableBounds(Theme.clockMediaDrawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(13.0f)) - Theme.clockMediaDrawable.getIntrinsicHeight());
                                    Theme.clockMediaDrawable.draw(canvas);
                                }
                            } else if (!this.currentMessageObject.isSendError()) {
                                Drawable countDrawable = Theme.viewsMediaCountDrawable;
                                setDrawableBounds(countDrawable, this.timeX, (this.layoutHeight - AndroidUtilities.dp(9.5f)) - this.timeLayout.getHeight());
                                countDrawable.draw(canvas);
                                if (this.viewsLayout != null) {
                                    canvas.save();
                                    canvas.translate((float) ((this.timeX + countDrawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.dp(11.3f)) - this.timeLayout.getHeight()));
                                    this.viewsLayout.draw(canvas);
                                    canvas.restore();
                                }
                            } else if (!this.currentMessageObject.isOutOwner()) {
                                setDrawableBounds(Theme.errorDrawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.errorDrawable.getIntrinsicHeight());
                                Theme.errorDrawable.draw(canvas);
                            }
                        }
                        canvas.save();
                        canvas.translate((float) (this.timeX + additionalX), (float) ((this.layoutHeight - AndroidUtilities.dp(11.3f)) - this.timeLayout.getHeight()));
                        this.timeLayout.draw(canvas);
                        canvas.restore();
                    } else {
                        additionalX = DOCUMENT_ATTACH_TYPE_NONE;
                        if ((this.currentMessageObject.messageOwner.flags & MessagesController.UPDATE_MASK_PHONE) != 0) {
                            additionalX = (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(DOCUMENT_ATTACH_TYPE_NONE));
                            if (this.currentMessageObject.isSending()) {
                                if (!this.currentMessageObject.isOutOwner()) {
                                    Drawable clockDrawable = Theme.clockChannelDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE];
                                    setDrawableBounds(clockDrawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - clockDrawable.getIntrinsicHeight());
                                    clockDrawable.draw(canvas);
                                }
                            } else if (!this.currentMessageObject.isSendError()) {
                                if (this.currentMessageObject.isOutOwner()) {
                                    setDrawableBounds(Theme.viewsOutCountDrawable, this.timeX, (this.layoutHeight - AndroidUtilities.dp(4.5f)) - this.timeLayout.getHeight());
                                    Theme.viewsOutCountDrawable.draw(canvas);
                                } else {
                                    setDrawableBounds(Theme.viewsCountDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE], this.timeX, (this.layoutHeight - AndroidUtilities.dp(4.5f)) - this.timeLayout.getHeight());
                                    Theme.viewsCountDrawable[isDrawSelectedBackground() ? DOCUMENT_ATTACH_TYPE_DOCUMENT : DOCUMENT_ATTACH_TYPE_NONE].draw(canvas);
                                }
                                if (this.viewsLayout != null) {
                                    canvas.save();
                                    canvas.translate((float) ((this.timeX + Theme.viewsOutCountDrawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.dp(6.5f)) - this.timeLayout.getHeight()));
                                    this.viewsLayout.draw(canvas);
                                    canvas.restore();
                                }
                            } else if (!this.currentMessageObject.isOutOwner()) {
                                setDrawableBounds(Theme.errorDrawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(6.5f)) - Theme.errorDrawable.getIntrinsicHeight());
                                Theme.errorDrawable.draw(canvas);
                            }
                        }
                        canvas.save();
                        canvas.translate((float) (this.timeX + additionalX), (float) ((this.layoutHeight - AndroidUtilities.dp(6.5f)) - this.timeLayout.getHeight()));
                        this.timeLayout.draw(canvas);
                        canvas.restore();
                    }
                    if (this.currentMessageObject.isOutOwner()) {
                        boolean drawCheck1 = false;
                        boolean drawCheck2 = false;
                        boolean drawClock = false;
                        boolean drawError = false;
                        boolean isBroadcast = ((int) (this.currentMessageObject.getDialogId() >> 32)) == DOCUMENT_ATTACH_TYPE_DOCUMENT;
                        if (this.currentMessageObject.isSending()) {
                            drawCheck1 = false;
                            drawCheck2 = false;
                            drawClock = true;
                            drawError = false;
                        } else if (this.currentMessageObject.isSendError()) {
                            drawCheck1 = false;
                            drawCheck2 = false;
                            drawClock = false;
                            drawError = true;
                        } else if (this.currentMessageObject.isSent()) {
                            if (this.currentMessageObject.isUnread()) {
                                drawCheck1 = false;
                                drawCheck2 = true;
                            } else {
                                drawCheck1 = true;
                                drawCheck2 = true;
                            }
                            drawClock = false;
                            drawError = false;
                        }
                        if (drawClock) {
                            if (this.mediaBackground) {
                                setDrawableBounds(Theme.clockMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(22.0f)) - Theme.clockMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.clockMediaDrawable.getIntrinsicHeight());
                                Theme.clockMediaDrawable.draw(canvas);
                            } else {
                                setDrawableBounds(Theme.clockDrawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - Theme.clockDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - Theme.clockDrawable.getIntrinsicHeight());
                                Theme.clockDrawable.draw(canvas);
                            }
                        }
                        if (!isBroadcast) {
                            if (drawCheck2) {
                                if (this.mediaBackground) {
                                    if (drawCheck1) {
                                        setDrawableBounds(Theme.checkMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.checkMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.checkMediaDrawable.getIntrinsicHeight());
                                    } else {
                                        setDrawableBounds(Theme.checkMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.checkMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.checkMediaDrawable.getIntrinsicHeight());
                                    }
                                    Theme.checkMediaDrawable.draw(canvas);
                                } else {
                                    if (drawCheck1) {
                                        setDrawableBounds(Theme.checkDrawable, (this.layoutWidth - AndroidUtilities.dp(22.5f)) - Theme.checkDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.checkDrawable.getIntrinsicHeight());
                                    } else {
                                        setDrawableBounds(Theme.checkDrawable, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - Theme.checkDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.checkDrawable.getIntrinsicHeight());
                                    }
                                    Theme.checkDrawable.draw(canvas);
                                }
                            }
                            if (drawCheck1) {
                                if (this.mediaBackground) {
                                    setDrawableBounds(Theme.halfCheckMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.halfCheckMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(12.5f)) - Theme.halfCheckMediaDrawable.getIntrinsicHeight());
                                    Theme.halfCheckMediaDrawable.draw(canvas);
                                } else {
                                    setDrawableBounds(Theme.halfCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(18.0f)) - Theme.halfCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.halfCheckDrawable.getIntrinsicHeight());
                                    Theme.halfCheckDrawable.draw(canvas);
                                }
                            }
                        } else if (drawCheck1 || drawCheck2) {
                            if (this.mediaBackground) {
                                setDrawableBounds(Theme.broadcastMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(24.0f)) - Theme.broadcastMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.0f)) - Theme.broadcastMediaDrawable.getIntrinsicHeight());
                                Theme.broadcastMediaDrawable.draw(canvas);
                            } else {
                                setDrawableBounds(Theme.broadcastDrawable, (this.layoutWidth - AndroidUtilities.dp(20.5f)) - Theme.broadcastDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.broadcastDrawable.getIntrinsicHeight());
                                Theme.broadcastDrawable.draw(canvas);
                            }
                        }
                        if (!drawError) {
                            return;
                        }
                        if (this.mediaBackground) {
                            setDrawableBounds(Theme.errorDrawable, (this.layoutWidth - AndroidUtilities.dp(20.5f)) - Theme.errorDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(11.5f)) - Theme.errorDrawable.getIntrinsicHeight());
                            Theme.errorDrawable.draw(canvas);
                            return;
                        }
                        setDrawableBounds(Theme.errorDrawable, (this.layoutWidth - AndroidUtilities.dp(18.0f)) - Theme.errorDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(7.0f)) - Theme.errorDrawable.getIntrinsicHeight());
                        Theme.errorDrawable.draw(canvas);
                        return;
                    }
                    return;
                }
                return;
            }
            requestLayout();
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }
}
