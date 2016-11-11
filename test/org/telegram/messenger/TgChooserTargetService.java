package org.telegram.messenger;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

@TargetApi(23)
public class TgChooserTargetService extends ChooserTargetService {
    private RectF bitmapRect;
    private Paint roundPaint;

    /* renamed from: org.telegram.messenger.TgChooserTargetService.1 */
    class C07431 implements Runnable {
        final /* synthetic */ ComponentName val$componentName;
        final /* synthetic */ Semaphore val$semaphore;
        final /* synthetic */ List val$targets;

        C07431(List list, ComponentName componentName, Semaphore semaphore) {
            this.val$targets = list;
            this.val$componentName = componentName;
            this.val$semaphore = semaphore;
        }

        public void run() {
            ArrayList<Integer> dialogs = new ArrayList();
            ArrayList<Chat> chats = new ArrayList();
            ArrayList<User> users = new ArrayList();
            try {
                ArrayList<Integer> usersToLoad = new ArrayList();
                usersToLoad.add(Integer.valueOf(UserConfig.getClientUserId()));
                ArrayList<Integer> chatsToLoad = new ArrayList();
                SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs ORDER BY date DESC LIMIT %d,%d", new Object[]{Integer.valueOf(0), Integer.valueOf(30)}), new Object[0]);
                while (cursor.next()) {
                    long id = cursor.longValue(0);
                    int lower_id = (int) id;
                    int high_id = (int) (id >> 32);
                    if (!(lower_id == 0 || high_id == 1)) {
                        if (lower_id > 0) {
                            if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                                usersToLoad.add(Integer.valueOf(lower_id));
                            }
                        } else if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                            chatsToLoad.add(Integer.valueOf(-lower_id));
                        }
                        dialogs.add(Integer.valueOf(lower_id));
                        if (dialogs.size() == 8) {
                            break;
                        }
                    }
                }
                cursor.dispose();
                if (!chatsToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                }
                if (!usersToLoad.isEmpty()) {
                    MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", usersToLoad), users);
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            for (int a = 0; a < dialogs.size(); a++) {
                Bundle extras = new Bundle();
                Icon icon = null;
                String name = null;
                int id2 = ((Integer) dialogs.get(a)).intValue();
                int b;
                if (id2 > 0) {
                    b = 0;
                    while (b < users.size()) {
                        User user = (User) users.get(b);
                        if (user.id != id2) {
                            b++;
                        } else if (!user.bot) {
                            extras.putLong("dialogId", (long) id2);
                            if (!(user.photo == null || user.photo.photo_small == null)) {
                                icon = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(user.photo.photo_small, true));
                            }
                            name = ContactsController.formatName(user.first_name, user.last_name);
                        }
                    }
                } else {
                    b = 0;
                    while (b < chats.size()) {
                        Chat chat = (Chat) chats.get(b);
                        if (chat.id != (-id2)) {
                            b++;
                        } else if (!ChatObject.isNotInChat(chat) && (!ChatObject.isChannel(chat) || chat.megagroup)) {
                            extras.putLong("dialogId", (long) id2);
                            if (!(chat.photo == null || chat.photo.photo_small == null)) {
                                icon = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(chat.photo.photo_small, true));
                            }
                            name = chat.title;
                        }
                    }
                }
                if (name != null) {
                    if (icon == null) {
                        icon = Icon.createWithResource(ApplicationLoader.applicationContext, C0691R.drawable.logo_avatar);
                    }
                    List list = this.val$targets;
                    r23.add(new ChooserTarget(name, icon, TouchHelperCallback.ALPHA_FULL, this.val$componentName, extras));
                }
            }
            this.val$semaphore.release();
        }
    }

    public List<ChooserTarget> onGetChooserTargets(ComponentName targetActivityName, IntentFilter matchedFilter) {
        List<ChooserTarget> targets = new ArrayList();
        if (UserConfig.isClientActivated() && ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("direct_share", true)) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            Semaphore semaphore = new Semaphore(0);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C07431(targets, new ComponentName(getPackageName(), LaunchActivity.class.getCanonicalName()), semaphore));
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
        return targets;
    }

    private Icon createRoundBitmap(File path) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path.toString());
            if (bitmap != null) {
                Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
                result.eraseColor(0);
                Canvas canvas = new Canvas(result);
                BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
                if (this.roundPaint == null) {
                    this.roundPaint = new Paint(1);
                    this.bitmapRect = new RectF();
                }
                this.roundPaint.setShader(shader);
                this.bitmapRect.set(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
                canvas.drawRoundRect(this.bitmapRect, (float) bitmap.getWidth(), (float) bitmap.getHeight(), this.roundPaint);
                return Icon.createWithBitmap(result);
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        return null;
    }
}
