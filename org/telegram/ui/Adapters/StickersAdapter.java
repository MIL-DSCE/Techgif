package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.ui.Cells.StickerCell;

public class StickersAdapter extends Adapter implements NotificationCenterDelegate {
    private StickersAdapterDelegate delegate;
    private String lastSticker;
    private Context mContext;
    private ArrayList<Long> newRecentStickers;
    private long recentLoadDate;
    private ArrayList<Document> stickers;
    private ArrayList<String> stickersToLoad;
    private boolean visible;

    /* renamed from: org.telegram.ui.Adapters.StickersAdapter.1 */
    class C09561 implements Comparator<Document> {
        C09561() {
        }

        public int compare(Document lhs, Document rhs) {
            int idx1 = StickersAdapter.this.newRecentStickers.indexOf(Long.valueOf(lhs.id));
            int idx2 = StickersAdapter.this.newRecentStickers.indexOf(Long.valueOf(rhs.id));
            if (idx1 > idx2) {
                return -1;
            }
            if (idx1 < idx2) {
                return 1;
            }
            return 0;
        }
    }

    public interface StickersAdapterDelegate {
        void needChangePanelVisibility(boolean z);
    }

    private class Holder extends ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public StickersAdapter(Context context, StickersAdapterDelegate delegate) {
        this.stickersToLoad = new ArrayList();
        this.newRecentStickers = new ArrayList();
        this.mContext = context;
        this.delegate = delegate;
        StickersQuery.checkStickers();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    public void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    public void didReceivedNotification(int id, Object... args) {
        boolean z = false;
        if ((id == NotificationCenter.FileDidLoaded || id == NotificationCenter.FileDidFailedLoad) && this.stickers != null && !this.stickers.isEmpty() && !this.stickersToLoad.isEmpty() && this.visible) {
            this.stickersToLoad.remove(args[0]);
            if (this.stickersToLoad.isEmpty()) {
                StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
                if (!(this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty())) {
                    z = true;
                }
                stickersAdapterDelegate.needChangePanelVisibility(z);
            }
        }
    }

    private boolean checkStickerFilesExistAndDownload() {
        if (this.stickers == null) {
            return false;
        }
        this.stickersToLoad.clear();
        int size = Math.min(10, this.stickers.size());
        for (int a = 0; a < size; a++) {
            Document document = (Document) this.stickers.get(a);
            if (!FileLoader.getPathToAttach(document.thumb, "webp", true).exists()) {
                this.stickersToLoad.add(FileLoader.getAttachFileName(document.thumb, "webp"));
                FileLoader.getInstance().loadFile(document.thumb.location, "webp", 0, true);
            }
        }
        return this.stickersToLoad.isEmpty();
    }

    public void loadStikersForEmoji(CharSequence emoji) {
        boolean search = emoji != null && emoji.length() > 0 && emoji.length() <= 14;
        if (search) {
            int length = emoji.length();
            int a = 0;
            while (a < length) {
                if (a < length - 1 && emoji.charAt(a) == '\ud83c' && emoji.charAt(a + 1) >= '\udffb' && emoji.charAt(a + 1) <= '\udfff') {
                    emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 2, emoji.length())});
                    break;
                }
                if (emoji.charAt(a) == '\ufe0f') {
                    emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 1, emoji.length())});
                    length--;
                }
                a++;
            }
            this.lastSticker = emoji.toString();
            HashMap<String, ArrayList<Document>> allStickers = StickersQuery.getAllStickers();
            if (allStickers != null) {
                ArrayList<Document> newStickers = (ArrayList) allStickers.get(this.lastSticker);
                if (this.stickers == null || newStickers != null) {
                    ArrayList arrayList = (newStickers == null || newStickers.isEmpty()) ? null : new ArrayList(newStickers);
                    this.stickers = arrayList;
                    if (this.stickers != null) {
                        if (Math.abs(this.recentLoadDate - System.currentTimeMillis()) > 10000) {
                            this.recentLoadDate = System.currentTimeMillis();
                            try {
                                String[] args = this.mContext.getSharedPreferences("emoji", 0).getString("stickers2", TtmlNode.ANONYMOUS_REGION_ID).split(",");
                                for (a = 0; a < args.length; a++) {
                                    if (args[a].length() != 0) {
                                        long id = Utilities.parseLong(args[a]).longValue();
                                        if (id != 0) {
                                            this.newRecentStickers.add(Long.valueOf(id));
                                        }
                                    }
                                }
                            } catch (Throwable e) {
                                FileLog.m13e("tmessages", e);
                            }
                        }
                        if (!this.newRecentStickers.isEmpty()) {
                            Collections.sort(this.stickers, new C09561());
                        }
                    }
                    checkStickerFilesExistAndDownload();
                    StickersAdapterDelegate stickersAdapterDelegate = this.delegate;
                    boolean z = (this.stickers == null || this.stickers.isEmpty() || !this.stickersToLoad.isEmpty()) ? false : true;
                    stickersAdapterDelegate.needChangePanelVisibility(z);
                    notifyDataSetChanged();
                    this.visible = true;
                } else if (this.visible) {
                    this.delegate.needChangePanelVisibility(false);
                    this.visible = false;
                }
            }
        }
        if (!search && this.visible && this.stickers != null) {
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
        }
    }

    public void clearStickers() {
        this.lastSticker = null;
        this.stickers = null;
        this.stickersToLoad.clear();
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.stickers != null ? this.stickers.size() : 0;
    }

    public Document getItem(int i) {
        return (this.stickers == null || i < 0 || i >= this.stickers.size()) ? null : (Document) this.stickers.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(new StickerCell(this.mContext));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int side = 0;
        if (i == 0) {
            if (this.stickers.size() == 1) {
                side = 2;
            } else {
                side = -1;
            }
        } else if (i == this.stickers.size() - 1) {
            side = 1;
        }
        ((StickerCell) viewHolder.itemView).setSticker((Document) this.stickers.get(i), side);
    }
}
