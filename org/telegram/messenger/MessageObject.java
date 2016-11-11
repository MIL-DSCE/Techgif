package org.telegram.messenger;

import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
import org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionTTLChange;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityBotCommand;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC.TL_messageEntityHashtag;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityMention;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageForwarded_old;
import org.telegram.tgnet.TLRPC.TL_messageForwarded_old2;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_old;
import org.telegram.tgnet.TLRPC.TL_message_old2;
import org.telegram.tgnet.TLRPC.TL_message_old3;
import org.telegram.tgnet.TLRPC.TL_message_old4;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class MessageObject {
    private static final int LINES_PER_BLOCK = 10;
    public static final int MESSAGE_SEND_STATE_SENDING = 1;
    public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
    public static final int MESSAGE_SEND_STATE_SENT = 0;
    private static TextPaint botButtonPaint;
    private static TextPaint textPaint;
    public static Pattern urlPattern;
    public boolean attachPathExists;
    public float audioProgress;
    public int audioProgressSec;
    public CharSequence caption;
    public int contentType;
    public String dateKey;
    public boolean deleted;
    public boolean forceUpdate;
    public int lastLineWidth;
    private boolean layoutCreated;
    public CharSequence linkDescription;
    public boolean mediaExists;
    public Message messageOwner;
    public CharSequence messageText;
    public String monthKey;
    public ArrayList<PhotoSize> photoThumbs;
    public MessageObject replyMessageObject;
    public int textHeight;
    public ArrayList<TextLayoutBlock> textLayoutBlocks;
    public int textWidth;
    public int type;
    public VideoEditedInfo videoEditedInfo;
    public boolean viewsReloaded;
    public int wantedBotKeyboardWidth;

    public static class TextLayoutBlock {
        public int charactersOffset;
        public int height;
        public StaticLayout textLayout;
        public float textXOffset;
        public float textYOffset;
    }

    public MessageObject(Message message, AbstractMap<Integer, User> users, boolean generateLayout) {
        this(message, users, null, generateLayout);
    }

    public MessageObject(Message message, AbstractMap<Integer, User> users, AbstractMap<Integer, Chat> chats, boolean generateLayout) {
        Object[] objArr;
        this.type = 1000;
        if (textPaint == null) {
            textPaint = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            textPaint.setColor(Theme.MSG_TEXT_COLOR);
            textPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
        }
        textPaint.setTextSize((float) AndroidUtilities.dp((float) MessagesController.getInstance().fontSize));
        this.messageOwner = message;
        if (message.replyMessage != null) {
            this.replyMessageObject = new MessageObject(message.replyMessage, users, chats, false);
        }
        User fromUser = null;
        if (message.from_id > 0) {
            if (users != null) {
                fromUser = (User) users.get(Integer.valueOf(message.from_id));
            }
            if (fromUser == null) {
                fromUser = MessagesController.getInstance().getUser(Integer.valueOf(message.from_id));
            }
        }
        if (message instanceof TL_messageService) {
            if (message.action != null) {
                if (!(message.action instanceof TL_messageActionChatCreate)) {
                    TLObject whoUser;
                    if (message.action instanceof TL_messageActionChatDeleteUser) {
                        if (message.action.user_id != message.from_id) {
                            whoUser = null;
                            if (users != null) {
                                whoUser = (User) users.get(Integer.valueOf(message.action.user_id));
                            }
                            if (whoUser == null) {
                                whoUser = MessagesController.getInstance().getUser(Integer.valueOf(message.action.user_id));
                            }
                            if (isOut()) {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", C0691R.string.ActionYouKickUser), "un2", whoUser);
                            } else {
                                if (message.action.user_id == UserConfig.getClientUserId()) {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", C0691R.string.ActionKickUserYou), "un1", fromUser);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", C0691R.string.ActionKickUser), "un2", whoUser);
                                    this.messageText = replaceWithLink(this.messageText, "un1", fromUser);
                                }
                            }
                        } else if (isOut()) {
                            this.messageText = LocaleController.getString("ActionYouLeftUser", C0691R.string.ActionYouLeftUser);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", C0691R.string.ActionLeftUser), "un1", fromUser);
                        }
                    } else {
                        if (message.action instanceof TL_messageActionChatAddUser) {
                            int singleUserId = this.messageOwner.action.user_id;
                            if (singleUserId == 0) {
                                if (this.messageOwner.action.users.size() == MESSAGE_SEND_STATE_SENDING) {
                                    singleUserId = ((Integer) this.messageOwner.action.users.get(0)).intValue();
                                }
                            }
                            if (singleUserId != 0) {
                                whoUser = null;
                                if (users != null) {
                                    whoUser = (User) users.get(Integer.valueOf(singleUserId));
                                }
                                if (whoUser == null) {
                                    whoUser = MessagesController.getInstance().getUser(Integer.valueOf(singleUserId));
                                }
                                int i = message.from_id;
                                if (singleUserId == r0) {
                                    if (message.to_id.channel_id == 0 || isMegagroup()) {
                                        if (message.to_id.channel_id == 0 || !isMegagroup()) {
                                            if (isOut()) {
                                                this.messageText = LocaleController.getString("ActionAddUserSelfYou", C0691R.string.ActionAddUserSelfYou);
                                            } else {
                                                this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", C0691R.string.ActionAddUserSelf), "un1", fromUser);
                                            }
                                        } else if (singleUserId == UserConfig.getClientUserId()) {
                                            this.messageText = LocaleController.getString("ChannelMegaJoined", C0691R.string.ChannelMegaJoined);
                                        } else {
                                            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", C0691R.string.ActionAddUserSelfMega), "un1", fromUser);
                                        }
                                    } else {
                                        this.messageText = LocaleController.getString("ChannelJoined", C0691R.string.ChannelJoined);
                                    }
                                } else if (isOut()) {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", C0691R.string.ActionYouAddUser), "un2", whoUser);
                                } else if (singleUserId == UserConfig.getClientUserId()) {
                                    if (message.to_id.channel_id == 0) {
                                        this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", C0691R.string.ActionAddUserYou), "un1", fromUser);
                                    } else if (isMegagroup()) {
                                        this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", C0691R.string.MegaAddedBy), "un1", fromUser);
                                    } else {
                                        this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", C0691R.string.ChannelAddedBy), "un1", fromUser);
                                    }
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", C0691R.string.ActionAddUser), "un2", whoUser);
                                    this.messageText = replaceWithLink(this.messageText, "un1", fromUser);
                                }
                            } else if (isOut()) {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", C0691R.string.ActionYouAddUser), "un2", message.action.users, users);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", C0691R.string.ActionAddUser), "un2", message.action.users, users);
                                this.messageText = replaceWithLink(this.messageText, "un1", fromUser);
                            }
                        } else {
                            if (!(message.action instanceof TL_messageActionChatJoinedByLink)) {
                                if (message.action instanceof TL_messageActionChatEditPhoto) {
                                    if (message.to_id.channel_id != 0 && !isMegagroup()) {
                                        this.messageText = LocaleController.getString("ActionChannelChangedPhoto", C0691R.string.ActionChannelChangedPhoto);
                                    } else if (isOut()) {
                                        this.messageText = LocaleController.getString("ActionYouChangedPhoto", C0691R.string.ActionYouChangedPhoto);
                                    } else {
                                        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", C0691R.string.ActionChangedPhoto), "un1", fromUser);
                                    }
                                } else {
                                    if (message.action instanceof TL_messageActionChatEditTitle) {
                                        if (message.to_id.channel_id != 0 && !isMegagroup()) {
                                            this.messageText = LocaleController.getString("ActionChannelChangedTitle", C0691R.string.ActionChannelChangedTitle).replace("un2", message.action.title);
                                        } else if (isOut()) {
                                            this.messageText = LocaleController.getString("ActionYouChangedTitle", C0691R.string.ActionYouChangedTitle).replace("un2", message.action.title);
                                        } else {
                                            this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", C0691R.string.ActionChangedTitle).replace("un2", message.action.title), "un1", fromUser);
                                        }
                                    } else {
                                        if (message.action instanceof TL_messageActionChatDeletePhoto) {
                                            if (message.to_id.channel_id != 0 && !isMegagroup()) {
                                                this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", C0691R.string.ActionChannelRemovedPhoto);
                                            } else if (isOut()) {
                                                this.messageText = LocaleController.getString("ActionYouRemovedPhoto", C0691R.string.ActionYouRemovedPhoto);
                                            } else {
                                                this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", C0691R.string.ActionRemovedPhoto), "un1", fromUser);
                                            }
                                        } else {
                                            Object[] objArr2;
                                            if (message.action instanceof TL_messageActionTTLChange) {
                                                if (message.action.ttl != 0) {
                                                    if (isOut()) {
                                                        objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                                                        objArr2[0] = AndroidUtilities.formatTTLString(message.action.ttl);
                                                        this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", C0691R.string.MessageLifetimeChangedOutgoing, objArr2);
                                                    } else {
                                                        objArr2 = new Object[MESSAGE_SEND_STATE_SEND_ERROR];
                                                        objArr2[0] = UserObject.getFirstName(fromUser);
                                                        objArr2[MESSAGE_SEND_STATE_SENDING] = AndroidUtilities.formatTTLString(message.action.ttl);
                                                        this.messageText = LocaleController.formatString("MessageLifetimeChanged", C0691R.string.MessageLifetimeChanged, objArr2);
                                                    }
                                                } else if (isOut()) {
                                                    this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", C0691R.string.MessageLifetimeYouRemoved);
                                                } else {
                                                    objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                                                    objArr2[0] = UserObject.getFirstName(fromUser);
                                                    this.messageText = LocaleController.formatString("MessageLifetimeRemoved", C0691R.string.MessageLifetimeRemoved, objArr2);
                                                }
                                            } else {
                                                if (message.action instanceof TL_messageActionLoginUnknownLocation) {
                                                    String date;
                                                    User to_user;
                                                    long time = ((long) message.date) * 1000;
                                                    if (LocaleController.getInstance().formatterDay != null) {
                                                        if (LocaleController.getInstance().formatterYear != null) {
                                                            objArr2 = new Object[MESSAGE_SEND_STATE_SEND_ERROR];
                                                            objArr2[0] = LocaleController.getInstance().formatterYear.format(time);
                                                            objArr2[MESSAGE_SEND_STATE_SENDING] = LocaleController.getInstance().formatterDay.format(time);
                                                            date = LocaleController.formatString("formatDateAtTime", C0691R.string.formatDateAtTime, objArr2);
                                                            to_user = UserConfig.getCurrentUser();
                                                            if (to_user == null) {
                                                                if (users != null) {
                                                                    to_user = (User) users.get(Integer.valueOf(this.messageOwner.to_id.user_id));
                                                                }
                                                                if (to_user == null) {
                                                                    to_user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
                                                                }
                                                            }
                                                            objArr2 = new Object[4];
                                                            objArr2[0] = to_user == null ? UserObject.getFirstName(to_user) : TtmlNode.ANONYMOUS_REGION_ID;
                                                            objArr2[MESSAGE_SEND_STATE_SENDING] = date;
                                                            objArr2[MESSAGE_SEND_STATE_SEND_ERROR] = message.action.title;
                                                            objArr2[3] = message.action.address;
                                                            this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", C0691R.string.NotificationUnrecognizedDevice, objArr2);
                                                        }
                                                    }
                                                    date = TtmlNode.ANONYMOUS_REGION_ID + message.date;
                                                    to_user = UserConfig.getCurrentUser();
                                                    if (to_user == null) {
                                                        if (users != null) {
                                                            to_user = (User) users.get(Integer.valueOf(this.messageOwner.to_id.user_id));
                                                        }
                                                        if (to_user == null) {
                                                            to_user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
                                                        }
                                                    }
                                                    if (to_user == null) {
                                                    }
                                                    objArr2 = new Object[4];
                                                    objArr2[0] = to_user == null ? UserObject.getFirstName(to_user) : TtmlNode.ANONYMOUS_REGION_ID;
                                                    objArr2[MESSAGE_SEND_STATE_SENDING] = date;
                                                    objArr2[MESSAGE_SEND_STATE_SEND_ERROR] = message.action.title;
                                                    objArr2[3] = message.action.address;
                                                    this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", C0691R.string.NotificationUnrecognizedDevice, objArr2);
                                                } else {
                                                    if (message.action instanceof TL_messageActionUserJoined) {
                                                        objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                                                        objArr2[0] = UserObject.getUserName(fromUser);
                                                        this.messageText = LocaleController.formatString("NotificationContactJoined", C0691R.string.NotificationContactJoined, objArr2);
                                                    } else {
                                                        if (message.action instanceof TL_messageActionUserUpdatedPhoto) {
                                                            objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                                                            objArr2[0] = UserObject.getUserName(fromUser);
                                                            this.messageText = LocaleController.formatString("NotificationContactNewPhoto", C0691R.string.NotificationContactNewPhoto, objArr2);
                                                        } else {
                                                            if (message.action instanceof TL_messageEncryptedAction) {
                                                                if (!(message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages)) {
                                                                    if (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                                                                        TL_decryptedMessageActionSetMessageTTL action = message.action.encryptedAction;
                                                                        if (action.ttl_seconds != 0) {
                                                                            if (isOut()) {
                                                                                objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                                                                                objArr2[0] = AndroidUtilities.formatTTLString(action.ttl_seconds);
                                                                                this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", C0691R.string.MessageLifetimeChangedOutgoing, objArr2);
                                                                            } else {
                                                                                objArr2 = new Object[MESSAGE_SEND_STATE_SEND_ERROR];
                                                                                objArr2[0] = UserObject.getFirstName(fromUser);
                                                                                objArr2[MESSAGE_SEND_STATE_SENDING] = AndroidUtilities.formatTTLString(action.ttl_seconds);
                                                                                this.messageText = LocaleController.formatString("MessageLifetimeChanged", C0691R.string.MessageLifetimeChanged, objArr2);
                                                                            }
                                                                        } else if (isOut()) {
                                                                            this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", C0691R.string.MessageLifetimeYouRemoved);
                                                                        } else {
                                                                            objArr2 = new Object[MESSAGE_SEND_STATE_SENDING];
                                                                            objArr2[0] = UserObject.getFirstName(fromUser);
                                                                            this.messageText = LocaleController.formatString("MessageLifetimeRemoved", C0691R.string.MessageLifetimeRemoved, objArr2);
                                                                        }
                                                                    }
                                                                } else if (isOut()) {
                                                                    this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", C0691R.string.ActionTakeScreenshootYou, new Object[0]);
                                                                } else {
                                                                    this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", C0691R.string.ActionTakeScreenshoot), "un1", fromUser);
                                                                }
                                                            } else {
                                                                if (message.action instanceof TL_messageActionCreatedBroadcastList) {
                                                                    this.messageText = LocaleController.formatString("YouCreatedBroadcastList", C0691R.string.YouCreatedBroadcastList, new Object[0]);
                                                                } else {
                                                                    if (!(message.action instanceof TL_messageActionChannelCreate)) {
                                                                        if (message.action instanceof TL_messageActionChatMigrateTo) {
                                                                            this.messageText = LocaleController.getString("ActionMigrateFromGroup", C0691R.string.ActionMigrateFromGroup);
                                                                        } else {
                                                                            if (message.action instanceof TL_messageActionChannelMigrateFrom) {
                                                                                this.messageText = LocaleController.getString("ActionMigrateFromGroup", C0691R.string.ActionMigrateFromGroup);
                                                                            } else {
                                                                                if (message.action instanceof TL_messageActionPinMessage) {
                                                                                    generatePinMessageText(fromUser, fromUser == null ? (Chat) chats.get(Integer.valueOf(message.to_id.channel_id)) : null);
                                                                                } else {
                                                                                    if (message.action instanceof TL_messageActionHistoryClear) {
                                                                                        this.messageText = LocaleController.getString("HistoryCleared", C0691R.string.HistoryCleared);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    } else if (isMegagroup()) {
                                                                        this.messageText = LocaleController.getString("ActionCreateMega", C0691R.string.ActionCreateMega);
                                                                    } else {
                                                                        this.messageText = LocaleController.getString("ActionCreateChannel", C0691R.string.ActionCreateChannel);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (isOut()) {
                                this.messageText = LocaleController.getString("ActionInviteYou", C0691R.string.ActionInviteYou);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", C0691R.string.ActionInviteUser), "un1", fromUser);
                            }
                        }
                    }
                } else if (isOut()) {
                    this.messageText = LocaleController.getString("ActionYouCreateGroup", C0691R.string.ActionYouCreateGroup);
                } else {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", C0691R.string.ActionCreateGroup), "un1", fromUser);
                }
            }
        } else if (isMediaEmpty()) {
            this.messageText = message.message;
        } else {
            if (message.media instanceof TL_messageMediaPhoto) {
                this.messageText = LocaleController.getString("AttachPhoto", C0691R.string.AttachPhoto);
            } else if (isVideo()) {
                this.messageText = LocaleController.getString("AttachVideo", C0691R.string.AttachVideo);
            } else if (isVoice()) {
                this.messageText = LocaleController.getString("AttachAudio", C0691R.string.AttachAudio);
            } else {
                if (!(message.media instanceof TL_messageMediaGeo)) {
                    if (!(message.media instanceof TL_messageMediaVenue)) {
                        if (message.media instanceof TL_messageMediaContact) {
                            this.messageText = LocaleController.getString("AttachContact", C0691R.string.AttachContact);
                        } else {
                            if (message.media instanceof TL_messageMediaUnsupported) {
                                this.messageText = LocaleController.getString("UnsupportedMedia", C0691R.string.UnsupportedMedia);
                            } else {
                                if (message.media instanceof TL_messageMediaDocument) {
                                    if (isSticker()) {
                                        String sch = getStrickerChar();
                                        if (sch == null || sch.length() <= 0) {
                                            this.messageText = LocaleController.getString("AttachSticker", C0691R.string.AttachSticker);
                                        } else {
                                            objArr = new Object[MESSAGE_SEND_STATE_SEND_ERROR];
                                            objArr[0] = sch;
                                            objArr[MESSAGE_SEND_STATE_SENDING] = LocaleController.getString("AttachSticker", C0691R.string.AttachSticker);
                                            this.messageText = String.format("%s %s", objArr);
                                        }
                                    } else if (isMusic()) {
                                        this.messageText = LocaleController.getString("AttachMusic", C0691R.string.AttachMusic);
                                    } else if (isGif()) {
                                        this.messageText = LocaleController.getString("AttachGif", C0691R.string.AttachGif);
                                    } else {
                                        String name = FileLoader.getDocumentFileName(message.media.document);
                                        if (name == null || name.length() <= 0) {
                                            this.messageText = LocaleController.getString("AttachDocument", C0691R.string.AttachDocument);
                                        } else {
                                            this.messageText = name;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                this.messageText = LocaleController.getString("AttachLocation", C0691R.string.AttachLocation);
            }
        }
        if (this.messageText == null) {
            this.messageText = TtmlNode.ANONYMOUS_REGION_ID;
        }
        setType();
        measureInlineBotButtons();
        Calendar rightNow = new GregorianCalendar();
        rightNow.setTimeInMillis(((long) this.messageOwner.date) * 1000);
        int dateDay = rightNow.get(6);
        this.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(rightNow.get(MESSAGE_SEND_STATE_SENDING)), Integer.valueOf(rightNow.get(MESSAGE_SEND_STATE_SEND_ERROR)), Integer.valueOf(dateDay)});
        objArr = new Object[MESSAGE_SEND_STATE_SEND_ERROR];
        objArr[0] = Integer.valueOf(dateYear);
        objArr[MESSAGE_SEND_STATE_SENDING] = Integer.valueOf(dateMonth);
        this.monthKey = String.format("%d_%02d", objArr);
        if (this.messageOwner.message != null) {
            if (this.messageOwner.id < 0) {
                if (this.messageOwner.message.length() > 6 && isVideo()) {
                    this.videoEditedInfo = new VideoEditedInfo();
                    if (!this.videoEditedInfo.parseString(this.messageOwner.message)) {
                        this.videoEditedInfo = null;
                    }
                }
            }
        }
        generateCaption();
        if (generateLayout) {
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            generateLayout(fromUser);
        }
        this.layoutCreated = generateLayout;
        generateThumbs(false);
        checkMediaExistance();
    }

    public static TextPaint getTextPaint() {
        if (textPaint == null) {
            textPaint = new TextPaint(MESSAGE_SEND_STATE_SENDING);
            textPaint.setColor(Theme.MSG_TEXT_COLOR);
            textPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
            textPaint.setTextSize((float) AndroidUtilities.dp((float) MessagesController.getInstance().fontSize));
        }
        return textPaint;
    }

    public void generatePinMessageText(User fromUser, Chat chat) {
        if (fromUser == null && chat == null) {
            if (this.messageOwner.from_id > 0) {
                fromUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            if (fromUser == null) {
                TLObject chat2 = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
            }
        }
        CharSequence string;
        String str;
        TLObject fromUser2;
        if (this.replyMessageObject == null) {
            string = LocaleController.getString("ActionPinnedNoText", C0691R.string.ActionPinnedNoText);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.isMusic()) {
            string = LocaleController.getString("ActionPinnedMusic", C0691R.string.ActionPinnedMusic);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.isVideo()) {
            string = LocaleController.getString("ActionPinnedVideo", C0691R.string.ActionPinnedVideo);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.isGif()) {
            string = LocaleController.getString("ActionPinnedGif", C0691R.string.ActionPinnedGif);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.isVoice()) {
            string = LocaleController.getString("ActionPinnedVoice", C0691R.string.ActionPinnedVoice);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.isSticker()) {
            string = LocaleController.getString("ActionPinnedSticker", C0691R.string.ActionPinnedSticker);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaDocument) {
            string = LocaleController.getString("ActionPinnedFile", C0691R.string.ActionPinnedFile);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGeo) {
            string = LocaleController.getString("ActionPinnedGeo", C0691R.string.ActionPinnedGeo);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaContact) {
            string = LocaleController.getString("ActionPinnedContact", C0691R.string.ActionPinnedContact);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
            string = LocaleController.getString("ActionPinnedPhoto", C0691R.string.ActionPinnedPhoto);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.messageText == null || this.replyMessageObject.messageText.length() <= 0) {
            string = LocaleController.getString("ActionPinnedNoText", C0691R.string.ActionPinnedNoText);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else {
            CharSequence mess = this.replyMessageObject.messageText;
            if (mess.length() > 20) {
                mess = mess.subSequence(0, 20) + "...";
            }
            Object[] objArr = new Object[MESSAGE_SEND_STATE_SENDING];
            objArr[0] = Emoji.replaceEmoji(mess, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            string = LocaleController.formatString("ActionPinnedText", C0691R.string.ActionPinnedText, objArr);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        }
    }

    private void measureInlineBotButtons() {
        this.wantedBotKeyboardWidth = 0;
        if (this.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
            if (botButtonPaint == null) {
                botButtonPaint = new TextPaint(MESSAGE_SEND_STATE_SENDING);
                botButtonPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
                botButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            }
            for (int a = 0; a < this.messageOwner.reply_markup.rows.size(); a += MESSAGE_SEND_STATE_SENDING) {
                TL_keyboardButtonRow row = (TL_keyboardButtonRow) this.messageOwner.reply_markup.rows.get(a);
                int maxButtonSize = 0;
                int size = row.buttons.size();
                for (int b = 0; b < size; b += MESSAGE_SEND_STATE_SENDING) {
                    StaticLayout staticLayout = new StaticLayout(Emoji.replaceEmoji(((KeyboardButton) row.buttons.get(b)).text, botButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), botButtonPaint, AndroidUtilities.dp(2000.0f), Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                    if (staticLayout.getLineCount() > 0) {
                        maxButtonSize = Math.max(maxButtonSize, ((int) Math.ceil((double) (staticLayout.getLineWidth(0) - staticLayout.getLineLeft(0)))) + AndroidUtilities.dp(4.0f));
                    }
                }
                this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((AndroidUtilities.dp(12.0f) + maxButtonSize) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
            }
        }
    }

    public void setType() {
        int oldType = this.type;
        if ((this.messageOwner instanceof TL_message) || (this.messageOwner instanceof TL_messageForwarded_old2)) {
            if (isMediaEmpty()) {
                this.type = 0;
                if (this.messageText == null || this.messageText.length() == 0) {
                    this.messageText = "Empty message";
                }
            } else if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
                this.type = MESSAGE_SEND_STATE_SENDING;
            } else if ((this.messageOwner.media instanceof TL_messageMediaGeo) || (this.messageOwner.media instanceof TL_messageMediaVenue)) {
                this.type = 4;
            } else if (isVideo()) {
                this.type = 3;
            } else if (isVoice()) {
                this.type = MESSAGE_SEND_STATE_SEND_ERROR;
            } else if (isMusic()) {
                this.type = 14;
            } else if (this.messageOwner.media instanceof TL_messageMediaContact) {
                this.type = 12;
            } else if (this.messageOwner.media instanceof TL_messageMediaUnsupported) {
                this.type = 0;
            } else if (this.messageOwner.media instanceof TL_messageMediaDocument) {
                if (this.messageOwner.media.document.mime_type == null) {
                    this.type = 9;
                } else if (isGifDocument(this.messageOwner.media.document)) {
                    this.type = 8;
                } else if (this.messageOwner.media.document.mime_type.equals("image/webp") && isSticker()) {
                    this.type = 13;
                } else {
                    this.type = 9;
                }
            }
        } else if (this.messageOwner instanceof TL_messageService) {
            if (this.messageOwner.action instanceof TL_messageActionLoginUnknownLocation) {
                this.type = 0;
            } else if ((this.messageOwner.action instanceof TL_messageActionChatEditPhoto) || (this.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto)) {
                this.contentType = MESSAGE_SEND_STATE_SENDING;
                this.type = 11;
            } else if (this.messageOwner.action instanceof TL_messageEncryptedAction) {
                if ((this.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) || (this.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) {
                    this.contentType = MESSAGE_SEND_STATE_SENDING;
                    this.type = LINES_PER_BLOCK;
                } else {
                    this.contentType = -1;
                    this.type = -1;
                }
            } else if (this.messageOwner.action instanceof TL_messageActionHistoryClear) {
                this.contentType = -1;
                this.type = -1;
            } else {
                this.contentType = MESSAGE_SEND_STATE_SENDING;
                this.type = LINES_PER_BLOCK;
            }
        }
        if (oldType != 1000 && oldType != this.type) {
            generateThumbs(false);
        }
    }

    public void checkLayout() {
        if (!this.layoutCreated) {
            this.layoutCreated = true;
            User fromUser = null;
            if (isFromUser()) {
                fromUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            generateLayout(fromUser);
        }
    }

    public static boolean isGifDocument(Document document) {
        return (document == null || document.thumb == null || document.mime_type == null || (!document.mime_type.equals("image/gif") && !isNewGifDocument(document))) ? false : true;
    }

    public static boolean isNewGifDocument(Document document) {
        if (!(document == null || document.mime_type == null || !document.mime_type.equals(MimeTypes.VIDEO_MP4))) {
            for (int a = 0; a < document.attributes.size(); a += MESSAGE_SEND_STATE_SENDING) {
                if (document.attributes.get(a) instanceof TL_documentAttributeAnimated) {
                    return true;
                }
            }
        }
        return false;
    }

    public void generateThumbs(boolean update) {
        int a;
        PhotoSize photoObject;
        int b;
        PhotoSize size;
        if (this.messageOwner instanceof TL_messageService) {
            if (!(this.messageOwner.action instanceof TL_messageActionChatEditPhoto)) {
                return;
            }
            if (!update) {
                this.photoThumbs = new ArrayList(this.messageOwner.action.photo.sizes);
            } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty()) {
                for (a = 0; a < this.photoThumbs.size(); a += MESSAGE_SEND_STATE_SENDING) {
                    photoObject = (PhotoSize) this.photoThumbs.get(a);
                    for (b = 0; b < this.messageOwner.action.photo.sizes.size(); b += MESSAGE_SEND_STATE_SENDING) {
                        size = (PhotoSize) this.messageOwner.action.photo.sizes.get(b);
                        if (!(size instanceof TL_photoSizeEmpty) && size.type.equals(photoObject.type)) {
                            photoObject.location = size.location;
                            break;
                        }
                    }
                }
            }
        } else if (this.messageOwner.media != null && !(this.messageOwner.media instanceof TL_messageMediaEmpty)) {
            if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
                if (!update || (this.photoThumbs != null && this.photoThumbs.size() != this.messageOwner.media.photo.sizes.size())) {
                    this.photoThumbs = new ArrayList(this.messageOwner.media.photo.sizes);
                } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty()) {
                    for (a = 0; a < this.photoThumbs.size(); a += MESSAGE_SEND_STATE_SENDING) {
                        photoObject = (PhotoSize) this.photoThumbs.get(a);
                        for (b = 0; b < this.messageOwner.media.photo.sizes.size(); b += MESSAGE_SEND_STATE_SENDING) {
                            size = (PhotoSize) this.messageOwner.media.photo.sizes.get(b);
                            if (!(size instanceof TL_photoSizeEmpty) && size.type.equals(photoObject.type)) {
                                photoObject.location = size.location;
                                break;
                            }
                        }
                    }
                }
            } else if (this.messageOwner.media instanceof TL_messageMediaDocument) {
                if (!(this.messageOwner.media.document.thumb instanceof TL_photoSizeEmpty)) {
                    if (!update) {
                        this.photoThumbs = new ArrayList();
                        this.photoThumbs.add(this.messageOwner.media.document.thumb);
                    } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty() && this.messageOwner.media.document.thumb != null) {
                        photoObject = (PhotoSize) this.photoThumbs.get(0);
                        photoObject.location = this.messageOwner.media.document.thumb.location;
                        photoObject.f34w = this.messageOwner.media.document.thumb.f34w;
                        photoObject.f33h = this.messageOwner.media.document.thumb.f33h;
                    }
                }
            } else if (!(this.messageOwner.media instanceof TL_messageMediaWebPage)) {
            } else {
                if (this.messageOwner.media.webpage.photo != null) {
                    if (!update || this.photoThumbs == null) {
                        this.photoThumbs = new ArrayList(this.messageOwner.media.webpage.photo.sizes);
                    } else if (!this.photoThumbs.isEmpty()) {
                        for (a = 0; a < this.photoThumbs.size(); a += MESSAGE_SEND_STATE_SENDING) {
                            photoObject = (PhotoSize) this.photoThumbs.get(a);
                            for (b = 0; b < this.messageOwner.media.webpage.photo.sizes.size(); b += MESSAGE_SEND_STATE_SENDING) {
                                size = (PhotoSize) this.messageOwner.media.webpage.photo.sizes.get(b);
                                if (!(size instanceof TL_photoSizeEmpty) && size.type.equals(photoObject.type)) {
                                    photoObject.location = size.location;
                                    break;
                                }
                            }
                        }
                    }
                } else if (this.messageOwner.media.webpage.document != null && !(this.messageOwner.media.webpage.document.thumb instanceof TL_photoSizeEmpty)) {
                    if (!update) {
                        this.photoThumbs = new ArrayList();
                        this.photoThumbs.add(this.messageOwner.media.webpage.document.thumb);
                    } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty() && this.messageOwner.media.webpage.document.thumb != null) {
                        ((PhotoSize) this.photoThumbs.get(0)).location = this.messageOwner.media.webpage.document.thumb.location;
                    }
                }
            }
        }
    }

    public CharSequence replaceWithLink(CharSequence source, String param, ArrayList<Integer> uids, AbstractMap<Integer, User> usersDict) {
        if (TextUtils.indexOf(source, param) < 0) {
            return source;
        }
        SpannableStringBuilder names = new SpannableStringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
        for (int a = 0; a < uids.size(); a += MESSAGE_SEND_STATE_SENDING) {
            User user = null;
            if (usersDict != null) {
                user = (User) usersDict.get(uids.get(a));
            }
            if (user == null) {
                user = MessagesController.getInstance().getUser((Integer) uids.get(a));
            }
            if (user != null) {
                String name = UserObject.getUserName(user);
                int start = names.length();
                if (names.length() != 0) {
                    names.append(", ");
                }
                names.append(name);
                names.setSpan(new URLSpanNoUnderlineBold(TtmlNode.ANONYMOUS_REGION_ID + user.id), start, name.length() + start, 33);
            }
        }
        String[] strArr = new String[MESSAGE_SEND_STATE_SENDING];
        strArr[0] = param;
        CharSequence[] charSequenceArr = new CharSequence[MESSAGE_SEND_STATE_SENDING];
        charSequenceArr[0] = names;
        return TextUtils.replace(source, strArr, charSequenceArr);
    }

    public CharSequence replaceWithLink(CharSequence source, String param, TLObject object) {
        int start = TextUtils.indexOf(source, param);
        if (start < 0) {
            return source;
        }
        String name;
        int id;
        if (object instanceof User) {
            name = UserObject.getUserName((User) object);
            id = ((User) object).id;
        } else if (object instanceof Chat) {
            name = ((Chat) object).title;
            id = -((Chat) object).id;
        } else {
            name = TtmlNode.ANONYMOUS_REGION_ID;
            id = 0;
        }
        String[] strArr = new String[MESSAGE_SEND_STATE_SENDING];
        strArr[0] = param;
        CharSequence[] charSequenceArr = new String[MESSAGE_SEND_STATE_SENDING];
        charSequenceArr[0] = name;
        SpannableStringBuilder builder = new SpannableStringBuilder(TextUtils.replace(source, strArr, charSequenceArr));
        builder.setSpan(new URLSpanNoUnderlineBold(TtmlNode.ANONYMOUS_REGION_ID + id), start, name.length() + start, 33);
        return builder;
    }

    public String getExtension() {
        String fileName = getFileName();
        int idx = fileName.lastIndexOf(46);
        String ext = null;
        if (idx != -1) {
            ext = fileName.substring(idx + MESSAGE_SEND_STATE_SENDING);
        }
        if (ext == null || ext.length() == 0) {
            ext = this.messageOwner.media.document.mime_type;
        }
        if (ext == null) {
            ext = TtmlNode.ANONYMOUS_REGION_ID;
        }
        return ext.toUpperCase();
    }

    public String getFileName() {
        if (this.messageOwner.media instanceof TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(this.messageOwner.media.document);
        }
        if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
            ArrayList<PhotoSize> sizes = this.messageOwner.media.photo.sizes;
            if (sizes.size() > 0) {
                PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    return FileLoader.getAttachFileName(sizeFull);
                }
            }
        } else if (this.messageOwner.media instanceof TL_messageMediaWebPage) {
            return FileLoader.getAttachFileName(this.messageOwner.media.webpage.document);
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public int getFileType() {
        if (isVideo()) {
            return MESSAGE_SEND_STATE_SEND_ERROR;
        }
        if (isVoice()) {
            return MESSAGE_SEND_STATE_SENDING;
        }
        if (this.messageOwner.media instanceof TL_messageMediaDocument) {
            return 3;
        }
        if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
            return 0;
        }
        return 4;
    }

    private static boolean containsUrls(CharSequence message) {
        if (message == null || message.length() < MESSAGE_SEND_STATE_SEND_ERROR || message.length() > 20480) {
            return false;
        }
        int length = message.length();
        int digitsInRow = 0;
        int schemeSequence = 0;
        int dotSequence = 0;
        char lastChar = '\u0000';
        int i = 0;
        while (i < length) {
            char c = message.charAt(i);
            if (c >= '0' && c <= '9') {
                digitsInRow += MESSAGE_SEND_STATE_SENDING;
                if (digitsInRow >= 6) {
                    return true;
                }
                schemeSequence = 0;
                dotSequence = 0;
            } else if (c == ' ' || digitsInRow <= 0) {
                digitsInRow = 0;
            }
            if ((c == '@' || c == '#' || c == '/') && i == 0) {
                return true;
            }
            if (i != 0 && (message.charAt(i - 1) == ' ' || message.charAt(i - 1) == '\n')) {
                return true;
            }
            if (c == ':') {
                if (schemeSequence == 0) {
                    schemeSequence = MESSAGE_SEND_STATE_SENDING;
                } else {
                    schemeSequence = 0;
                }
            } else if (c == '/') {
                if (schemeSequence == MESSAGE_SEND_STATE_SEND_ERROR) {
                    return true;
                }
                if (schemeSequence == MESSAGE_SEND_STATE_SENDING) {
                    schemeSequence += MESSAGE_SEND_STATE_SENDING;
                } else {
                    schemeSequence = 0;
                }
            } else if (c == '.') {
                if (dotSequence != 0 || lastChar == ' ') {
                    dotSequence = 0;
                } else {
                    dotSequence += MESSAGE_SEND_STATE_SENDING;
                }
            } else if (c != ' ' && lastChar == '.' && dotSequence == MESSAGE_SEND_STATE_SENDING) {
                return true;
            } else {
                dotSequence = 0;
            }
            lastChar = c;
            i += MESSAGE_SEND_STATE_SENDING;
        }
        return false;
    }

    public void generateLinkDescription() {
        if (this.linkDescription == null && (this.messageOwner.media instanceof TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TL_webPage) && this.messageOwner.media.webpage.description != null) {
            this.linkDescription = Factory.getInstance().newSpannable(this.messageOwner.media.webpage.description);
            if (containsUrls(this.linkDescription)) {
                try {
                    Linkify.addLinks((Spannable) this.linkDescription, MESSAGE_SEND_STATE_SENDING);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
            this.linkDescription = Emoji.replaceEmoji(this.linkDescription, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        }
    }

    public void generateCaption() {
        if (this.caption == null && this.messageOwner.media != null && this.messageOwner.media.caption != null && this.messageOwner.media.caption.length() > 0) {
            this.caption = Emoji.replaceEmoji(this.messageOwner.media.caption, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (containsUrls(this.caption)) {
                try {
                    Linkify.addLinks((Spannable) this.caption, 5);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                addUsernamesAndHashtags(this.caption, true);
            }
        }
    }

    private static void addUsernamesAndHashtags(CharSequence charSequence, boolean botCommands) {
        try {
            if (urlPattern == null) {
                urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s)@[a-zA-Z\\d_]{1,32}|(^|\\s)#[\\w\\.]+");
            }
            Matcher matcher = urlPattern.matcher(charSequence);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (!(charSequence.charAt(start) == '@' || charSequence.charAt(start) == '#' || charSequence.charAt(start) == '/')) {
                    start += MESSAGE_SEND_STATE_SENDING;
                }
                URLSpanNoUnderline url = null;
                if (charSequence.charAt(start) != '/') {
                    url = new URLSpanNoUnderline(charSequence.subSequence(start, end).toString());
                } else if (botCommands) {
                    url = new URLSpanBotCommand(charSequence.subSequence(start, end).toString());
                }
                if (url != null) {
                    ((Spannable) charSequence).setSpan(url, start, end, 0);
                }
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public static void addLinks(CharSequence messageText) {
        addLinks(messageText, true);
    }

    public static void addLinks(CharSequence messageText, boolean botCommands) {
        if ((messageText instanceof Spannable) && containsUrls(messageText)) {
            if (messageText.length() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                try {
                    Linkify.addLinks((Spannable) messageText, 5);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            } else {
                try {
                    Linkify.addLinks((Spannable) messageText, MESSAGE_SEND_STATE_SENDING);
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
            }
            addUsernamesAndHashtags(messageText, botCommands);
        }
    }

    private void generateLayout(User fromUser) {
        if (this.type == 0 && this.messageOwner.to_id != null && this.messageText != null && this.messageText.length() != 0) {
            boolean hasEntities;
            int a;
            boolean useManualParse;
            int maxWidth;
            generateLinkDescription();
            this.textLayoutBlocks = new ArrayList();
            if (this.messageOwner.send_state != 0) {
                hasEntities = false;
                for (a = 0; a < this.messageOwner.entities.size(); a += MESSAGE_SEND_STATE_SENDING) {
                    if (!(this.messageOwner.entities.get(a) instanceof TL_inputMessageEntityMentionName)) {
                        hasEntities = true;
                        break;
                    }
                }
            } else {
                hasEntities = !this.messageOwner.entities.isEmpty();
            }
            if (hasEntities || !((this.messageOwner instanceof TL_message_old) || (this.messageOwner instanceof TL_message_old2) || (this.messageOwner instanceof TL_message_old3) || (this.messageOwner instanceof TL_message_old4) || (this.messageOwner instanceof TL_messageForwarded_old) || (this.messageOwner instanceof TL_messageForwarded_old2) || (this.messageOwner instanceof TL_message_secret) || ((isOut() && this.messageOwner.send_state != 0) || this.messageOwner.id < 0 || (this.messageOwner.media instanceof TL_messageMediaUnsupported)))) {
                useManualParse = false;
            } else {
                useManualParse = true;
            }
            if (useManualParse) {
                addLinks(this.messageText);
            } else if ((this.messageText instanceof Spannable) && this.messageText.length() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                try {
                    Linkify.addLinks((Spannable) this.messageText, 4);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
            if (this.messageText instanceof Spannable) {
                Spannable spannable = (Spannable) this.messageText;
                int count = this.messageOwner.entities.size();
                URLSpan[] spans = (URLSpan[]) spannable.getSpans(0, this.messageText.length(), URLSpan.class);
                for (a = 0; a < count; a += MESSAGE_SEND_STATE_SENDING) {
                    MessageEntity entity = (MessageEntity) this.messageOwner.entities.get(a);
                    if (entity.length > 0 && entity.offset >= 0 && entity.offset < this.messageOwner.message.length()) {
                        if (entity.offset + entity.length > this.messageOwner.message.length()) {
                            entity.length = this.messageOwner.message.length() - entity.offset;
                        }
                        if (spans != null && spans.length > 0) {
                            for (int b = 0; b < spans.length; b += MESSAGE_SEND_STATE_SENDING) {
                                if (spans[b] != null) {
                                    int start = spannable.getSpanStart(spans[b]);
                                    int end = spannable.getSpanEnd(spans[b]);
                                    if ((entity.offset <= start && entity.offset + entity.length >= start) || (entity.offset <= end && entity.offset + entity.length >= end)) {
                                        spannable.removeSpan(spans[b]);
                                        spans[b] = null;
                                    }
                                }
                            }
                        }
                        if (entity instanceof TL_messageEntityBold) {
                            spannable.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), entity.offset, entity.offset + entity.length, 33);
                        } else if (entity instanceof TL_messageEntityItalic) {
                            spannable.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), entity.offset, entity.offset + entity.length, 33);
                        } else if ((entity instanceof TL_messageEntityCode) || (entity instanceof TL_messageEntityPre)) {
                            spannable.setSpan(new TypefaceSpan(Typeface.MONOSPACE, AndroidUtilities.dp((float) (MessagesController.getInstance().fontSize - 1))), entity.offset, entity.offset + entity.length, 33);
                        } else if (entity instanceof TL_messageEntityMentionName) {
                            spannable.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + ((TL_messageEntityMentionName) entity).user_id), entity.offset, entity.offset + entity.length, 33);
                        } else if (entity instanceof TL_inputMessageEntityMentionName) {
                            spannable.setSpan(new URLSpanUserMention(TtmlNode.ANONYMOUS_REGION_ID + ((TL_inputMessageEntityMentionName) entity).user_id.user_id), entity.offset, entity.offset + entity.length, 33);
                        } else if (!useManualParse) {
                            String url = this.messageOwner.message.substring(entity.offset, entity.offset + entity.length);
                            if (entity instanceof TL_messageEntityBotCommand) {
                                spannable.setSpan(new URLSpanBotCommand(url), entity.offset, entity.offset + entity.length, 33);
                            } else if ((entity instanceof TL_messageEntityHashtag) || (entity instanceof TL_messageEntityMention)) {
                                spannable.setSpan(new URLSpanNoUnderline(url), entity.offset, entity.offset + entity.length, 33);
                            } else if (entity instanceof TL_messageEntityEmail) {
                                spannable.setSpan(new URLSpanReplacement("mailto:" + url), entity.offset, entity.offset + entity.length, 33);
                            } else if (entity instanceof TL_messageEntityUrl) {
                                if (url.toLowerCase().startsWith("http")) {
                                    spannable.setSpan(new URLSpan(url), entity.offset, entity.offset + entity.length, 33);
                                } else {
                                    spannable.setSpan(new URLSpan("http://" + url), entity.offset, entity.offset + entity.length, 33);
                                }
                            } else if (entity instanceof TL_messageEntityTextUrl) {
                                spannable.setSpan(new URLSpanReplacement(entity.url), entity.offset, entity.offset + entity.length, 33);
                            }
                        }
                    }
                }
            }
            if (AndroidUtilities.isTablet()) {
                if (this.messageOwner.from_id <= 0 || ((this.messageOwner.to_id.channel_id == 0 && this.messageOwner.to_id.chat_id == 0) || isOut())) {
                    maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(80.0f);
                } else {
                    maxWidth = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(122.0f);
                }
            } else if (this.messageOwner.from_id <= 0 || ((this.messageOwner.to_id.channel_id == 0 && this.messageOwner.to_id.chat_id == 0) || isOut())) {
                maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(80.0f);
            } else {
                maxWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(122.0f);
            }
            if ((fromUser != null && fromUser.bot) || ((isMegagroup() || !(this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.channel_id == 0)) && !isOut())) {
                maxWidth -= AndroidUtilities.dp(20.0f);
            }
            try {
                StaticLayout textLayout = new StaticLayout(this.messageText, textPaint, maxWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                this.textHeight = textLayout.getHeight();
                int linesCount = textLayout.getLineCount();
                int blocksCount = (int) Math.ceil((double) (((float) linesCount) / 10.0f));
                int linesOffset = 0;
                float prevOffset = 0.0f;
                for (a = 0; a < blocksCount; a += MESSAGE_SEND_STATE_SENDING) {
                    int currentBlockLinesCount = Math.min(LINES_PER_BLOCK, linesCount - linesOffset);
                    TextLayoutBlock block = new TextLayoutBlock();
                    if (blocksCount == MESSAGE_SEND_STATE_SENDING) {
                        block.textLayout = textLayout;
                        block.textYOffset = 0.0f;
                        block.charactersOffset = 0;
                        block.height = this.textHeight;
                    } else {
                        int startCharacter = textLayout.getLineStart(linesOffset);
                        int endCharacter = textLayout.getLineEnd((linesOffset + currentBlockLinesCount) - 1);
                        if (endCharacter >= startCharacter) {
                            block.charactersOffset = startCharacter;
                            try {
                                block.textLayout = new StaticLayout(this.messageText.subSequence(startCharacter, endCharacter), textPaint, maxWidth, Alignment.ALIGN_NORMAL, TouchHelperCallback.ALPHA_FULL, 0.0f, false);
                                block.textYOffset = (float) textLayout.getLineTop(linesOffset);
                                if (a != 0) {
                                    block.height = (int) (block.textYOffset - prevOffset);
                                }
                                block.height = Math.max(block.height, block.textLayout.getLineBottom(block.textLayout.getLineCount() - 1));
                                prevOffset = block.textYOffset;
                                if (a == blocksCount - 1) {
                                    currentBlockLinesCount = Math.max(currentBlockLinesCount, block.textLayout.getLineCount());
                                    try {
                                        this.textHeight = Math.max(this.textHeight, (int) (block.textYOffset + ((float) block.textLayout.getHeight())));
                                    } catch (Throwable e2) {
                                        FileLog.m13e("tmessages", e2);
                                    }
                                }
                            } catch (Throwable e22) {
                                FileLog.m13e("tmessages", e22);
                            }
                        }
                    }
                    this.textLayoutBlocks.add(block);
                    float lastLeft = 0.0f;
                    block.textXOffset = 0.0f;
                    try {
                        float lastLeft2 = block.textLayout.getLineLeft(currentBlockLinesCount - 1);
                        block.textXOffset = lastLeft2;
                        lastLeft = lastLeft2;
                    } catch (Throwable e222) {
                        FileLog.m13e("tmessages", e222);
                    }
                    float lastLine = 0.0f;
                    try {
                        lastLine = block.textLayout.getLineWidth(currentBlockLinesCount - 1);
                    } catch (Throwable e2222) {
                        FileLog.m13e("tmessages", e2222);
                    }
                    int linesMaxWidth = (int) Math.ceil((double) lastLine);
                    boolean hasNonRTL = false;
                    if (a == blocksCount - 1) {
                        this.lastLineWidth = linesMaxWidth;
                    }
                    int lastLineWidthWithLeft = (int) Math.ceil((double) (lastLine + lastLeft));
                    int linesMaxWidthWithLeft = lastLineWidthWithLeft;
                    if (lastLeft == 0.0f) {
                        hasNonRTL = true;
                    }
                    if (currentBlockLinesCount > MESSAGE_SEND_STATE_SENDING) {
                        float textRealMaxWidth = 0.0f;
                        float textRealMaxWidthWithLeft = 0.0f;
                        for (int n = 0; n < currentBlockLinesCount; n += MESSAGE_SEND_STATE_SENDING) {
                            float lineWidth;
                            float lineLeft;
                            try {
                                lineWidth = block.textLayout.getLineWidth(n);
                            } catch (Throwable e22222) {
                                FileLog.m13e("tmessages", e22222);
                                lineWidth = 0.0f;
                            }
                            if (lineWidth > ((float) (maxWidth + 100))) {
                                lineWidth = (float) maxWidth;
                            }
                            try {
                                lineLeft = block.textLayout.getLineLeft(n);
                            } catch (Throwable e222222) {
                                FileLog.m13e("tmessages", e222222);
                                lineLeft = 0.0f;
                            }
                            block.textXOffset = Math.min(block.textXOffset, lineLeft);
                            if (lineLeft == 0.0f) {
                                hasNonRTL = true;
                            }
                            textRealMaxWidth = Math.max(textRealMaxWidth, lineWidth);
                            textRealMaxWidthWithLeft = Math.max(textRealMaxWidthWithLeft, lineWidth + lineLeft);
                            linesMaxWidth = Math.max(linesMaxWidth, (int) Math.ceil((double) lineWidth));
                            linesMaxWidthWithLeft = Math.max(linesMaxWidthWithLeft, (int) Math.ceil((double) (lineWidth + lineLeft)));
                        }
                        if (hasNonRTL) {
                            textRealMaxWidth = textRealMaxWidthWithLeft;
                            if (a == blocksCount - 1) {
                                this.lastLineWidth = lastLineWidthWithLeft;
                            }
                        } else if (a == blocksCount - 1) {
                            this.lastLineWidth = linesMaxWidth;
                        }
                        this.textWidth = Math.max(this.textWidth, (int) Math.ceil((double) textRealMaxWidth));
                    } else {
                        this.textWidth = Math.max(this.textWidth, Math.min(maxWidth, linesMaxWidth));
                    }
                    if (hasNonRTL) {
                        block.textXOffset = 0.0f;
                    }
                    linesOffset += currentBlockLinesCount;
                }
            } catch (Throwable e2222222) {
                FileLog.m13e("tmessages", e2222222);
            }
        }
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    public boolean isOutOwner() {
        return this.messageOwner.out && this.messageOwner.from_id > 0 && !this.messageOwner.post;
    }

    public boolean isFromUser() {
        return this.messageOwner.from_id > 0 && !this.messageOwner.post;
    }

    public boolean isUnread() {
        return this.messageOwner.unread;
    }

    public boolean isContentUnread() {
        return this.messageOwner.media_unread;
    }

    public void setIsRead() {
        this.messageOwner.unread = false;
    }

    public int getUnradFlags() {
        return getUnreadFlags(this.messageOwner);
    }

    public static int getUnreadFlags(Message message) {
        int flags = 0;
        if (!message.unread) {
            flags = 0 | MESSAGE_SEND_STATE_SENDING;
        }
        if (message.media_unread) {
            return flags;
        }
        return flags | MESSAGE_SEND_STATE_SEND_ERROR;
    }

    public void setContentIsRead() {
        this.messageOwner.media_unread = false;
    }

    public int getId() {
        return this.messageOwner.id;
    }

    public boolean isSecretPhoto() {
        return (this.messageOwner instanceof TL_message_secret) && (this.messageOwner.media instanceof TL_messageMediaPhoto) && this.messageOwner.ttl > 0 && this.messageOwner.ttl <= 60;
    }

    public boolean isSecretMedia() {
        return (this.messageOwner instanceof TL_message_secret) && (((this.messageOwner.media instanceof TL_messageMediaPhoto) && this.messageOwner.ttl > 0 && this.messageOwner.ttl <= 60) || isVoice() || isVideo());
    }

    public static void setUnreadFlags(Message message, int flag) {
        boolean z;
        boolean z2 = true;
        if ((flag & MESSAGE_SEND_STATE_SENDING) == 0) {
            z = true;
        } else {
            z = false;
        }
        message.unread = z;
        if ((flag & MESSAGE_SEND_STATE_SEND_ERROR) != 0) {
            z2 = false;
        }
        message.media_unread = z2;
    }

    public static boolean isUnread(Message message) {
        return message.unread;
    }

    public static boolean isContentUnread(Message message) {
        return message.media_unread;
    }

    public boolean isMegagroup() {
        return isMegagroup(this.messageOwner);
    }

    public static boolean isMegagroup(Message message) {
        return (message.flags & LinearLayoutManager.INVALID_OFFSET) != 0;
    }

    public static boolean isOut(Message message) {
        return message.out;
    }

    public long getDialogId() {
        return getDialogId(this.messageOwner);
    }

    public static long getDialogId(Message message) {
        if (message.dialog_id == 0 && message.to_id != null) {
            if (message.to_id.chat_id != 0) {
                if (message.to_id.chat_id < 0) {
                    message.dialog_id = AndroidUtilities.makeBroadcastId(message.to_id.chat_id);
                } else {
                    message.dialog_id = (long) (-message.to_id.chat_id);
                }
            } else if (message.to_id.channel_id != 0) {
                message.dialog_id = (long) (-message.to_id.channel_id);
            } else if (isOut(message)) {
                message.dialog_id = (long) message.to_id.user_id;
            } else {
                message.dialog_id = (long) message.from_id;
            }
        }
        return message.dialog_id;
    }

    public boolean isSending() {
        return this.messageOwner.send_state == MESSAGE_SEND_STATE_SENDING && this.messageOwner.id < 0;
    }

    public boolean isSendError() {
        return this.messageOwner.send_state == MESSAGE_SEND_STATE_SEND_ERROR && this.messageOwner.id < 0;
    }

    public boolean isSent() {
        return this.messageOwner.send_state == 0 || this.messageOwner.id > 0;
    }

    public String getSecretTimeString() {
        if (!isSecretMedia()) {
            return null;
        }
        int secondsLeft = this.messageOwner.ttl;
        if (this.messageOwner.destroyTime != 0) {
            secondsLeft = Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance().getCurrentTime());
        }
        if (secondsLeft < 60) {
            return secondsLeft + "s";
        }
        return (secondsLeft / 60) + "m";
    }

    public String getDocumentName() {
        if (this.messageOwner.media == null || this.messageOwner.media.document == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        return FileLoader.getDocumentFileName(this.messageOwner.media.document);
    }

    public static boolean isStickerDocument(Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a += MESSAGE_SEND_STATE_SENDING) {
                if (((DocumentAttribute) document.attributes.get(a)) instanceof TL_documentAttributeSticker) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceDocument(Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a += MESSAGE_SEND_STATE_SENDING) {
                DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
                if (attribute instanceof TL_documentAttributeAudio) {
                    return attribute.voice;
                }
            }
        }
        return false;
    }

    public static boolean isMusicDocument(Document document) {
        if (document == null) {
            return false;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (!(attribute instanceof TL_documentAttributeAudio)) {
                a += MESSAGE_SEND_STATE_SENDING;
            } else if (attribute.voice) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isVideoDocument(Document document) {
        if (document == null) {
            return false;
        }
        boolean isAnimated = false;
        boolean isVideo = false;
        for (int a = 0; a < document.attributes.size(); a += MESSAGE_SEND_STATE_SENDING) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeVideo) {
                isVideo = true;
            } else if (attribute instanceof TL_documentAttributeAnimated) {
                isAnimated = true;
            }
        }
        if (!isVideo || isAnimated) {
            return false;
        }
        return true;
    }

    public Document getDocument() {
        if (this.messageOwner.media instanceof TL_messageMediaWebPage) {
            return this.messageOwner.media.webpage.document;
        }
        return this.messageOwner.media != null ? this.messageOwner.media.document : null;
    }

    public static boolean isStickerMessage(Message message) {
        return (message.media == null || message.media.document == null || !isStickerDocument(message.media.document)) ? false : true;
    }

    public static boolean isMusicMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isMusicDocument(message.media.webpage.document);
        }
        return (message.media == null || message.media.document == null || !isMusicDocument(message.media.document)) ? false : true;
    }

    public static boolean isVoiceMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isVoiceDocument(message.media.webpage.document);
        }
        return (message.media == null || message.media.document == null || !isVoiceDocument(message.media.document)) ? false : true;
    }

    public static boolean isVideoMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isVideoDocument(message.media.webpage.document);
        }
        return (message.media == null || message.media.document == null || !isVideoDocument(message.media.document)) ? false : true;
    }

    public static InputStickerSet getInputStickerSet(Message message) {
        if (message.media == null || message.media.document == null) {
            return null;
        }
        Iterator i$ = message.media.document.attributes.iterator();
        while (i$.hasNext()) {
            DocumentAttribute attribute = (DocumentAttribute) i$.next();
            if (attribute instanceof TL_documentAttributeSticker) {
                if (attribute.stickerset instanceof TL_inputStickerSetEmpty) {
                    return null;
                }
                return attribute.stickerset;
            }
        }
        return null;
    }

    public String getStrickerChar() {
        if (!(this.messageOwner.media == null || this.messageOwner.media.document == null)) {
            Iterator i$ = this.messageOwner.media.document.attributes.iterator();
            while (i$.hasNext()) {
                DocumentAttribute attribute = (DocumentAttribute) i$.next();
                if (attribute instanceof TL_documentAttributeSticker) {
                    return attribute.alt;
                }
            }
        }
        return null;
    }

    public int getApproximateHeight() {
        if (this.type == 0) {
            int i = this.textHeight;
            int dp = ((this.messageOwner.media instanceof TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TL_webPage)) ? AndroidUtilities.dp(100.0f) : 0;
            int height = i + dp;
            return isReply() ? height + AndroidUtilities.dp(42.0f) : height;
        } else if (this.type == MESSAGE_SEND_STATE_SEND_ERROR) {
            return AndroidUtilities.dp(72.0f);
        } else {
            if (this.type == 12) {
                return AndroidUtilities.dp(71.0f);
            }
            if (this.type == 9) {
                return AndroidUtilities.dp(100.0f);
            }
            if (this.type == 4) {
                return AndroidUtilities.dp(114.0f);
            }
            if (this.type == 14) {
                return AndroidUtilities.dp(82.0f);
            }
            if (this.type == LINES_PER_BLOCK) {
                return AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
            }
            if (this.type == 11) {
                return AndroidUtilities.dp(50.0f);
            }
            int photoHeight;
            int photoWidth;
            if (this.type == 13) {
                float maxWidth;
                float maxHeight = ((float) AndroidUtilities.displaySize.y) * 0.4f;
                if (AndroidUtilities.isTablet()) {
                    maxWidth = ((float) AndroidUtilities.getMinTabletSide()) * 0.5f;
                } else {
                    maxWidth = ((float) AndroidUtilities.displaySize.x) * 0.5f;
                }
                photoHeight = 0;
                photoWidth = 0;
                Iterator i$ = this.messageOwner.media.document.attributes.iterator();
                while (i$.hasNext()) {
                    DocumentAttribute attribute = (DocumentAttribute) i$.next();
                    if (attribute instanceof TL_documentAttributeImageSize) {
                        photoWidth = attribute.f29w;
                        photoHeight = attribute.f28h;
                        break;
                    }
                }
                if (photoWidth == 0) {
                    photoHeight = (int) maxHeight;
                    photoWidth = photoHeight + AndroidUtilities.dp(100.0f);
                }
                if (((float) photoHeight) > maxHeight) {
                    photoWidth = (int) (((float) photoWidth) * (maxHeight / ((float) photoHeight)));
                    photoHeight = (int) maxHeight;
                }
                if (((float) photoWidth) > maxWidth) {
                    photoHeight = (int) (((float) photoHeight) * (maxWidth / ((float) photoWidth)));
                }
                return photoHeight + AndroidUtilities.dp(14.0f);
            }
            if (AndroidUtilities.isTablet()) {
                photoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
            } else {
                photoWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
            }
            photoHeight = photoWidth + AndroidUtilities.dp(100.0f);
            if (photoWidth > AndroidUtilities.getPhotoSize()) {
                photoWidth = AndroidUtilities.getPhotoSize();
            }
            if (photoHeight > AndroidUtilities.getPhotoSize()) {
                photoHeight = AndroidUtilities.getPhotoSize();
            }
            PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
            if (currentPhotoObject != null) {
                int h = (int) (((float) currentPhotoObject.f33h) / (((float) currentPhotoObject.f34w) / ((float) photoWidth)));
                if (h == 0) {
                    h = AndroidUtilities.dp(100.0f);
                }
                if (h > photoHeight) {
                    h = photoHeight;
                } else if (h < AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN)) {
                    h = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN);
                }
                if (isSecretPhoto()) {
                    if (AndroidUtilities.isTablet()) {
                        h = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                    } else {
                        h = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f);
                    }
                }
                photoHeight = h;
            }
            return photoHeight + AndroidUtilities.dp(14.0f);
        }
    }

    public String getStickerEmoji() {
        int a = 0;
        while (a < this.messageOwner.media.document.attributes.size()) {
            DocumentAttribute attribute = (DocumentAttribute) this.messageOwner.media.document.attributes.get(a);
            if (!(attribute instanceof TL_documentAttributeSticker)) {
                a += MESSAGE_SEND_STATE_SENDING;
            } else if (attribute.alt == null || attribute.alt.length() <= 0) {
                return null;
            } else {
                return attribute.alt;
            }
        }
        return null;
    }

    public boolean isSticker() {
        if (this.type != 1000) {
            return this.type == 13;
        } else {
            return isStickerMessage(this.messageOwner);
        }
    }

    public boolean isMusic() {
        return isMusicMessage(this.messageOwner);
    }

    public boolean isVoice() {
        return isVoiceMessage(this.messageOwner);
    }

    public boolean isVideo() {
        return isVideoMessage(this.messageOwner);
    }

    public boolean isGif() {
        return (this.messageOwner.media instanceof TL_messageMediaDocument) && isGifDocument(this.messageOwner.media.document);
    }

    public boolean isWebpageDocument() {
        return (!(this.messageOwner.media instanceof TL_messageMediaWebPage) || this.messageOwner.media.webpage.document == null || isGifDocument(this.messageOwner.media.webpage.document)) ? false : true;
    }

    public boolean isNewGif() {
        return this.messageOwner.media != null && isNewGifDocument(this.messageOwner.media.document);
    }

    public String getMusicTitle() {
        Document document;
        if (this.type == 0) {
            document = this.messageOwner.media.webpage.document;
        } else {
            document = this.messageOwner.media.document;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (!(attribute instanceof TL_documentAttributeAudio)) {
                a += MESSAGE_SEND_STATE_SENDING;
            } else if (attribute.voice) {
                return LocaleController.formatDateAudio((long) this.messageOwner.date);
            } else {
                String title = attribute.title;
                if (title != null && title.length() != 0) {
                    return title;
                }
                title = FileLoader.getDocumentFileName(document);
                if (title == null || title.length() == 0) {
                    return LocaleController.getString("AudioUnknownTitle", C0691R.string.AudioUnknownTitle);
                }
                return title;
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public int getDuration() {
        Document document;
        if (this.type == 0) {
            document = this.messageOwner.media.webpage.document;
        } else {
            document = this.messageOwner.media.document;
        }
        for (int a = 0; a < document.attributes.size(); a += MESSAGE_SEND_STATE_SENDING) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeAudio) {
                return attribute.duration;
            }
        }
        return 0;
    }

    public String getMusicAuthor() {
        Document document;
        if (this.type == 0) {
            document = this.messageOwner.media.webpage.document;
        } else {
            document = this.messageOwner.media.document;
        }
        for (int a = 0; a < document.attributes.size(); a += MESSAGE_SEND_STATE_SENDING) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeAudio) {
                if (attribute.voice) {
                    if (isOutOwner() || (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_id == UserConfig.getClientUserId())) {
                        return LocaleController.getString("FromYou", C0691R.string.FromYou);
                    }
                    User user = null;
                    Chat chat = null;
                    if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.channel_id != 0) {
                        chat = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                    } else if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_id != 0) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                    } else if (this.messageOwner.from_id < 0) {
                        chat = MessagesController.getInstance().getChat(Integer.valueOf(-this.messageOwner.from_id));
                    } else {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
                    }
                    if (user != null) {
                        return UserObject.getUserName(user);
                    }
                    if (chat != null) {
                        return chat.title;
                    }
                }
                String performer = attribute.performer;
                if (performer == null || performer.length() == 0) {
                    return LocaleController.getString("AudioUnknownArtist", C0691R.string.AudioUnknownArtist);
                }
                return performer;
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    public static boolean isForwardedMessage(Message message) {
        return (message.flags & 4) != 0;
    }

    public boolean isReply() {
        return (this.replyMessageObject == null || !(this.replyMessageObject.messageOwner instanceof TL_messageEmpty)) && !((this.messageOwner.reply_to_msg_id == 0 && this.messageOwner.reply_to_random_id == 0) || (this.messageOwner.flags & 8) == 0);
    }

    public boolean isMediaEmpty() {
        return isMediaEmpty(this.messageOwner);
    }

    public static boolean isMediaEmpty(Message message) {
        return message == null || message.media == null || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage);
    }

    public boolean canEditMessage(Chat chat) {
        return canEditMessage(this.messageOwner, chat);
    }

    public static boolean canEditMessage(Message message, Chat chat) {
        boolean z = true;
        if (message == null || message.to_id == null) {
            return false;
        }
        if ((message.action != null && !(message.action instanceof TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0 || message.id < 0 || Math.abs(message.date - ConnectionsManager.getInstance().getCurrentTime()) > MessagesController.getInstance().maxEditTime) {
            return false;
        }
        if (message.to_id.channel_id == 0) {
            if (!(message.out && ((message.media instanceof TL_messageMediaPhoto) || (((message.media instanceof TL_messageMediaDocument) && !isStickerMessage(message)) || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage) || message.media == null)))) {
                z = false;
            }
            return z;
        }
        if (chat == null && message.to_id.channel_id != 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(message.to_id.channel_id));
            if (chat == null) {
                return false;
            }
        }
        if (!(chat.megagroup && message.out)) {
            if (chat.megagroup) {
                return false;
            }
            if (!((chat.creator || (chat.editor && isOut(message))) && message.post)) {
                return false;
            }
        }
        if ((message.media instanceof TL_messageMediaPhoto) || (((message.media instanceof TL_messageMediaDocument) && !isStickerMessage(message)) || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage) || message.media == null)) {
            return true;
        }
        return false;
    }

    public boolean canDeleteMessage(Chat chat) {
        return canDeleteMessage(this.messageOwner, chat);
    }

    public static boolean canDeleteMessage(Message message, Chat chat) {
        boolean z = false;
        if (message.id < 0) {
            return true;
        }
        if (chat == null && message.to_id.channel_id != 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(message.to_id.channel_id));
        }
        if (ChatObject.isChannel(chat)) {
            if (message.id == MESSAGE_SEND_STATE_SENDING) {
                return false;
            }
            if (chat.creator) {
                return true;
            }
            if (chat.editor) {
                if (isOut(message)) {
                    return true;
                }
                if (message.from_id > 0 && !message.post) {
                    return true;
                }
            } else if (chat.moderator) {
                if (message.from_id > 0 && !message.post) {
                    return true;
                }
            } else if (isOut(message) && message.from_id > 0) {
                return true;
            }
        }
        if (isOut(message) || !ChatObject.isChannel(chat)) {
            z = true;
        }
        return z;
    }

    public String getForwardedName() {
        if (this.messageOwner.fwd_from != null) {
            if (this.messageOwner.fwd_from.channel_id != 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                if (chat != null) {
                    return chat.title;
                }
            } else if (this.messageOwner.fwd_from.from_id != 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                if (user != null) {
                    return UserObject.getUserName(user);
                }
            }
        }
        return null;
    }

    public void checkMediaExistance() {
        this.attachPathExists = false;
        this.mediaExists = false;
        if (this.type == MESSAGE_SEND_STATE_SENDING) {
            if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
                this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
            }
        } else if (this.type == 8 || this.type == 3 || this.type == 9 || this.type == MESSAGE_SEND_STATE_SEND_ERROR || this.type == 14) {
            if (this.messageOwner.attachPath != null && this.messageOwner.attachPath.length() > 0) {
                this.attachPathExists = new File(this.messageOwner.attachPath).exists();
            }
            if (!this.attachPathExists) {
                this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
            }
        } else {
            Document document = getDocument();
            if (document != null) {
                this.mediaExists = FileLoader.getPathToAttach(document).exists();
            } else if (this.type == 0) {
                PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                if (currentPhotoObject != null && currentPhotoObject != null) {
                    this.mediaExists = FileLoader.getPathToAttach(currentPhotoObject, true).exists();
                }
            }
        }
    }
}
