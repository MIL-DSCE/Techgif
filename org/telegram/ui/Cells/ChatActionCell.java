package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.PhotoViewer;

public class ChatActionCell extends BaseCell {
    private static Paint backPaint;
    private static TextPaint textPaint;
    private AvatarDrawable avatarDrawable;
    private MessageObject currentMessageObject;
    private ChatActionCellDelegate delegate;
    private boolean hasReplyMessage;
    private boolean imagePressed;
    private ImageReceiver imageReceiver;
    private URLSpan pressedLink;
    private int previousWidth;
    private int textHeight;
    private StaticLayout textLayout;
    private int textWidth;
    private int textX;
    private int textXLeft;
    private int textY;

    public interface ChatActionCellDelegate {
        void didClickedImage(ChatActionCell chatActionCell);

        void didLongPressed(ChatActionCell chatActionCell);

        void needOpenUserProfile(int i);
    }

    public ChatActionCell(Context context) {
        super(context);
        this.textWidth = 0;
        this.textHeight = 0;
        this.textX = 0;
        this.textY = 0;
        this.textXLeft = 0;
        this.previousWidth = 0;
        this.imagePressed = false;
        if (textPaint == null) {
            textPaint = new TextPaint(1);
            textPaint.setColor(-1);
            textPaint.linkColor = -1;
            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            backPaint = new Paint(1);
        }
        backPaint.setColor(ApplicationLoader.getServiceMessageColor());
        this.imageReceiver = new ImageReceiver(this);
        this.imageReceiver.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable = new AvatarDrawable();
        textPaint.setTextSize((float) AndroidUtilities.dp((float) (MessagesController.getInstance().fontSize - 2)));
    }

    public void setDelegate(ChatActionCellDelegate delegate) {
        this.delegate = delegate;
    }

    public void setMessageObject(MessageObject messageObject) {
        boolean z = true;
        if (this.currentMessageObject != messageObject || (!this.hasReplyMessage && messageObject.replyMessageObject != null)) {
            this.currentMessageObject = messageObject;
            this.hasReplyMessage = messageObject.replyMessageObject != null;
            this.previousWidth = 0;
            if (this.currentMessageObject.type == 11) {
                int id = 0;
                if (messageObject.messageOwner.to_id != null) {
                    if (messageObject.messageOwner.to_id.chat_id != 0) {
                        id = messageObject.messageOwner.to_id.chat_id;
                    } else if (messageObject.messageOwner.to_id.channel_id != 0) {
                        id = messageObject.messageOwner.to_id.channel_id;
                    } else {
                        id = messageObject.messageOwner.to_id.user_id;
                        if (id == UserConfig.getClientUserId()) {
                            id = messageObject.messageOwner.from_id;
                        }
                    }
                }
                this.avatarDrawable.setInfo(id, null, null, false);
                if (this.currentMessageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                    this.imageReceiver.setImage(this.currentMessageObject.messageOwner.action.newUserPhoto.photo_small, "50_50", this.avatarDrawable, null, false);
                } else {
                    PhotoSize photo = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, AndroidUtilities.dp(64.0f));
                    if (photo != null) {
                        this.imageReceiver.setImage(photo.location, "50_50", this.avatarDrawable, null, false);
                    } else {
                        this.imageReceiver.setImageBitmap(this.avatarDrawable);
                    }
                }
                ImageReceiver imageReceiver = this.imageReceiver;
                if (PhotoViewer.getInstance().isShowingImage(this.currentMessageObject)) {
                    z = false;
                }
                imageReceiver.setVisible(z, false);
            } else {
                this.imageReceiver.setImageBitmap((Bitmap) null);
            }
            requestLayout();
        }
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public ImageReceiver getPhotoImage() {
        return this.imageReceiver;
    }

    protected void onLongPress() {
        if (this.delegate != null) {
            this.delegate.didLongPressed(this);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean result = false;
        if (event.getAction() != 0) {
            if (event.getAction() != 2) {
                cancelCheckLongPress();
            }
            if (this.imagePressed) {
                if (event.getAction() == 1) {
                    this.imagePressed = false;
                    if (this.delegate != null) {
                        this.delegate.didClickedImage(this);
                        playSoundEffect(0);
                    }
                } else if (event.getAction() == 3) {
                    this.imagePressed = false;
                } else if (event.getAction() == 2 && !this.imageReceiver.isInsideImage(x, y)) {
                    this.imagePressed = false;
                }
            }
        } else if (this.delegate != null) {
            if (this.currentMessageObject.type == 11 && this.imageReceiver.isInsideImage(x, y)) {
                this.imagePressed = true;
                result = true;
            }
            if (result) {
                startCheckLongPress();
            }
        }
        if (!result && (event.getAction() == 0 || (this.pressedLink != null && event.getAction() == 1))) {
            if (x < ((float) this.textX) || y < ((float) this.textY) || x > ((float) (this.textX + this.textWidth)) || y > ((float) (this.textY + this.textHeight))) {
                this.pressedLink = null;
            } else {
                x -= (float) this.textXLeft;
                int line = this.textLayout.getLineForVertical((int) (y - ((float) this.textY)));
                int off = this.textLayout.getOffsetForHorizontal(line, x);
                float left = this.textLayout.getLineLeft(line);
                if (left > x || this.textLayout.getLineWidth(line) + left < x || !(this.currentMessageObject.messageText instanceof Spannable)) {
                    this.pressedLink = null;
                } else {
                    URLSpan[] link = (URLSpan[]) this.currentMessageObject.messageText.getSpans(off, off, URLSpan.class);
                    if (link.length == 0) {
                        this.pressedLink = null;
                    } else if (event.getAction() == 0) {
                        this.pressedLink = link[0];
                        result = true;
                    } else if (link[0] == this.pressedLink) {
                        if (this.delegate != null) {
                            this.delegate.needOpenUserProfile(Integer.parseInt(link[0].getURL()));
                        }
                        result = true;
                    }
                }
            }
        }
        if (result) {
            return result;
        }
        return super.onTouchEvent(event);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onMeasure(int r14, int r15) {
        /*
        r13 = this;
        r0 = r13.currentMessageObject;
        if (r0 != 0) goto L_0x0015;
    L_0x0004:
        r0 = android.view.View.MeasureSpec.getSize(r14);
        r1 = r13.textHeight;
        r2 = 1096810496; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 + r2;
        r13.setMeasuredDimension(r0, r1);
    L_0x0014:
        return;
    L_0x0015:
        r0 = 1106247680; // 0x41f00000 float:30.0 double:5.465589745E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1 = android.view.View.MeasureSpec.getSize(r14);
        r12 = java.lang.Math.max(r0, r1);
        r0 = r13.previousWidth;
        if (r12 == r0) goto L_0x00db;
    L_0x0027:
        r13.previousWidth = r12;
        r0 = 1106247680; // 0x41f00000 float:30.0 double:5.465589745E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r3 = r12 - r0;
        r0 = new android.text.StaticLayout;
        r1 = r13.currentMessageObject;
        r1 = r1.messageText;
        r2 = textPaint;
        r4 = android.text.Layout.Alignment.ALIGN_CENTER;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r6 = 0;
        r7 = 0;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        r13.textLayout = r0;
        r0 = 0;
        r13.textHeight = r0;
        r0 = 0;
        r13.textWidth = r0;
        r0 = r13.textLayout;	 Catch:{ Exception -> 0x008d }
        r11 = r0.getLineCount();	 Catch:{ Exception -> 0x008d }
        r8 = 0;
    L_0x0051:
        if (r8 >= r11) goto L_0x0093;
    L_0x0053:
        r0 = r13.textLayout;	 Catch:{ Exception -> 0x0086 }
        r10 = r0.getLineWidth(r8);	 Catch:{ Exception -> 0x0086 }
        r0 = (float) r3;	 Catch:{ Exception -> 0x0086 }
        r0 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r0 <= 0) goto L_0x005f;
    L_0x005e:
        r10 = (float) r3;	 Catch:{ Exception -> 0x0086 }
    L_0x005f:
        r0 = r13.textHeight;	 Catch:{ Exception -> 0x0086 }
        r0 = (double) r0;	 Catch:{ Exception -> 0x0086 }
        r2 = r13.textLayout;	 Catch:{ Exception -> 0x0086 }
        r2 = r2.getLineBottom(r8);	 Catch:{ Exception -> 0x0086 }
        r4 = (double) r2;	 Catch:{ Exception -> 0x0086 }
        r4 = java.lang.Math.ceil(r4);	 Catch:{ Exception -> 0x0086 }
        r0 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x0086 }
        r0 = (int) r0;	 Catch:{ Exception -> 0x0086 }
        r13.textHeight = r0;	 Catch:{ Exception -> 0x0086 }
        r0 = r13.textWidth;	 Catch:{ Exception -> 0x008d }
        r0 = (double) r0;	 Catch:{ Exception -> 0x008d }
        r4 = (double) r10;	 Catch:{ Exception -> 0x008d }
        r4 = java.lang.Math.ceil(r4);	 Catch:{ Exception -> 0x008d }
        r0 = java.lang.Math.max(r0, r4);	 Catch:{ Exception -> 0x008d }
        r0 = (int) r0;	 Catch:{ Exception -> 0x008d }
        r13.textWidth = r0;	 Catch:{ Exception -> 0x008d }
        r8 = r8 + 1;
        goto L_0x0051;
    L_0x0086:
        r9 = move-exception;
        r0 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r0, r9);	 Catch:{ Exception -> 0x008d }
        goto L_0x0014;
    L_0x008d:
        r9 = move-exception;
        r0 = "tmessages";
        org.telegram.messenger.FileLog.m13e(r0, r9);
    L_0x0093:
        r0 = r13.textWidth;
        r0 = r12 - r0;
        r0 = r0 / 2;
        r13.textX = r0;
        r0 = 1088421888; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r13.textY = r0;
        r0 = r13.textLayout;
        r0 = r0.getWidth();
        r0 = r12 - r0;
        r0 = r0 / 2;
        r13.textXLeft = r0;
        r0 = r13.currentMessageObject;
        r0 = r0.type;
        r1 = 11;
        if (r0 != r1) goto L_0x00db;
    L_0x00b7:
        r0 = r13.imageReceiver;
        r1 = 1115684864; // 0x42800000 float:64.0 double:5.51221563E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r12 - r1;
        r1 = r1 / 2;
        r2 = r13.textHeight;
        r4 = 1097859072; // 0x41700000 float:15.0 double:5.424144515E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r4 = 1115684864; // 0x42800000 float:64.0 double:5.51221563E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = 1115684864; // 0x42800000 float:64.0 double:5.51221563E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.setImageCoords(r1, r2, r4, r5);
    L_0x00db:
        r1 = r13.textHeight;
        r0 = r13.currentMessageObject;
        r0 = r0.type;
        r2 = 11;
        if (r0 != r2) goto L_0x00f4;
    L_0x00e5:
        r0 = 70;
    L_0x00e7:
        r0 = r0 + 14;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = r0 + r1;
        r13.setMeasuredDimension(r12, r0);
        goto L_0x0014;
    L_0x00f4:
        r0 = 0;
        goto L_0x00e7;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onMeasure(int, int):void");
    }

    private int findMaxWidthAroundLine(int line) {
        int a;
        int width = (int) Math.ceil((double) this.textLayout.getLineWidth(line));
        int count = this.textLayout.getLineCount();
        for (a = line + 1; a < count; a++) {
            int w = (int) Math.ceil((double) this.textLayout.getLineWidth(a));
            if (Math.abs(w - width) >= AndroidUtilities.dp(12.0f)) {
                break;
            }
            width = Math.max(w, width);
        }
        for (a = line - 1; a >= 0; a--) {
            w = (int) Math.ceil((double) this.textLayout.getLineWidth(a));
            if (Math.abs(w - width) >= AndroidUtilities.dp(12.0f)) {
                break;
            }
            width = Math.max(w, width);
        }
        return width;
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentMessageObject != null) {
            if (this.currentMessageObject.type == 11) {
                this.imageReceiver.draw(canvas);
            }
            if (this.textLayout != null) {
                int count = this.textLayout.getLineCount();
                int corner = AndroidUtilities.dp(6.0f);
                int y = AndroidUtilities.dp(7.0f);
                int previousLineBottom = 0;
                int a = 0;
                while (a < count) {
                    int dy;
                    int dx;
                    int width = findMaxWidthAroundLine(a);
                    int x = ((getMeasuredWidth() - width) / 2) - AndroidUtilities.dp(3.0f);
                    width += AndroidUtilities.dp(6.0f);
                    int lineBottom = this.textLayout.getLineBottom(a);
                    int height = lineBottom - previousLineBottom;
                    int additionalHeight = 0;
                    previousLineBottom = lineBottom;
                    boolean drawBottomCorners = a == count + -1;
                    boolean drawTopCorners = a == 0;
                    if (drawTopCorners) {
                        y -= AndroidUtilities.dp(3.0f);
                        height += AndroidUtilities.dp(3.0f);
                    }
                    if (drawBottomCorners) {
                        height += AndroidUtilities.dp(3.0f);
                    }
                    canvas.drawRect((float) x, (float) y, (float) (x + width), (float) (y + height), backPaint);
                    if (!drawBottomCorners && a + 1 < count) {
                        int nextLineWidth = findMaxWidthAroundLine(a + 1) + AndroidUtilities.dp(6.0f);
                        if ((corner * 2) + nextLineWidth < width) {
                            int nextX = (getMeasuredWidth() - nextLineWidth) / 2;
                            drawBottomCorners = true;
                            additionalHeight = AndroidUtilities.dp(3.0f);
                            canvas.drawRect((float) x, (float) (y + height), (float) nextX, (float) ((y + height) + AndroidUtilities.dp(3.0f)), backPaint);
                            canvas.drawRect((float) (nextX + nextLineWidth), (float) (y + height), (float) (x + width), (float) ((y + height) + AndroidUtilities.dp(3.0f)), backPaint);
                        } else if ((corner * 2) + width < nextLineWidth) {
                            additionalHeight = AndroidUtilities.dp(3.0f);
                            dy = (y + height) - AndroidUtilities.dp(9.0f);
                            dx = x - (corner * 2);
                            Theme.cornerInner[2].setBounds(dx, dy, dx + corner, dy + corner);
                            Theme.cornerInner[2].draw(canvas);
                            dx = (x + width) + corner;
                            Theme.cornerInner[3].setBounds(dx, dy, dx + corner, dy + corner);
                            Theme.cornerInner[3].draw(canvas);
                        } else {
                            additionalHeight = AndroidUtilities.dp(6.0f);
                        }
                    }
                    if (!drawTopCorners && a > 0) {
                        int prevLineWidth = findMaxWidthAroundLine(a - 1) + AndroidUtilities.dp(6.0f);
                        if ((corner * 2) + prevLineWidth < width) {
                            int prevX = (getMeasuredWidth() - prevLineWidth) / 2;
                            drawTopCorners = true;
                            y -= AndroidUtilities.dp(3.0f);
                            height += AndroidUtilities.dp(3.0f);
                            canvas.drawRect((float) x, (float) y, (float) prevX, (float) (AndroidUtilities.dp(3.0f) + y), backPaint);
                            canvas.drawRect((float) (prevX + prevLineWidth), (float) y, (float) (x + width), (float) (AndroidUtilities.dp(3.0f) + y), backPaint);
                        } else if ((corner * 2) + width < prevLineWidth) {
                            y -= AndroidUtilities.dp(3.0f);
                            height += AndroidUtilities.dp(3.0f);
                            dy = y + corner;
                            dx = x - (corner * 2);
                            Theme.cornerInner[0].setBounds(dx, dy, dx + corner, dy + corner);
                            Theme.cornerInner[0].draw(canvas);
                            dx = (x + width) + corner;
                            Theme.cornerInner[1].setBounds(dx, dy, dx + corner, dy + corner);
                            Theme.cornerInner[1].draw(canvas);
                        } else {
                            y -= AndroidUtilities.dp(6.0f);
                            height += AndroidUtilities.dp(6.0f);
                        }
                    }
                    canvas.drawRect((float) (x - corner), (float) (y + corner), (float) x, (float) (((y + height) + additionalHeight) - corner), backPaint);
                    canvas.drawRect((float) (x + width), (float) (y + corner), (float) ((x + width) + corner), (float) (((y + height) + additionalHeight) - corner), backPaint);
                    if (drawTopCorners) {
                        dx = x - corner;
                        Theme.cornerOuter[0].setBounds(dx, y, dx + corner, y + corner);
                        Theme.cornerOuter[0].draw(canvas);
                        dx = x + width;
                        Theme.cornerOuter[1].setBounds(dx, y, dx + corner, y + corner);
                        Theme.cornerOuter[1].draw(canvas);
                    }
                    if (drawBottomCorners) {
                        dy = ((y + height) + additionalHeight) - corner;
                        dx = x + width;
                        Theme.cornerOuter[2].setBounds(dx, dy, dx + corner, dy + corner);
                        Theme.cornerOuter[2].draw(canvas);
                        dx = x - corner;
                        Theme.cornerOuter[3].setBounds(dx, dy, dx + corner, dy + corner);
                        Theme.cornerOuter[3].draw(canvas);
                    }
                    y += height;
                    a++;
                }
                canvas.save();
                canvas.translate((float) this.textXLeft, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }
}
