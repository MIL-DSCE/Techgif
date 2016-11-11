package org.telegram.messenger.query;

import android.content.Context;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_messages_allStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getAllStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_uninstallStickerSet;
import org.telegram.tgnet.TLRPC.TL_stickerPack;

public class StickersQuery {
    private static HashMap<String, ArrayList<Document>> allStickers;
    private static int loadDate;
    private static int loadHash;
    private static boolean loadingStickers;
    private static ArrayList<TL_messages_stickerSet> stickerSets;
    private static HashMap<Long, TL_messages_stickerSet> stickerSetsById;
    private static HashMap<String, TL_messages_stickerSet> stickerSetsByName;
    private static HashMap<Long, String> stickersByEmoji;
    private static HashMap<Long, Document> stickersById;
    private static boolean stickersLoaded;

    /* renamed from: org.telegram.messenger.query.StickersQuery.1 */
    static class C08361 implements Comparator<TL_messages_stickerSet> {
        final /* synthetic */ ArrayList val$order;

        C08361(ArrayList arrayList) {
            this.val$order = arrayList;
        }

        public int compare(TL_messages_stickerSet lhs, TL_messages_stickerSet rhs) {
            int index1 = this.val$order.indexOf(Long.valueOf(lhs.set.id));
            int index2 = this.val$order.indexOf(Long.valueOf(rhs.set.id));
            if (index1 > index2) {
                return 1;
            }
            if (index1 < index2) {
                return -1;
            }
            return 0;
        }
    }

    /* renamed from: org.telegram.messenger.query.StickersQuery.2 */
    static class C08372 implements Runnable {
        C08372() {
        }

        public void run() {
            Throwable e;
            Throwable th;
            ArrayList<TL_messages_stickerSet> newStickerArray = null;
            int date = 0;
            int hash = 0;
            SQLiteCursor cursor = null;
            try {
                cursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT data, date, hash FROM stickers_v2 WHERE 1", new Object[0]);
                if (cursor.next()) {
                    NativeByteBuffer data = cursor.byteBufferValue(0);
                    if (data != null) {
                        ArrayList<TL_messages_stickerSet> newStickerArray2 = new ArrayList();
                        try {
                            int count = data.readInt32(false);
                            for (int a = 0; a < count; a++) {
                                newStickerArray2.add(TL_messages_stickerSet.TLdeserialize(data, data.readInt32(false), false));
                            }
                            data.reuse();
                            newStickerArray = newStickerArray2;
                        } catch (Throwable th2) {
                            th = th2;
                            newStickerArray = newStickerArray2;
                            if (cursor != null) {
                                cursor.dispose();
                            }
                            throw th;
                        }
                    }
                    date = cursor.intValue(1);
                    hash = StickersQuery.calcStickersHash(newStickerArray);
                }
                if (cursor != null) {
                    cursor.dispose();
                }
            } catch (Throwable th3) {
                e = th3;
                FileLog.m13e("tmessages", e);
                if (cursor != null) {
                    cursor.dispose();
                }
                StickersQuery.processLoadedStickers(newStickerArray, true, date, hash);
            }
            StickersQuery.processLoadedStickers(newStickerArray, true, date, hash);
        }
    }

    /* renamed from: org.telegram.messenger.query.StickersQuery.4 */
    static class C08404 implements Runnable {
        final /* synthetic */ int val$date;
        final /* synthetic */ int val$hash;
        final /* synthetic */ ArrayList val$stickersFinal;

        C08404(ArrayList arrayList, int i, int i2) {
            this.val$stickersFinal = arrayList;
            this.val$date = i;
            this.val$hash = i2;
        }

        public void run() {
            try {
                SQLitePreparedStatement state;
                if (this.val$stickersFinal != null) {
                    int a;
                    state = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
                    state.requery();
                    int size = 4;
                    for (a = 0; a < this.val$stickersFinal.size(); a++) {
                        size += ((TL_messages_stickerSet) this.val$stickersFinal.get(a)).getObjectSize();
                    }
                    NativeByteBuffer data = new NativeByteBuffer(size);
                    data.writeInt32(this.val$stickersFinal.size());
                    for (a = 0; a < this.val$stickersFinal.size(); a++) {
                        ((TL_messages_stickerSet) this.val$stickersFinal.get(a)).serializeToStream(data);
                    }
                    state.bindInteger(1, 1);
                    state.bindByteBuffer(2, data);
                    state.bindInteger(3, this.val$date);
                    state.bindInteger(4, this.val$hash);
                    state.step();
                    data.reuse();
                    state.dispose();
                    return;
                }
                state = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
                state.requery();
                state.bindInteger(1, this.val$date);
                state.step();
                state.dispose();
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.query.StickersQuery.5 */
    static class C08415 implements Runnable {
        C08415() {
        }

        public void run() {
            StickersQuery.loadingStickers = false;
            StickersQuery.stickersLoaded = true;
        }
    }

    /* renamed from: org.telegram.messenger.query.StickersQuery.6 */
    static class C08456 implements Runnable {
        final /* synthetic */ boolean val$cache;
        final /* synthetic */ int val$date;
        final /* synthetic */ int val$hash;
        final /* synthetic */ ArrayList val$res;

        /* renamed from: org.telegram.messenger.query.StickersQuery.6.1 */
        class C08421 implements Runnable {
            C08421() {
            }

            public void run() {
                if (!(C08456.this.val$res == null || C08456.this.val$hash == 0)) {
                    StickersQuery.loadHash = C08456.this.val$hash;
                }
                StickersQuery.loadStickers(false, false);
            }
        }

        /* renamed from: org.telegram.messenger.query.StickersQuery.6.2 */
        class C08432 implements Runnable {
            final /* synthetic */ HashMap val$allStickersNew;
            final /* synthetic */ HashMap val$stickerSetsByIdNew;
            final /* synthetic */ HashMap val$stickerSetsByNameNew;
            final /* synthetic */ ArrayList val$stickerSetsNew;
            final /* synthetic */ HashMap val$stickersByEmojiNew;
            final /* synthetic */ HashMap val$stickersByIdNew;

            C08432(HashMap hashMap, HashMap hashMap2, HashMap hashMap3, ArrayList arrayList, HashMap hashMap4, HashMap hashMap5) {
                this.val$stickersByIdNew = hashMap;
                this.val$stickerSetsByIdNew = hashMap2;
                this.val$stickerSetsByNameNew = hashMap3;
                this.val$stickerSetsNew = arrayList;
                this.val$allStickersNew = hashMap4;
                this.val$stickersByEmojiNew = hashMap5;
            }

            public void run() {
                StickersQuery.stickersById = this.val$stickersByIdNew;
                StickersQuery.stickerSetsById = this.val$stickerSetsByIdNew;
                StickersQuery.stickerSetsByName = this.val$stickerSetsByNameNew;
                StickersQuery.stickerSets = this.val$stickerSetsNew;
                StickersQuery.allStickers = this.val$allStickersNew;
                StickersQuery.stickersByEmoji = this.val$stickersByEmojiNew;
                StickersQuery.loadHash = C08456.this.val$hash;
                StickersQuery.loadDate = C08456.this.val$date;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
            }
        }

        /* renamed from: org.telegram.messenger.query.StickersQuery.6.3 */
        class C08443 implements Runnable {
            C08443() {
            }

            public void run() {
                StickersQuery.loadDate = C08456.this.val$date;
            }
        }

        C08456(boolean z, ArrayList arrayList, int i, int i2) {
            this.val$cache = z;
            this.val$res = arrayList;
            this.val$date = i;
            this.val$hash = i2;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r22 = this;
            r0 = r22;
            r2 = r0.val$cache;
            if (r2 == 0) goto L_0x002b;
        L_0x0006:
            r0 = r22;
            r2 = r0.val$res;
            if (r2 == 0) goto L_0x003d;
        L_0x000c:
            r2 = java.lang.System.currentTimeMillis();
            r20 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r2 = r2 / r20;
            r0 = r22;
            r0 = r0.val$date;
            r20 = r0;
            r0 = r20;
            r0 = (long) r0;
            r20 = r0;
            r2 = r2 - r20;
            r2 = java.lang.Math.abs(r2);
            r20 = 3600; // 0xe10 float:5.045E-42 double:1.7786E-320;
            r2 = (r2 > r20 ? 1 : (r2 == r20 ? 0 : -1));
            if (r2 >= 0) goto L_0x003d;
        L_0x002b:
            r0 = r22;
            r2 = r0.val$cache;
            if (r2 != 0) goto L_0x0063;
        L_0x0031:
            r0 = r22;
            r2 = r0.val$res;
            if (r2 != 0) goto L_0x0063;
        L_0x0037:
            r0 = r22;
            r2 = r0.val$hash;
            if (r2 != 0) goto L_0x0063;
        L_0x003d:
            r20 = new org.telegram.messenger.query.StickersQuery$6$1;
            r0 = r20;
            r1 = r22;
            r0.<init>();
            r0 = r22;
            r2 = r0.val$res;
            if (r2 != 0) goto L_0x0060;
        L_0x004c:
            r0 = r22;
            r2 = r0.val$cache;
            if (r2 != 0) goto L_0x0060;
        L_0x0052:
            r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        L_0x0054:
            r0 = r20;
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r2);
            r0 = r22;
            r2 = r0.val$res;
            if (r2 != 0) goto L_0x0063;
        L_0x005f:
            return;
        L_0x0060:
            r2 = 0;
            goto L_0x0054;
        L_0x0063:
            r0 = r22;
            r2 = r0.val$res;
            if (r2 == 0) goto L_0x0197;
        L_0x0069:
            r7 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x00e8 }
            r7.<init>();	 Catch:{ Throwable -> 0x00e8 }
            r5 = new java.util.HashMap;	 Catch:{ Throwable -> 0x00e8 }
            r5.<init>();	 Catch:{ Throwable -> 0x00e8 }
            r6 = new java.util.HashMap;	 Catch:{ Throwable -> 0x00e8 }
            r6.<init>();	 Catch:{ Throwable -> 0x00e8 }
            r9 = new java.util.HashMap;	 Catch:{ Throwable -> 0x00e8 }
            r9.<init>();	 Catch:{ Throwable -> 0x00e8 }
            r4 = new java.util.HashMap;	 Catch:{ Throwable -> 0x00e8 }
            r4.<init>();	 Catch:{ Throwable -> 0x00e8 }
            r8 = new java.util.HashMap;	 Catch:{ Throwable -> 0x00e8 }
            r8.<init>();	 Catch:{ Throwable -> 0x00e8 }
            r10 = 0;
        L_0x0088:
            r0 = r22;
            r2 = r0.val$res;	 Catch:{ Throwable -> 0x00e8 }
            r2 = r2.size();	 Catch:{ Throwable -> 0x00e8 }
            if (r10 >= r2) goto L_0x017a;
        L_0x0092:
            r0 = r22;
            r2 = r0.val$res;	 Catch:{ Throwable -> 0x00e8 }
            r19 = r2.get(r10);	 Catch:{ Throwable -> 0x00e8 }
            r19 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r19;	 Catch:{ Throwable -> 0x00e8 }
            if (r19 != 0) goto L_0x00a1;
        L_0x009e:
            r10 = r10 + 1;
            goto L_0x0088;
        L_0x00a1:
            r0 = r19;
            r7.add(r0);	 Catch:{ Throwable -> 0x00e8 }
            r0 = r19;
            r2 = r0.set;	 Catch:{ Throwable -> 0x00e8 }
            r2 = r2.id;	 Catch:{ Throwable -> 0x00e8 }
            r2 = java.lang.Long.valueOf(r2);	 Catch:{ Throwable -> 0x00e8 }
            r0 = r19;
            r5.put(r2, r0);	 Catch:{ Throwable -> 0x00e8 }
            r0 = r19;
            r2 = r0.set;	 Catch:{ Throwable -> 0x00e8 }
            r2 = r2.short_name;	 Catch:{ Throwable -> 0x00e8 }
            r0 = r19;
            r6.put(r2, r0);	 Catch:{ Throwable -> 0x00e8 }
            r12 = 0;
        L_0x00c1:
            r0 = r19;
            r2 = r0.documents;	 Catch:{ Throwable -> 0x00e8 }
            r2 = r2.size();	 Catch:{ Throwable -> 0x00e8 }
            if (r12 >= r2) goto L_0x00f0;
        L_0x00cb:
            r0 = r19;
            r2 = r0.documents;	 Catch:{ Throwable -> 0x00e8 }
            r14 = r2.get(r12);	 Catch:{ Throwable -> 0x00e8 }
            r14 = (org.telegram.tgnet.TLRPC.Document) r14;	 Catch:{ Throwable -> 0x00e8 }
            if (r14 == 0) goto L_0x00db;
        L_0x00d7:
            r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty;	 Catch:{ Throwable -> 0x00e8 }
            if (r2 == 0) goto L_0x00de;
        L_0x00db:
            r12 = r12 + 1;
            goto L_0x00c1;
        L_0x00de:
            r2 = r14.id;	 Catch:{ Throwable -> 0x00e8 }
            r2 = java.lang.Long.valueOf(r2);	 Catch:{ Throwable -> 0x00e8 }
            r4.put(r2, r14);	 Catch:{ Throwable -> 0x00e8 }
            goto L_0x00db;
        L_0x00e8:
            r15 = move-exception;
            r2 = "tmessages";
            org.telegram.messenger.FileLog.m13e(r2, r15);
            goto L_0x005f;
        L_0x00f0:
            r0 = r19;
            r2 = r0.set;	 Catch:{ Throwable -> 0x00e8 }
            r2 = r2.disabled;	 Catch:{ Throwable -> 0x00e8 }
            if (r2 != 0) goto L_0x009e;
        L_0x00f8:
            r12 = 0;
        L_0x00f9:
            r0 = r19;
            r2 = r0.packs;	 Catch:{ Throwable -> 0x00e8 }
            r2 = r2.size();	 Catch:{ Throwable -> 0x00e8 }
            if (r12 >= r2) goto L_0x009e;
        L_0x0103:
            r0 = r19;
            r2 = r0.packs;	 Catch:{ Throwable -> 0x00e8 }
            r18 = r2.get(r12);	 Catch:{ Throwable -> 0x00e8 }
            r18 = (org.telegram.tgnet.TLRPC.TL_stickerPack) r18;	 Catch:{ Throwable -> 0x00e8 }
            if (r18 == 0) goto L_0x0115;
        L_0x010f:
            r0 = r18;
            r2 = r0.emoticon;	 Catch:{ Throwable -> 0x00e8 }
            if (r2 != 0) goto L_0x0118;
        L_0x0115:
            r12 = r12 + 1;
            goto L_0x00f9;
        L_0x0118:
            r0 = r18;
            r2 = r0.emoticon;	 Catch:{ Throwable -> 0x00e8 }
            r3 = "\ufe0f";
            r20 = "";
            r0 = r20;
            r2 = r2.replace(r3, r0);	 Catch:{ Throwable -> 0x00e8 }
            r0 = r18;
            r0.emoticon = r2;	 Catch:{ Throwable -> 0x00e8 }
            r0 = r18;
            r2 = r0.emoticon;	 Catch:{ Throwable -> 0x00e8 }
            r11 = r8.get(r2);	 Catch:{ Throwable -> 0x00e8 }
            r11 = (java.util.ArrayList) r11;	 Catch:{ Throwable -> 0x00e8 }
            if (r11 != 0) goto L_0x0142;
        L_0x0136:
            r11 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x00e8 }
            r11.<init>();	 Catch:{ Throwable -> 0x00e8 }
            r0 = r18;
            r2 = r0.emoticon;	 Catch:{ Throwable -> 0x00e8 }
            r8.put(r2, r11);	 Catch:{ Throwable -> 0x00e8 }
        L_0x0142:
            r13 = 0;
        L_0x0143:
            r0 = r18;
            r2 = r0.documents;	 Catch:{ Throwable -> 0x00e8 }
            r2 = r2.size();	 Catch:{ Throwable -> 0x00e8 }
            if (r13 >= r2) goto L_0x0115;
        L_0x014d:
            r0 = r18;
            r2 = r0.documents;	 Catch:{ Throwable -> 0x00e8 }
            r16 = r2.get(r13);	 Catch:{ Throwable -> 0x00e8 }
            r16 = (java.lang.Long) r16;	 Catch:{ Throwable -> 0x00e8 }
            r0 = r16;
            r2 = r9.containsKey(r0);	 Catch:{ Throwable -> 0x00e8 }
            if (r2 != 0) goto L_0x0168;
        L_0x015f:
            r0 = r18;
            r2 = r0.emoticon;	 Catch:{ Throwable -> 0x00e8 }
            r0 = r16;
            r9.put(r0, r2);	 Catch:{ Throwable -> 0x00e8 }
        L_0x0168:
            r0 = r16;
            r17 = r4.get(r0);	 Catch:{ Throwable -> 0x00e8 }
            r17 = (org.telegram.tgnet.TLRPC.Document) r17;	 Catch:{ Throwable -> 0x00e8 }
            if (r17 == 0) goto L_0x0177;
        L_0x0172:
            r0 = r17;
            r11.add(r0);	 Catch:{ Throwable -> 0x00e8 }
        L_0x0177:
            r13 = r13 + 1;
            goto L_0x0143;
        L_0x017a:
            r0 = r22;
            r2 = r0.val$cache;	 Catch:{ Throwable -> 0x00e8 }
            if (r2 != 0) goto L_0x018b;
        L_0x0180:
            r0 = r22;
            r2 = r0.val$date;	 Catch:{ Throwable -> 0x00e8 }
            r0 = r22;
            r3 = r0.val$hash;	 Catch:{ Throwable -> 0x00e8 }
            org.telegram.messenger.query.StickersQuery.putStickersToCache(r7, r2, r3);	 Catch:{ Throwable -> 0x00e8 }
        L_0x018b:
            r2 = new org.telegram.messenger.query.StickersQuery$6$2;	 Catch:{ Throwable -> 0x00e8 }
            r3 = r22;
            r2.<init>(r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x00e8 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);	 Catch:{ Throwable -> 0x00e8 }
            goto L_0x005f;
        L_0x0197:
            r0 = r22;
            r2 = r0.val$cache;
            if (r2 != 0) goto L_0x005f;
        L_0x019d:
            r2 = new org.telegram.messenger.query.StickersQuery$6$3;
            r0 = r22;
            r2.<init>();
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
            r2 = 0;
            r0 = r22;
            r3 = r0.val$date;
            r20 = 0;
            r0 = r20;
            org.telegram.messenger.query.StickersQuery.putStickersToCache(r2, r3, r0);
            goto L_0x005f;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.query.StickersQuery.6.run():void");
        }
    }

    /* renamed from: org.telegram.messenger.query.StickersQuery.3 */
    static class C17173 implements RequestDelegate {
        final /* synthetic */ TL_messages_getAllStickers val$req;

        /* renamed from: org.telegram.messenger.query.StickersQuery.3.1 */
        class C08391 implements Runnable {
            final /* synthetic */ TLObject val$response;

            /* renamed from: org.telegram.messenger.query.StickersQuery.3.1.1 */
            class C17161 implements RequestDelegate {
                final /* synthetic */ int val$index;
                final /* synthetic */ ArrayList val$newStickerArray;
                final /* synthetic */ HashMap val$newStickerSets;
                final /* synthetic */ TL_messages_allStickers val$res;
                final /* synthetic */ StickerSet val$stickerSet;

                /* renamed from: org.telegram.messenger.query.StickersQuery.3.1.1.1 */
                class C08381 implements Runnable {
                    final /* synthetic */ TLObject val$response;

                    C08381(TLObject tLObject) {
                        this.val$response = tLObject;
                    }

                    public void run() {
                        TL_messages_stickerSet res1 = this.val$response;
                        C17161.this.val$newStickerArray.set(C17161.this.val$index, res1);
                        C17161.this.val$newStickerSets.put(Long.valueOf(C17161.this.val$stickerSet.id), res1);
                        if (C17161.this.val$newStickerSets.size() == C17161.this.val$res.sets.size()) {
                            StickersQuery.processLoadedStickers(C17161.this.val$newStickerArray, false, (int) (System.currentTimeMillis() / 1000), C17161.this.val$res.hash);
                        }
                    }
                }

                C17161(ArrayList arrayList, int i, HashMap hashMap, StickerSet stickerSet, TL_messages_allStickers tL_messages_allStickers) {
                    this.val$newStickerArray = arrayList;
                    this.val$index = i;
                    this.val$newStickerSets = hashMap;
                    this.val$stickerSet = stickerSet;
                    this.val$res = tL_messages_allStickers;
                }

                public void run(TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new C08381(response));
                }
            }

            C08391(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$response instanceof TL_messages_allStickers) {
                    HashMap<Long, TL_messages_stickerSet> newStickerSets = new HashMap();
                    ArrayList<TL_messages_stickerSet> newStickerArray = new ArrayList();
                    TL_messages_allStickers res = this.val$response;
                    for (int a = 0; a < res.sets.size(); a++) {
                        StickerSet stickerSet = (StickerSet) res.sets.get(a);
                        TL_messages_stickerSet oldSet = (TL_messages_stickerSet) StickersQuery.stickerSetsById.get(Long.valueOf(stickerSet.id));
                        if (oldSet == null || oldSet.set.hash != stickerSet.hash) {
                            newStickerArray.add(null);
                            int index = a;
                            TL_messages_getStickerSet req = new TL_messages_getStickerSet();
                            req.stickerset = new TL_inputStickerSetID();
                            req.stickerset.id = stickerSet.id;
                            req.stickerset.access_hash = stickerSet.access_hash;
                            ConnectionsManager.getInstance().sendRequest(req, new C17161(newStickerArray, index, newStickerSets, stickerSet, res));
                        } else {
                            oldSet.set.disabled = stickerSet.disabled;
                            oldSet.set.installed = stickerSet.installed;
                            oldSet.set.official = stickerSet.official;
                            newStickerSets.put(Long.valueOf(oldSet.set.id), oldSet);
                            newStickerArray.add(oldSet);
                            if (newStickerSets.size() == res.sets.size()) {
                                StickersQuery.processLoadedStickers(newStickerArray, false, (int) (System.currentTimeMillis() / 1000), res.hash);
                            }
                        }
                    }
                    return;
                }
                StickersQuery.processLoadedStickers(null, false, (int) (System.currentTimeMillis() / 1000), C17173.this.val$req.hash);
            }
        }

        C17173(TL_messages_getAllStickers tL_messages_getAllStickers) {
            this.val$req = tL_messages_getAllStickers;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C08391(response));
        }
    }

    /* renamed from: org.telegram.messenger.query.StickersQuery.7 */
    static class C17187 implements RequestDelegate {

        /* renamed from: org.telegram.messenger.query.StickersQuery.7.1 */
        class C08461 implements Runnable {
            C08461() {
            }

            public void run() {
                StickersQuery.loadStickers(false, false);
            }
        }

        C17187() {
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C08461(), 1000);
        }
    }

    /* renamed from: org.telegram.messenger.query.StickersQuery.8 */
    static class C17198 implements RequestDelegate {
        final /* synthetic */ Context val$context;

        /* renamed from: org.telegram.messenger.query.StickersQuery.8.1 */
        class C08471 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C08471(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                try {
                    if (this.val$error == null) {
                        Toast.makeText(C17198.this.val$context, LocaleController.getString("StickersRemoved", C0691R.string.StickersRemoved), 0).show();
                    } else {
                        Toast.makeText(C17198.this.val$context, LocaleController.getString("ErrorOccurred", C0691R.string.ErrorOccurred), 0).show();
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                StickersQuery.loadStickers(false, true);
            }
        }

        C17198(Context context) {
            this.val$context = context;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C08471(error));
        }
    }

    static {
        stickerSets = new ArrayList();
        stickerSetsById = new HashMap();
        stickerSetsByName = new HashMap();
        stickersByEmoji = new HashMap();
        stickersById = new HashMap();
        allStickers = new HashMap();
    }

    public static void cleanup() {
        loadHash = 0;
        loadDate = 0;
        allStickers.clear();
        stickerSets.clear();
        stickersByEmoji.clear();
        stickerSetsById.clear();
        stickerSetsByName.clear();
        loadingStickers = false;
        stickersLoaded = false;
    }

    public static void checkStickers() {
        if (!loadingStickers) {
            if (!stickersLoaded || Math.abs((System.currentTimeMillis() / 1000) - ((long) loadDate)) >= 3600) {
                loadStickers(true, false);
            }
        }
    }

    public static boolean isLoadingStickers() {
        return loadingStickers;
    }

    public static Document getStickerById(long id) {
        Document document = (Document) stickersById.get(Long.valueOf(id));
        if (document == null) {
            return document;
        }
        TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) stickerSetsById.get(Long.valueOf(getStickerSetId(document)));
        if (stickerSet == null || !stickerSet.set.disabled) {
            return document;
        }
        return null;
    }

    public static TL_messages_stickerSet getStickerSetByName(String name) {
        return (TL_messages_stickerSet) stickerSetsByName.get(name);
    }

    public static TL_messages_stickerSet getStickerSetById(Long id) {
        return (TL_messages_stickerSet) stickerSetsById.get(id);
    }

    public static HashMap<String, ArrayList<Document>> getAllStickers() {
        return allStickers;
    }

    public static ArrayList<TL_messages_stickerSet> getStickerSets() {
        return stickerSets;
    }

    public static boolean isStickerPackInstalled(long id) {
        return stickerSetsById.containsKey(Long.valueOf(id));
    }

    public static boolean isStickerPackInstalled(String name) {
        return stickerSetsByName.containsKey(name);
    }

    public static String getEmojiForSticker(long id) {
        String value = (String) stickersByEmoji.get(Long.valueOf(id));
        return value != null ? value : TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static void reorderStickers(ArrayList<Long> order) {
        Collections.sort(stickerSets, new C08361(order));
        loadHash = calcStickersHash(stickerSets);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
        loadStickers(false, true);
    }

    public static void calcNewHash() {
        loadHash = calcStickersHash(stickerSets);
    }

    public static void addNewStickerSet(TL_messages_stickerSet set) {
        if (!stickerSetsById.containsKey(Long.valueOf(set.set.id)) && !stickerSetsByName.containsKey(set.set.short_name)) {
            int a;
            stickerSets.add(0, set);
            stickerSetsById.put(Long.valueOf(set.set.id), set);
            stickerSetsByName.put(set.set.short_name, set);
            for (a = 0; a < set.documents.size(); a++) {
                Document document = (Document) set.documents.get(a);
                stickersById.put(Long.valueOf(document.id), document);
            }
            for (a = 0; a < set.packs.size(); a++) {
                TL_stickerPack stickerPack = (TL_stickerPack) set.packs.get(a);
                stickerPack.emoticon = stickerPack.emoticon.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                ArrayList<Document> arrayList = (ArrayList) allStickers.get(stickerPack.emoticon);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    allStickers.put(stickerPack.emoticon, arrayList);
                }
                for (int c = 0; c < stickerPack.documents.size(); c++) {
                    Long id = (Long) stickerPack.documents.get(c);
                    if (!stickersByEmoji.containsKey(id)) {
                        stickersByEmoji.put(id, stickerPack.emoticon);
                    }
                    Document sticker = (Document) stickersById.get(id);
                    if (sticker != null) {
                        arrayList.add(sticker);
                    }
                }
            }
            loadHash = calcStickersHash(stickerSets);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
            loadStickers(false, true);
        }
    }

    public static void loadStickers(boolean cache, boolean force) {
        if (!loadingStickers) {
            loadingStickers = true;
            if (cache) {
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08372());
                return;
            }
            TL_messages_getAllStickers req = new TL_messages_getAllStickers();
            req.hash = force ? 0 : loadHash;
            ConnectionsManager.getInstance().sendRequest(req, new C17173(req));
        }
    }

    private static void putStickersToCache(ArrayList<TL_messages_stickerSet> stickers, int date, int hash) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new C08404(stickers != null ? new ArrayList(stickers) : null, date, hash));
    }

    public static String getStickerSetName(long setId) {
        TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) stickerSetsById.get(Long.valueOf(setId));
        return stickerSet != null ? stickerSet.set.short_name : null;
    }

    public static long getStickerSetId(Document document) {
        for (int a = 0; a < document.attributes.size(); a++) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeSticker) {
                if (attribute.stickerset instanceof TL_inputStickerSetID) {
                    return attribute.stickerset.id;
                }
                return -1;
            }
        }
        return -1;
    }

    private static int calcStickersHash(ArrayList<TL_messages_stickerSet> sets) {
        long acc = 0;
        for (int a = 0; a < sets.size(); a++) {
            StickerSet set = ((TL_messages_stickerSet) sets.get(a)).set;
            if (!set.disabled) {
                acc = (((20261 * acc) + 2147483648L) + ((long) set.hash)) % 2147483648L;
            }
        }
        return (int) acc;
    }

    private static void processLoadedStickers(ArrayList<TL_messages_stickerSet> res, boolean cache, int date, int hash) {
        AndroidUtilities.runOnUIThread(new C08415());
        Utilities.stageQueue.postRunnable(new C08456(cache, res, date, hash));
    }

    public static void removeStickersSet(Context context, StickerSet stickerSet, int hide) {
        boolean z = true;
        TL_inputStickerSetID stickerSetID = new TL_inputStickerSetID();
        stickerSetID.access_hash = stickerSet.access_hash;
        stickerSetID.id = stickerSet.id;
        if (hide != 0) {
            TL_messages_installStickerSet req;
            stickerSet.disabled = hide == 1;
            for (int a = 0; a < stickerSets.size(); a++) {
                TL_messages_stickerSet set = (TL_messages_stickerSet) stickerSets.get(a);
                if (set.set.id == stickerSet.id) {
                    stickerSets.remove(a);
                    if (hide != 2) {
                        for (int b = stickerSets.size() - 1; b >= 0; b--) {
                            if (!((TL_messages_stickerSet) stickerSets.get(b)).set.disabled) {
                                stickerSets.add(b + 1, set);
                                break;
                            }
                        }
                    } else {
                        stickerSets.add(0, set);
                    }
                    loadHash = calcStickersHash(stickerSets);
                    putStickersToCache(stickerSets, loadDate, loadHash);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
                    req = new TL_messages_installStickerSet();
                    req.stickerset = stickerSetID;
                    if (hide != 1) {
                        z = false;
                    }
                    req.disabled = z;
                    ConnectionsManager.getInstance().sendRequest(req, new C17187());
                    return;
                }
            }
            loadHash = calcStickersHash(stickerSets);
            putStickersToCache(stickerSets, loadDate, loadHash);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[0]);
            req = new TL_messages_installStickerSet();
            req.stickerset = stickerSetID;
            if (hide != 1) {
                z = false;
            }
            req.disabled = z;
            ConnectionsManager.getInstance().sendRequest(req, new C17187());
            return;
        }
        TL_messages_uninstallStickerSet req2 = new TL_messages_uninstallStickerSet();
        req2.stickerset = stickerSetID;
        ConnectionsManager.getInstance().sendRequest(req2, new C17198(context));
    }
}
