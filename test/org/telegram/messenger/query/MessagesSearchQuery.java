package org.telegram.messenger.query;

import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.messages_Messages;

public class MessagesSearchQuery {
    private static long lastMergeDialogId;
    private static int lastReqId;
    private static int lastReturnedNum;
    private static String lastSearchQuery;
    private static int mergeReqId;
    private static int[] messagesSearchCount;
    private static boolean[] messagesSearchEndReached;
    private static int reqId;
    private static ArrayList<MessageObject> searchResultMessages;

    /* renamed from: org.telegram.messenger.query.MessagesSearchQuery.1 */
    static class C17091 implements RequestDelegate {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$direction;
        final /* synthetic */ int val$guid;
        final /* synthetic */ long val$mergeDialogId;
        final /* synthetic */ TL_messages_search val$req;

        /* renamed from: org.telegram.messenger.query.MessagesSearchQuery.1.1 */
        class C08141 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C08141(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (MessagesSearchQuery.lastMergeDialogId == C17091.this.val$mergeDialogId) {
                    MessagesSearchQuery.mergeReqId = 0;
                    if (this.val$response != null) {
                        messages_Messages res = this.val$response;
                        MessagesSearchQuery.messagesSearchEndReached[1] = res.messages.isEmpty();
                        MessagesSearchQuery.messagesSearchCount[1] = res instanceof TL_messages_messagesSlice ? res.count : res.messages.size();
                        MessagesSearchQuery.searchMessagesInChat(C17091.this.val$req.f37q, C17091.this.val$dialog_id, C17091.this.val$mergeDialogId, C17091.this.val$guid, C17091.this.val$direction, true);
                    }
                }
            }
        }

        C17091(long j, TL_messages_search tL_messages_search, long j2, int i, int i2) {
            this.val$mergeDialogId = j;
            this.val$req = tL_messages_search;
            this.val$dialog_id = j2;
            this.val$guid = i;
            this.val$direction = i2;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C08141(response));
        }
    }

    /* renamed from: org.telegram.messenger.query.MessagesSearchQuery.2 */
    static class C17102 implements RequestDelegate {
        final /* synthetic */ int val$currentReqId;
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ int val$guid;
        final /* synthetic */ long val$mergeDialogId;
        final /* synthetic */ long val$queryWithDialogFinal;
        final /* synthetic */ TL_messages_search val$req;

        /* renamed from: org.telegram.messenger.query.MessagesSearchQuery.2.1 */
        class C08151 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C08151(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                if (C17102.this.val$currentReqId == MessagesSearchQuery.lastReqId) {
                    MessagesSearchQuery.reqId = 0;
                    if (this.val$response != null) {
                        messages_Messages res = this.val$response;
                        MessagesStorage.getInstance().putUsersAndChats(res.users, res.chats, true, true);
                        MessagesController.getInstance().putUsers(res.users, false);
                        MessagesController.getInstance().putChats(res.chats, false);
                        if (C17102.this.val$req.max_id == 0 && C17102.this.val$queryWithDialogFinal == C17102.this.val$dialog_id) {
                            MessagesSearchQuery.lastReturnedNum = 0;
                            MessagesSearchQuery.searchResultMessages.clear();
                            MessagesSearchQuery.messagesSearchCount[0] = 0;
                        }
                        boolean added = false;
                        for (int a = 0; a < Math.min(res.messages.size(), 20); a++) {
                            added = true;
                            MessagesSearchQuery.searchResultMessages.add(new MessageObject((Message) res.messages.get(a), null, false));
                        }
                        MessagesSearchQuery.messagesSearchEndReached[C17102.this.val$queryWithDialogFinal == C17102.this.val$dialog_id ? 0 : 1] = res.messages.size() != 21;
                        MessagesSearchQuery.messagesSearchCount[C17102.this.val$queryWithDialogFinal == C17102.this.val$dialog_id ? 0 : 1] = res instanceof TL_messages_messagesSlice ? res.count : res.messages.size();
                        if (MessagesSearchQuery.searchResultMessages.isEmpty()) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(C17102.this.val$guid), Integer.valueOf(0), Integer.valueOf(MessagesSearchQuery.getMask()), Long.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                        } else if (added) {
                            if (MessagesSearchQuery.lastReturnedNum >= MessagesSearchQuery.searchResultMessages.size()) {
                                MessagesSearchQuery.lastReturnedNum = MessagesSearchQuery.searchResultMessages.size() - 1;
                            }
                            MessageObject messageObject = (MessageObject) MessagesSearchQuery.searchResultMessages.get(MessagesSearchQuery.lastReturnedNum);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(C17102.this.val$guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(MessagesSearchQuery.getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(MessagesSearchQuery.lastReturnedNum), Integer.valueOf(MessagesSearchQuery.messagesSearchCount[0] + MessagesSearchQuery.messagesSearchCount[1]));
                        }
                        if (C17102.this.val$queryWithDialogFinal == C17102.this.val$dialog_id && MessagesSearchQuery.messagesSearchEndReached[0] && C17102.this.val$mergeDialogId != 0 && !MessagesSearchQuery.messagesSearchEndReached[1]) {
                            MessagesSearchQuery.searchMessagesInChat(MessagesSearchQuery.lastSearchQuery, C17102.this.val$dialog_id, C17102.this.val$mergeDialogId, C17102.this.val$guid, 0, true);
                        }
                    }
                }
            }
        }

        C17102(int i, TL_messages_search tL_messages_search, long j, long j2, int i2, long j3) {
            this.val$currentReqId = i;
            this.val$req = tL_messages_search;
            this.val$queryWithDialogFinal = j;
            this.val$dialog_id = j2;
            this.val$guid = i2;
            this.val$mergeDialogId = j3;
        }

        public void run(TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new C08151(response));
        }
    }

    static {
        messagesSearchCount = new int[]{0, 0};
        messagesSearchEndReached = new boolean[]{false, false};
        searchResultMessages = new ArrayList();
    }

    private static int getMask() {
        int mask = 0;
        if (!(lastReturnedNum >= searchResultMessages.size() - 1 && messagesSearchEndReached[0] && messagesSearchEndReached[1])) {
            mask = 0 | 1;
        }
        if (lastReturnedNum > 0) {
            return mask | 2;
        }
        return mask;
    }

    public static void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction) {
        searchMessagesInChat(query, dialog_id, mergeDialogId, guid, direction, false);
    }

    private static void searchMessagesInChat(String query, long dialog_id, long mergeDialogId, int guid, int direction, boolean internal) {
        TL_messages_search req;
        int max_id = 0;
        long queryWithDialog = dialog_id;
        boolean firstQuery = !internal;
        if (reqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(reqId, true);
            reqId = 0;
        }
        if (mergeReqId != 0) {
            ConnectionsManager.getInstance().cancelRequest(mergeReqId, true);
            mergeReqId = 0;
        }
        if (query == null || query.length() == 0) {
            if (!searchResultMessages.isEmpty()) {
                MessageObject messageObject;
                if (direction == 1) {
                    lastReturnedNum++;
                    if (lastReturnedNum < searchResultMessages.size()) {
                        messageObject = (MessageObject) searchResultMessages.get(lastReturnedNum);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(lastReturnedNum), Integer.valueOf(messagesSearchCount[0] + messagesSearchCount[1]));
                        return;
                    } else if ((messagesSearchEndReached[0] && mergeDialogId == 0) || messagesSearchEndReached[1]) {
                        lastReturnedNum--;
                        return;
                    } else {
                        firstQuery = false;
                        query = lastSearchQuery;
                        messageObject = (MessageObject) searchResultMessages.get(searchResultMessages.size() - 1);
                        if (messageObject.getDialogId() != dialog_id || messagesSearchEndReached[0]) {
                            if (messageObject.getDialogId() == mergeDialogId) {
                                max_id = messageObject.getId();
                            }
                            queryWithDialog = mergeDialogId;
                            messagesSearchEndReached[1] = false;
                        } else {
                            max_id = messageObject.getId();
                            queryWithDialog = dialog_id;
                        }
                    }
                } else if (direction == 2) {
                    lastReturnedNum--;
                    if (lastReturnedNum < 0) {
                        lastReturnedNum = 0;
                        return;
                    }
                    if (lastReturnedNum >= searchResultMessages.size()) {
                        lastReturnedNum = searchResultMessages.size() - 1;
                    }
                    messageObject = (MessageObject) searchResultMessages.get(lastReturnedNum);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, Integer.valueOf(guid), Integer.valueOf(messageObject.getId()), Integer.valueOf(getMask()), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(lastReturnedNum), Integer.valueOf(messagesSearchCount[0] + messagesSearchCount[1]));
                    return;
                } else {
                    return;
                }
            }
            return;
        }
        if (!(!messagesSearchEndReached[0] || messagesSearchEndReached[1] || mergeDialogId == 0)) {
            queryWithDialog = mergeDialogId;
        }
        if (queryWithDialog == dialog_id && firstQuery) {
            if (mergeDialogId != 0) {
                InputPeer inputPeer = MessagesController.getInputPeer((int) mergeDialogId);
                if (inputPeer != null) {
                    req = new TL_messages_search();
                    req.peer = inputPeer;
                    lastMergeDialogId = mergeDialogId;
                    req.limit = 1;
                    req.f37q = query;
                    req.filter = new TL_inputMessagesFilterEmpty();
                    mergeReqId = ConnectionsManager.getInstance().sendRequest(req, new C17091(mergeDialogId, req, dialog_id, guid, direction), 2);
                    return;
                }
                return;
            }
            lastMergeDialogId = 0;
            messagesSearchEndReached[1] = true;
            messagesSearchCount[1] = 0;
        }
        req = new TL_messages_search();
        req.peer = MessagesController.getInputPeer((int) queryWithDialog);
        if (req.peer != null) {
            req.limit = 21;
            req.f37q = query;
            req.max_id = max_id;
            req.filter = new TL_inputMessagesFilterEmpty();
            int currentReqId = lastReqId + 1;
            lastReqId = currentReqId;
            lastSearchQuery = query;
            reqId = ConnectionsManager.getInstance().sendRequest(req, new C17102(currentReqId, req, queryWithDialog, dialog_id, guid, mergeDialogId), 2);
        }
    }

    public static String getLastSearchQuery() {
        return lastSearchQuery;
    }
}
