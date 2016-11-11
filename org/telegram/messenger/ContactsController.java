package org.telegram.messenger;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.PrivacyRule;
import org.telegram.tgnet.TLRPC.TL_account_getAccountTTL;
import org.telegram.tgnet.TLRPC.TL_account_getPrivacy;
import org.telegram.tgnet.TLRPC.TL_account_privacyRules;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.TL_contactStatus;
import org.telegram.tgnet.TLRPC.TL_contacts_contactsNotModified;
import org.telegram.tgnet.TLRPC.TL_contacts_deleteContacts;
import org.telegram.tgnet.TLRPC.TL_contacts_getContacts;
import org.telegram.tgnet.TLRPC.TL_contacts_getStatuses;
import org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC.TL_contacts_importedContacts;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getInviteText;
import org.telegram.tgnet.TLRPC.TL_help_inviteText;
import org.telegram.tgnet.TLRPC.TL_importedContact;
import org.telegram.tgnet.TLRPC.TL_inputPhoneContact;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.contacts_Contacts;

public class ContactsController {
    private static volatile ContactsController Instance;
    private static final Object loadContactsSync;
    private int completedRequestsCount;
    public ArrayList<TL_contact> contacts;
    public HashMap<Integer, Contact> contactsBook;
    private boolean contactsBookLoaded;
    public HashMap<String, Contact> contactsBookSPhones;
    public HashMap<String, TL_contact> contactsByPhone;
    public SparseArray<TL_contact> contactsDict;
    public boolean contactsLoaded;
    private boolean contactsSyncInProgress;
    private Account currentAccount;
    private ArrayList<Integer> delayedContactsUpdate;
    private int deleteAccountTTL;
    private ArrayList<PrivacyRule> groupPrivacyRules;
    private boolean ignoreChanges;
    private String inviteText;
    private String lastContactsVersions;
    private boolean loadingContacts;
    private int loadingDeleteInfo;
    private int loadingGroupInfo;
    private int loadingLastSeenInfo;
    private final Object observerLock;
    public ArrayList<Contact> phoneBookContacts;
    private ArrayList<PrivacyRule> privacyRules;
    private String[] projectionNames;
    private String[] projectionPhones;
    private HashMap<String, String> sectionsToReplace;
    public ArrayList<String> sortedUsersMutualSectionsArray;
    public ArrayList<String> sortedUsersSectionsArray;
    private boolean updatingInviteText;
    public HashMap<String, ArrayList<TL_contact>> usersMutualSectionsDict;
    public HashMap<String, ArrayList<TL_contact>> usersSectionsDict;

    /* renamed from: org.telegram.messenger.ContactsController.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ ArrayList val$contactsArray;

        AnonymousClass12(ArrayList arrayList) {
            this.val$contactsArray = arrayList;
        }

        public void run() {
            ContactsController.this.performWriteContactsToPhoneBookInternal(this.val$contactsArray);
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.13 */
    class AnonymousClass13 implements Runnable {
        final /* synthetic */ Integer val$uid;

        AnonymousClass13(Integer num) {
            this.val$uid = num;
        }

        public void run() {
            ContactsController.this.deleteContactFromPhoneBook(this.val$uid.intValue());
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.15 */
    class AnonymousClass15 implements Runnable {
        final /* synthetic */ ArrayList val$contactsToDelete;
        final /* synthetic */ ArrayList val$newContacts;

        AnonymousClass15(ArrayList arrayList, ArrayList arrayList2) {
            this.val$newContacts = arrayList;
            this.val$contactsToDelete = arrayList2;
        }

        public void run() {
            int a;
            boolean z;
            for (a = 0; a < this.val$newContacts.size(); a++) {
                TL_contact contact = (TL_contact) this.val$newContacts.get(a);
                if (ContactsController.this.contactsDict.get(contact.user_id) == null) {
                    ContactsController.this.contacts.add(contact);
                    ContactsController.this.contactsDict.put(contact.user_id, contact);
                }
            }
            for (a = 0; a < this.val$contactsToDelete.size(); a++) {
                Integer uid = (Integer) this.val$contactsToDelete.get(a);
                contact = (TL_contact) ContactsController.this.contactsDict.get(uid.intValue());
                if (contact != null) {
                    ContactsController.this.contacts.remove(contact);
                    ContactsController.this.contactsDict.remove(uid.intValue());
                }
            }
            if (!this.val$newContacts.isEmpty()) {
                ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                ContactsController.this.performWriteContactsToPhoneBook();
            }
            ContactsController.this.performSyncPhoneBook(ContactsController.this.getContactsCopy(ContactsController.this.contactsBook), false, false, false, false);
            ContactsController contactsController = ContactsController.this;
            if (this.val$newContacts.isEmpty()) {
                z = false;
            } else {
                z = true;
            }
            contactsController.buildContactsSectionsArrays(z);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.16 */
    class AnonymousClass16 implements Runnable {
        final /* synthetic */ String val$contactId;

        AnonymousClass16(String str) {
            this.val$contactId = str;
        }

        public void run() {
            Uri uri = Uri.parse(this.val$contactId);
            ContentValues values = new ContentValues();
            values.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
            ApplicationLoader.applicationContext.getContentResolver().update(uri, values, null, null);
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.1 */
    class C04371 implements Runnable {
        C04371() {
        }

        public void run() {
            ContactsController.this.completedRequestsCount = 0;
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.3 */
    class C04423 implements Runnable {
        C04423() {
        }

        public void run() {
            if (ContactsController.this.checkContactsInternal()) {
                FileLog.m11e("tmessages", "detected contacts change");
                ContactsController.getInstance().performSyncPhoneBook(ContactsController.getInstance().getContactsCopy(ContactsController.getInstance().contactsBook), true, false, true, false);
            }
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.4 */
    class C04434 implements Runnable {
        C04434() {
        }

        public void run() {
            ContactsController.getInstance().performSyncPhoneBook(new HashMap(), true, true, true, true);
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.5 */
    class C04445 implements Runnable {
        C04445() {
        }

        public void run() {
            if (!ContactsController.this.contacts.isEmpty() || ContactsController.this.contactsLoaded) {
                synchronized (ContactsController.loadContactsSync) {
                    ContactsController.this.loadingContacts = false;
                }
                return;
            }
            ContactsController.this.loadContacts(true, false);
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.6 */
    class C04506 implements Runnable {
        final /* synthetic */ HashMap val$contactHashMap;
        final /* synthetic */ boolean val$first;
        final /* synthetic */ boolean val$force;
        final /* synthetic */ boolean val$request;
        final /* synthetic */ boolean val$schedule;

        /* renamed from: org.telegram.messenger.ContactsController.6.1 */
        class C04451 implements Runnable {
            C04451() {
            }

            public void run() {
                ArrayList<User> toDelete = new ArrayList();
                if (!(C04506.this.val$contactHashMap == null || C04506.this.val$contactHashMap.isEmpty())) {
                    try {
                        int a;
                        User user;
                        HashMap<String, User> contactsPhonesShort = new HashMap();
                        for (a = 0; a < ContactsController.this.contacts.size(); a++) {
                            user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) ContactsController.this.contacts.get(a)).user_id));
                            if (!(user == null || user.phone == null || user.phone.length() == 0)) {
                                contactsPhonesShort.put(user.phone, user);
                            }
                        }
                        int removed = 0;
                        for (Entry<Integer, Contact> entry : C04506.this.val$contactHashMap.entrySet()) {
                            Contact contact = (Contact) entry.getValue();
                            boolean was = false;
                            a = 0;
                            while (a < contact.shortPhones.size()) {
                                user = (User) contactsPhonesShort.get((String) contact.shortPhones.get(a));
                                if (user != null) {
                                    was = true;
                                    toDelete.add(user);
                                    contact.shortPhones.remove(a);
                                    a--;
                                }
                                a++;
                            }
                            if (!was || contact.shortPhones.size() == 0) {
                                removed++;
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                    }
                }
                if (!toDelete.isEmpty()) {
                    ContactsController.this.deleteContact(toDelete);
                }
            }
        }

        /* renamed from: org.telegram.messenger.ContactsController.6.3 */
        class C04483 implements Runnable {
            final /* synthetic */ HashMap val$contactsBookShort;
            final /* synthetic */ HashMap val$contactsMap;

            /* renamed from: org.telegram.messenger.ContactsController.6.3.1 */
            class C04471 implements Runnable {
                C04471() {
                }

                public void run() {
                    ContactsController.this.updateUnregisteredContacts(ContactsController.this.contacts);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                }
            }

            C04483(HashMap hashMap, HashMap hashMap2) {
                this.val$contactsBookShort = hashMap;
                this.val$contactsMap = hashMap2;
            }

            public void run() {
                ContactsController.this.contactsBookSPhones = this.val$contactsBookShort;
                ContactsController.this.contactsBook = this.val$contactsMap;
                ContactsController.this.contactsSyncInProgress = false;
                ContactsController.this.contactsBookLoaded = true;
                if (C04506.this.val$first) {
                    ContactsController.this.contactsLoaded = true;
                }
                if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded) {
                    ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                    ContactsController.this.delayedContactsUpdate.clear();
                }
                AndroidUtilities.runOnUIThread(new C04471());
            }
        }

        /* renamed from: org.telegram.messenger.ContactsController.6.4 */
        class C04494 implements Runnable {
            final /* synthetic */ HashMap val$contactsBookShort;
            final /* synthetic */ HashMap val$contactsMap;

            C04494(HashMap hashMap, HashMap hashMap2) {
                this.val$contactsBookShort = hashMap;
                this.val$contactsMap = hashMap2;
            }

            public void run() {
                ContactsController.this.contactsBookSPhones = this.val$contactsBookShort;
                ContactsController.this.contactsBook = this.val$contactsMap;
                ContactsController.this.contactsSyncInProgress = false;
                ContactsController.this.contactsBookLoaded = true;
                if (C04506.this.val$first) {
                    ContactsController.this.contactsLoaded = true;
                }
                if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded && ContactsController.this.contactsBookLoaded) {
                    ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                    ContactsController.this.delayedContactsUpdate.clear();
                }
            }
        }

        /* renamed from: org.telegram.messenger.ContactsController.6.2 */
        class C16762 implements RequestDelegate {
            final /* synthetic */ HashMap val$contactsBookShort;
            final /* synthetic */ HashMap val$contactsMap;
            final /* synthetic */ HashMap val$contactsMapToSave;
            final /* synthetic */ int val$count;

            /* renamed from: org.telegram.messenger.ContactsController.6.2.1 */
            class C04461 implements Runnable {
                C04461() {
                }

                public void run() {
                    ContactsController.this.contactsBookSPhones = C16762.this.val$contactsBookShort;
                    ContactsController.this.contactsBook = C16762.this.val$contactsMap;
                    ContactsController.this.contactsSyncInProgress = false;
                    ContactsController.this.contactsBookLoaded = true;
                    if (C04506.this.val$first) {
                        ContactsController.this.contactsLoaded = true;
                    }
                    if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsLoaded) {
                        ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                        ContactsController.this.delayedContactsUpdate.clear();
                    }
                }
            }

            C16762(HashMap hashMap, int i, HashMap hashMap2, HashMap hashMap3) {
                this.val$contactsMapToSave = hashMap;
                this.val$count = i;
                this.val$contactsBookShort = hashMap2;
                this.val$contactsMap = hashMap3;
            }

            public void run(TLObject response, TL_error error) {
                ContactsController.this.completedRequestsCount = ContactsController.this.completedRequestsCount + 1;
                if (error == null) {
                    int a;
                    FileLog.m11e("tmessages", "contacts imported");
                    TL_contacts_importedContacts res = (TL_contacts_importedContacts) response;
                    if (!res.retry_contacts.isEmpty()) {
                        for (a = 0; a < res.retry_contacts.size(); a++) {
                            this.val$contactsMapToSave.remove(Integer.valueOf((int) ((Long) res.retry_contacts.get(a)).longValue()));
                        }
                    }
                    if (ContactsController.this.completedRequestsCount == this.val$count && !this.val$contactsMapToSave.isEmpty()) {
                        MessagesStorage.getInstance().putCachedPhoneBook(this.val$contactsMapToSave);
                    }
                    MessagesStorage.getInstance().putUsersAndChats(res.users, null, true, true);
                    ArrayList<TL_contact> cArr = new ArrayList();
                    for (a = 0; a < res.imported.size(); a++) {
                        TL_contact contact = new TL_contact();
                        contact.user_id = ((TL_importedContact) res.imported.get(a)).user_id;
                        cArr.add(contact);
                    }
                    ContactsController.this.processLoadedContacts(cArr, res.users, 2);
                } else {
                    FileLog.m11e("tmessages", "import contacts error " + error.text);
                }
                if (ContactsController.this.completedRequestsCount == this.val$count) {
                    Utilities.stageQueue.postRunnable(new C04461());
                }
            }
        }

        C04506(HashMap hashMap, boolean z, boolean z2, boolean z3, boolean z4) {
            this.val$contactHashMap = hashMap;
            this.val$schedule = z;
            this.val$request = z2;
            this.val$first = z3;
            this.val$force = z4;
        }

        public void run() {
            int a;
            HashMap<String, Contact> contactShortHashMap = new HashMap();
            for (Entry<Integer, Contact> entry : this.val$contactHashMap.entrySet()) {
                Contact c = (Contact) entry.getValue();
                for (a = 0; a < c.shortPhones.size(); a++) {
                    contactShortHashMap.put(c.shortPhones.get(a), c);
                }
            }
            FileLog.m11e("tmessages", "start read contacts from phone");
            if (!this.val$schedule) {
                ContactsController.this.checkContactsInternal();
            }
            HashMap<Integer, Contact> contactsMap = ContactsController.this.readContactsFromPhoneBook();
            HashMap<String, Contact> contactsBookShort = new HashMap();
            int oldCount = this.val$contactHashMap.size();
            ArrayList<TL_inputPhoneContact> toImport = new ArrayList();
            Contact value;
            TL_inputPhoneContact imp;
            TL_contact contact;
            User user;
            if (!this.val$contactHashMap.isEmpty()) {
                for (Entry<Integer, Contact> pair : contactsMap.entrySet()) {
                    Integer id = (Integer) pair.getKey();
                    value = (Contact) pair.getValue();
                    Contact existing = (Contact) this.val$contactHashMap.get(id);
                    if (existing == null) {
                        for (a = 0; a < value.shortPhones.size(); a++) {
                            c = (Contact) contactShortHashMap.get(value.shortPhones.get(a));
                            if (c != null) {
                                existing = c;
                                id = Integer.valueOf(existing.id);
                                break;
                            }
                        }
                    }
                    boolean nameChanged = existing != null && ((TextUtils.isEmpty(value.first_name) && !existing.first_name.equals(value.first_name)) || !(TextUtils.isEmpty(value.last_name) || existing.last_name.equals(value.last_name)));
                    String sphone;
                    int index;
                    if (existing == null || nameChanged) {
                        for (a = 0; a < value.phones.size(); a++) {
                            sphone = (String) value.shortPhones.get(a);
                            contactsBookShort.put(sphone, value);
                            if (existing != null) {
                                index = existing.shortPhones.indexOf(sphone);
                                if (index != -1) {
                                    Integer deleted = (Integer) existing.phoneDeleted.get(index);
                                    value.phoneDeleted.set(a, deleted);
                                    if (deleted.intValue() == 1) {
                                    }
                                }
                            }
                            if (this.val$request && (nameChanged || !ContactsController.this.contactsByPhone.containsKey(sphone))) {
                                imp = new TL_inputPhoneContact();
                                imp.client_id = (long) value.id;
                                imp.client_id |= ((long) a) << 32;
                                imp.first_name = value.first_name;
                                imp.last_name = value.last_name;
                                imp.phone = (String) value.phones.get(a);
                                toImport.add(imp);
                            }
                        }
                        if (existing != null) {
                            this.val$contactHashMap.remove(id);
                        }
                    } else {
                        for (a = 0; a < value.phones.size(); a++) {
                            sphone = (String) value.shortPhones.get(a);
                            contactsBookShort.put(sphone, value);
                            index = existing.shortPhones.indexOf(sphone);
                            if (index != -1) {
                                value.phoneDeleted.set(a, existing.phoneDeleted.get(index));
                                existing.phones.remove(index);
                                existing.shortPhones.remove(index);
                                existing.phoneDeleted.remove(index);
                                existing.phoneTypes.remove(index);
                            } else if (this.val$request) {
                                contact = (TL_contact) ContactsController.this.contactsByPhone.get(sphone);
                                if (contact != null) {
                                    user = MessagesController.getInstance().getUser(Integer.valueOf(contact.user_id));
                                    if (user != null) {
                                        if (TextUtils.isEmpty(user.first_name)) {
                                            if (TextUtils.isEmpty(user.last_name)) {
                                                if (TextUtils.isEmpty(value.first_name) && TextUtils.isEmpty(value.last_name)) {
                                                }
                                            }
                                        }
                                    }
                                }
                                imp = new TL_inputPhoneContact();
                                imp.client_id = (long) value.id;
                                imp.client_id |= ((long) a) << 32;
                                imp.first_name = value.first_name;
                                imp.last_name = value.last_name;
                                imp.phone = (String) value.phones.get(a);
                                toImport.add(imp);
                            }
                        }
                        if (existing.phones.isEmpty()) {
                            this.val$contactHashMap.remove(id);
                        }
                    }
                }
                if (!this.val$first && this.val$contactHashMap.isEmpty() && toImport.isEmpty() && oldCount == contactsMap.size()) {
                    FileLog.m11e("tmessages", "contacts not changed!");
                    return;
                } else if (!(!this.val$request || this.val$contactHashMap.isEmpty() || contactsMap.isEmpty())) {
                    if (toImport.isEmpty()) {
                        MessagesStorage.getInstance().putCachedPhoneBook(contactsMap);
                    }
                    if (!(true || this.val$contactHashMap.isEmpty())) {
                        AndroidUtilities.runOnUIThread(new C04451());
                    }
                }
            } else if (this.val$request) {
                for (Entry<Integer, Contact> pair2 : contactsMap.entrySet()) {
                    value = (Contact) pair2.getValue();
                    int id2 = ((Integer) pair2.getKey()).intValue();
                    for (a = 0; a < value.phones.size(); a++) {
                        if (!this.val$force) {
                            contact = (TL_contact) ContactsController.this.contactsByPhone.get((String) value.shortPhones.get(a));
                            if (contact != null) {
                                user = MessagesController.getInstance().getUser(Integer.valueOf(contact.user_id));
                                if (user != null) {
                                    if (TextUtils.isEmpty(user.first_name)) {
                                        if (TextUtils.isEmpty(user.last_name)) {
                                            if (TextUtils.isEmpty(value.first_name) && TextUtils.isEmpty(value.last_name)) {
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        imp = new TL_inputPhoneContact();
                        imp.client_id = (long) id2;
                        imp.client_id |= ((long) a) << 32;
                        imp.first_name = value.first_name;
                        imp.last_name = value.last_name;
                        imp.phone = (String) value.phones.get(a);
                        toImport.add(imp);
                    }
                }
            }
            FileLog.m11e("tmessages", "done processing contacts");
            if (!this.val$request) {
                Utilities.stageQueue.postRunnable(new C04494(contactsBookShort, contactsMap));
                if (!contactsMap.isEmpty()) {
                    MessagesStorage.getInstance().putCachedPhoneBook(contactsMap);
                }
            } else if (toImport.isEmpty()) {
                Utilities.stageQueue.postRunnable(new C04483(contactsBookShort, contactsMap));
            } else {
                HashMap<Integer, Contact> contactsMapToSave = new HashMap(contactsMap);
                ContactsController.this.completedRequestsCount = 0;
                int count = (int) Math.ceil((double) (((float) toImport.size()) / 500.0f));
                for (a = 0; a < count; a++) {
                    ArrayList<TL_inputPhoneContact> finalToImport = new ArrayList();
                    finalToImport.addAll(toImport.subList(a * 500, Math.min((a + 1) * 500, toImport.size())));
                    TLObject req = new TL_contacts_importContacts();
                    req.contacts = finalToImport;
                    req.replace = false;
                    ConnectionsManager.getInstance().sendRequest(req, new C16762(contactsMapToSave, count, contactsBookShort, contactsMap), 6);
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.8 */
    class C04608 implements Runnable {
        final /* synthetic */ ArrayList val$contactsArr;
        final /* synthetic */ int val$from;
        final /* synthetic */ ArrayList val$usersArr;

        /* renamed from: org.telegram.messenger.ContactsController.8.1 */
        class C04591 implements Runnable {
            final /* synthetic */ boolean val$isEmpty;
            final /* synthetic */ HashMap val$usersDict;

            /* renamed from: org.telegram.messenger.ContactsController.8.1.1 */
            class C04521 implements Comparator<TL_contact> {
                C04521() {
                }

                public int compare(TL_contact tl_contact, TL_contact tl_contact2) {
                    if (tl_contact.user_id > tl_contact2.user_id) {
                        return 1;
                    }
                    if (tl_contact.user_id < tl_contact2.user_id) {
                        return -1;
                    }
                    return 0;
                }
            }

            /* renamed from: org.telegram.messenger.ContactsController.8.1.2 */
            class C04532 implements Comparator<TL_contact> {
                C04532() {
                }

                public int compare(TL_contact tl_contact, TL_contact tl_contact2) {
                    return UserObject.getFirstName((User) C04591.this.val$usersDict.get(Integer.valueOf(tl_contact.user_id))).compareTo(UserObject.getFirstName((User) C04591.this.val$usersDict.get(Integer.valueOf(tl_contact2.user_id))));
                }
            }

            /* renamed from: org.telegram.messenger.ContactsController.8.1.3 */
            class C04543 implements Comparator<String> {
                C04543() {
                }

                public int compare(String s, String s2) {
                    char cv1 = s.charAt(0);
                    char cv2 = s2.charAt(0);
                    if (cv1 == '#') {
                        return 1;
                    }
                    if (cv2 == '#') {
                        return -1;
                    }
                    return s.compareTo(s2);
                }
            }

            /* renamed from: org.telegram.messenger.ContactsController.8.1.4 */
            class C04554 implements Comparator<String> {
                C04554() {
                }

                public int compare(String s, String s2) {
                    char cv1 = s.charAt(0);
                    char cv2 = s2.charAt(0);
                    if (cv1 == '#') {
                        return 1;
                    }
                    if (cv2 == '#') {
                        return -1;
                    }
                    return s.compareTo(s2);
                }
            }

            /* renamed from: org.telegram.messenger.ContactsController.8.1.5 */
            class C04565 implements Runnable {
                final /* synthetic */ SparseArray val$contactsDictionary;
                final /* synthetic */ HashMap val$sectionsDict;
                final /* synthetic */ HashMap val$sectionsDictMutual;
                final /* synthetic */ ArrayList val$sortedSectionsArray;
                final /* synthetic */ ArrayList val$sortedSectionsArrayMutual;

                C04565(SparseArray sparseArray, HashMap hashMap, HashMap hashMap2, ArrayList arrayList, ArrayList arrayList2) {
                    this.val$contactsDictionary = sparseArray;
                    this.val$sectionsDict = hashMap;
                    this.val$sectionsDictMutual = hashMap2;
                    this.val$sortedSectionsArray = arrayList;
                    this.val$sortedSectionsArrayMutual = arrayList2;
                }

                public void run() {
                    ContactsController.this.contacts = C04608.this.val$contactsArr;
                    ContactsController.this.contactsDict = this.val$contactsDictionary;
                    ContactsController.this.usersSectionsDict = this.val$sectionsDict;
                    ContactsController.this.usersMutualSectionsDict = this.val$sectionsDictMutual;
                    ContactsController.this.sortedUsersSectionsArray = this.val$sortedSectionsArray;
                    ContactsController.this.sortedUsersMutualSectionsArray = this.val$sortedSectionsArrayMutual;
                    if (C04608.this.val$from != 2) {
                        synchronized (ContactsController.loadContactsSync) {
                            ContactsController.this.loadingContacts = false;
                        }
                    }
                    ContactsController.this.performWriteContactsToPhoneBook();
                    ContactsController.this.updateUnregisteredContacts(C04608.this.val$contactsArr);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                    if (C04608.this.val$from == 1 || C04591.this.val$isEmpty) {
                        ContactsController.this.reloadContactsStatusesMaybe();
                    } else {
                        ContactsController.this.saveContactsLoadTime();
                    }
                }
            }

            /* renamed from: org.telegram.messenger.ContactsController.8.1.6 */
            class C04586 implements Runnable {
                final /* synthetic */ HashMap val$contactsByPhonesDictFinal;

                /* renamed from: org.telegram.messenger.ContactsController.8.1.6.1 */
                class C04571 implements Runnable {
                    C04571() {
                    }

                    public void run() {
                        ContactsController.this.contactsByPhone = C04586.this.val$contactsByPhonesDictFinal;
                    }
                }

                C04586(HashMap hashMap) {
                    this.val$contactsByPhonesDictFinal = hashMap;
                }

                public void run() {
                    Utilities.globalQueue.postRunnable(new C04571());
                    if (!ContactsController.this.contactsSyncInProgress) {
                        ContactsController.this.contactsSyncInProgress = true;
                        MessagesStorage.getInstance().getCachedPhoneBook();
                    }
                }
            }

            C04591(HashMap hashMap, boolean z) {
                this.val$usersDict = hashMap;
                this.val$isEmpty = z;
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r26 = this;
                r4 = "tmessages";
                r5 = "done loading contacts";
                org.telegram.messenger.FileLog.m11e(r4, r5);
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$from;
                r5 = 1;
                if (r4 != r5) goto L_0x0047;
            L_0x0010:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$contactsArr;
                r4 = r4.isEmpty();
                if (r4 != 0) goto L_0x0038;
            L_0x001c:
                r4 = java.lang.System.currentTimeMillis();
                r24 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
                r4 = r4 / r24;
                r23 = org.telegram.messenger.UserConfig.lastContactsSyncTime;
                r0 = r23;
                r0 = (long) r0;
                r24 = r0;
                r4 = r4 - r24;
                r4 = java.lang.Math.abs(r4);
                r24 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
                r4 = (r4 > r24 ? 1 : (r4 == r24 ? 0 : -1));
                if (r4 < 0) goto L_0x0047;
            L_0x0038:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = org.telegram.messenger.ContactsController.this;
                r5 = 0;
                r23 = 1;
                r0 = r23;
                r4.loadContacts(r5, r0);
            L_0x0046:
                return;
            L_0x0047:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$from;
                if (r4 != 0) goto L_0x005e;
            L_0x004f:
                r4 = java.lang.System.currentTimeMillis();
                r24 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
                r4 = r4 / r24;
                r4 = (int) r4;
                org.telegram.messenger.UserConfig.lastContactsSyncTime = r4;
                r4 = 0;
                org.telegram.messenger.UserConfig.saveConfig(r4);
            L_0x005e:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$contactsArr;
                r17 = r4.iterator();
            L_0x0068:
                r4 = r17.hasNext();
                if (r4 == 0) goto L_0x00a2;
            L_0x006e:
                r14 = r17.next();
                r14 = (org.telegram.tgnet.TLRPC.TL_contact) r14;
                r0 = r26;
                r4 = r0.val$usersDict;
                r5 = r14.user_id;
                r5 = java.lang.Integer.valueOf(r5);
                r4 = r4.get(r5);
                if (r4 != 0) goto L_0x0068;
            L_0x0084:
                r4 = r14.user_id;
                r5 = org.telegram.messenger.UserConfig.getClientUserId();
                if (r4 == r5) goto L_0x0068;
            L_0x008c:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = org.telegram.messenger.ContactsController.this;
                r5 = 0;
                r23 = 1;
                r0 = r23;
                r4.loadContacts(r5, r0);
                r4 = "tmessages";
                r5 = "contacts are broken, load from server";
                org.telegram.messenger.FileLog.m11e(r4, r5);
                goto L_0x0046;
            L_0x00a2:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$from;
                r5 = 1;
                if (r4 == r5) goto L_0x0132;
            L_0x00ab:
                r4 = org.telegram.messenger.MessagesStorage.getInstance();
                r0 = r26;
                r5 = org.telegram.messenger.ContactsController.C04608.this;
                r5 = r5.val$usersArr;
                r23 = 0;
                r24 = 1;
                r25 = 1;
                r0 = r23;
                r1 = r24;
                r2 = r25;
                r4.putUsersAndChats(r5, r0, r1, r2);
                r5 = org.telegram.messenger.MessagesStorage.getInstance();
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r0 = r4.val$contactsArr;
                r23 = r0;
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$from;
                r24 = 2;
                r0 = r24;
                if (r4 == r0) goto L_0x0122;
            L_0x00dc:
                r4 = 1;
            L_0x00dd:
                r0 = r23;
                r5.putContacts(r0, r4);
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$contactsArr;
                r5 = new org.telegram.messenger.ContactsController$8$1$1;
                r0 = r26;
                r5.<init>();
                java.util.Collections.sort(r4, r5);
                r18 = new java.lang.StringBuilder;
                r18.<init>();
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$contactsArr;
                r17 = r4.iterator();
            L_0x0101:
                r4 = r17.hasNext();
                if (r4 == 0) goto L_0x0124;
            L_0x0107:
                r12 = r17.next();
                r12 = (org.telegram.tgnet.TLRPC.TL_contact) r12;
                r4 = r18.length();
                if (r4 == 0) goto L_0x011a;
            L_0x0113:
                r4 = ",";
                r0 = r18;
                r0.append(r4);
            L_0x011a:
                r4 = r12.user_id;
                r0 = r18;
                r0.append(r4);
                goto L_0x0101;
            L_0x0122:
                r4 = 0;
                goto L_0x00dd;
            L_0x0124:
                r4 = r18.toString();
                r4 = org.telegram.messenger.Utilities.MD5(r4);
                org.telegram.messenger.UserConfig.contactsHash = r4;
                r4 = 0;
                org.telegram.messenger.UserConfig.saveConfig(r4);
            L_0x0132:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$contactsArr;
                r5 = new org.telegram.messenger.ContactsController$8$1$2;
                r0 = r26;
                r5.<init>();
                java.util.Collections.sort(r4, r5);
                r6 = new android.util.SparseArray;
                r6.<init>();
                r7 = new java.util.HashMap;
                r7.<init>();
                r8 = new java.util.HashMap;
                r8.<init>();
                r9 = new java.util.ArrayList;
                r9.<init>();
                r10 = new java.util.ArrayList;
                r10.<init>();
                r15 = 0;
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = org.telegram.messenger.ContactsController.this;
                r4 = r4.contactsBookLoaded;
                if (r4 != 0) goto L_0x016d;
            L_0x0168:
                r15 = new java.util.HashMap;
                r15.<init>();
            L_0x016d:
                r16 = r15;
                r11 = 0;
            L_0x0170:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$contactsArr;
                r4 = r4.size();
                if (r11 >= r4) goto L_0x022d;
            L_0x017c:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = r4.val$contactsArr;
                r22 = r4.get(r11);
                r22 = (org.telegram.tgnet.TLRPC.TL_contact) r22;
                r0 = r26;
                r4 = r0.val$usersDict;
                r0 = r22;
                r5 = r0.user_id;
                r5 = java.lang.Integer.valueOf(r5);
                r21 = r4.get(r5);
                r21 = (org.telegram.tgnet.TLRPC.User) r21;
                if (r21 != 0) goto L_0x019f;
            L_0x019c:
                r11 = r11 + 1;
                goto L_0x0170;
            L_0x019f:
                r0 = r22;
                r4 = r0.user_id;
                r0 = r22;
                r6.put(r4, r0);
                if (r15 == 0) goto L_0x01b3;
            L_0x01aa:
                r0 = r21;
                r4 = r0.phone;
                r0 = r22;
                r15.put(r4, r0);
            L_0x01b3:
                r19 = org.telegram.messenger.UserObject.getFirstName(r21);
                r4 = r19.length();
                r5 = 1;
                if (r4 <= r5) goto L_0x01c6;
            L_0x01be:
                r4 = 0;
                r5 = 1;
                r0 = r19;
                r19 = r0.substring(r4, r5);
            L_0x01c6:
                r4 = r19.length();
                if (r4 != 0) goto L_0x0228;
            L_0x01cc:
                r19 = "#";
            L_0x01ce:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = org.telegram.messenger.ContactsController.this;
                r4 = r4.sectionsToReplace;
                r0 = r19;
                r20 = r4.get(r0);
                r20 = (java.lang.String) r20;
                if (r20 == 0) goto L_0x01e4;
            L_0x01e2:
                r19 = r20;
            L_0x01e4:
                r0 = r19;
                r13 = r7.get(r0);
                r13 = (java.util.ArrayList) r13;
                if (r13 != 0) goto L_0x01fd;
            L_0x01ee:
                r13 = new java.util.ArrayList;
                r13.<init>();
                r0 = r19;
                r7.put(r0, r13);
                r0 = r19;
                r9.add(r0);
            L_0x01fd:
                r0 = r22;
                r13.add(r0);
                r0 = r21;
                r4 = r0.mutual_contact;
                if (r4 == 0) goto L_0x019c;
            L_0x0208:
                r0 = r19;
                r13 = r8.get(r0);
                r13 = (java.util.ArrayList) r13;
                if (r13 != 0) goto L_0x0221;
            L_0x0212:
                r13 = new java.util.ArrayList;
                r13.<init>();
                r0 = r19;
                r8.put(r0, r13);
                r0 = r19;
                r10.add(r0);
            L_0x0221:
                r0 = r22;
                r13.add(r0);
                goto L_0x019c;
            L_0x0228:
                r19 = r19.toUpperCase();
                goto L_0x01ce;
            L_0x022d:
                r4 = new org.telegram.messenger.ContactsController$8$1$3;
                r0 = r26;
                r4.<init>();
                java.util.Collections.sort(r9, r4);
                r4 = new org.telegram.messenger.ContactsController$8$1$4;
                r0 = r26;
                r4.<init>();
                java.util.Collections.sort(r10, r4);
                r4 = new org.telegram.messenger.ContactsController$8$1$5;
                r5 = r26;
                r4.<init>(r6, r7, r8, r9, r10);
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = org.telegram.messenger.ContactsController.this;
                r4 = r4.delayedContactsUpdate;
                r4 = r4.isEmpty();
                if (r4 != 0) goto L_0x029d;
            L_0x025b:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = org.telegram.messenger.ContactsController.this;
                r4 = r4.contactsLoaded;
                if (r4 == 0) goto L_0x029d;
            L_0x0265:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = org.telegram.messenger.ContactsController.this;
                r4 = r4.contactsBookLoaded;
                if (r4 == 0) goto L_0x029d;
            L_0x0271:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = org.telegram.messenger.ContactsController.this;
                r0 = r26;
                r5 = org.telegram.messenger.ContactsController.C04608.this;
                r5 = org.telegram.messenger.ContactsController.this;
                r5 = r5.delayedContactsUpdate;
                r23 = 0;
                r24 = 0;
                r25 = 0;
                r0 = r23;
                r1 = r24;
                r2 = r25;
                r4.applyContactsUpdates(r5, r0, r1, r2);
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = org.telegram.messenger.ContactsController.this;
                r4 = r4.delayedContactsUpdate;
                r4.clear();
            L_0x029d:
                if (r16 == 0) goto L_0x02ad;
            L_0x029f:
                r4 = new org.telegram.messenger.ContactsController$8$1$6;
                r0 = r26;
                r1 = r16;
                r4.<init>(r1);
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
                goto L_0x0046;
            L_0x02ad:
                r0 = r26;
                r4 = org.telegram.messenger.ContactsController.C04608.this;
                r4 = org.telegram.messenger.ContactsController.this;
                r5 = 1;
                r4.contactsLoaded = r5;
                goto L_0x0046;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsController.8.1.run():void");
            }
        }

        C04608(ArrayList arrayList, int i, ArrayList arrayList2) {
            this.val$usersArr = arrayList;
            this.val$from = i;
            this.val$contactsArr = arrayList2;
        }

        public void run() {
            int a;
            boolean z = true;
            MessagesController instance = MessagesController.getInstance();
            ArrayList arrayList = this.val$usersArr;
            if (this.val$from != 1) {
                z = false;
            }
            instance.putUsers(arrayList, z);
            HashMap<Integer, User> usersDict = new HashMap();
            boolean isEmpty = this.val$contactsArr.isEmpty();
            if (!ContactsController.this.contacts.isEmpty()) {
                a = 0;
                while (a < this.val$contactsArr.size()) {
                    if (ContactsController.this.contactsDict.get(((TL_contact) this.val$contactsArr.get(a)).user_id) != null) {
                        this.val$contactsArr.remove(a);
                        a--;
                    }
                    a++;
                }
                this.val$contactsArr.addAll(ContactsController.this.contacts);
            }
            for (a = 0; a < this.val$contactsArr.size(); a++) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) this.val$contactsArr.get(a)).user_id));
                if (user != null) {
                    usersDict.put(Integer.valueOf(user.id), user);
                }
            }
            Utilities.stageQueue.postRunnable(new C04591(usersDict, isEmpty));
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.9 */
    class C04619 implements Comparator<Contact> {
        C04619() {
        }

        public int compare(Contact contact, Contact contact2) {
            String toComapre1 = contact.first_name;
            if (toComapre1.length() == 0) {
                toComapre1 = contact.last_name;
            }
            String toComapre2 = contact2.first_name;
            if (toComapre2.length() == 0) {
                toComapre2 = contact2.last_name;
            }
            return toComapre1.compareTo(toComapre2);
        }
    }

    public static class Contact {
        public String first_name;
        public int id;
        public String last_name;
        public ArrayList<Integer> phoneDeleted;
        public ArrayList<String> phoneTypes;
        public ArrayList<String> phones;
        public ArrayList<String> shortPhones;

        public Contact() {
            this.phones = new ArrayList();
            this.phoneTypes = new ArrayList();
            this.shortPhones = new ArrayList();
            this.phoneDeleted = new ArrayList();
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.18 */
    class AnonymousClass18 implements RequestDelegate {
        final /* synthetic */ ArrayList val$uids;
        final /* synthetic */ ArrayList val$users;

        /* renamed from: org.telegram.messenger.ContactsController.18.1 */
        class C04341 implements Runnable {
            C04341() {
            }

            public void run() {
                Iterator i$ = AnonymousClass18.this.val$users.iterator();
                while (i$.hasNext()) {
                    ContactsController.this.deleteContactFromPhoneBook(((User) i$.next()).id);
                }
            }
        }

        /* renamed from: org.telegram.messenger.ContactsController.18.2 */
        class C04352 implements Runnable {
            C04352() {
            }

            public void run() {
                boolean remove = false;
                Iterator i$ = AnonymousClass18.this.val$users.iterator();
                while (i$.hasNext()) {
                    User user = (User) i$.next();
                    TL_contact contact = (TL_contact) ContactsController.this.contactsDict.get(user.id);
                    if (contact != null) {
                        remove = true;
                        ContactsController.this.contacts.remove(contact);
                        ContactsController.this.contactsDict.remove(user.id);
                    }
                }
                if (remove) {
                    ContactsController.this.buildContactsSectionsArrays(false);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
            }
        }

        AnonymousClass18(ArrayList arrayList, ArrayList arrayList2) {
            this.val$uids = arrayList;
            this.val$users = arrayList2;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                MessagesStorage.getInstance().deleteContacts(this.val$uids);
                Utilities.phoneBookQueue.postRunnable(new C04341());
                Iterator i$ = this.val$users.iterator();
                while (i$.hasNext()) {
                    User user = (User) i$.next();
                    if (user.phone != null && user.phone.length() > 0) {
                        CharSequence name = UserObject.getUserName(user);
                        MessagesStorage.getInstance().applyPhoneBookUpdates(user.phone, TtmlNode.ANONYMOUS_REGION_ID);
                        Contact contact = (Contact) ContactsController.this.contactsBookSPhones.get(user.phone);
                        if (contact != null) {
                            int index = contact.shortPhones.indexOf(user.phone);
                            if (index != -1) {
                                contact.phoneDeleted.set(index, Integer.valueOf(1));
                            }
                        }
                    }
                }
                AndroidUtilities.runOnUIThread(new C04352());
            }
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.19 */
    class AnonymousClass19 implements RequestDelegate {
        final /* synthetic */ Editor val$editor;

        /* renamed from: org.telegram.messenger.ContactsController.19.1 */
        class C04361 implements Runnable {
            final /* synthetic */ TLObject val$response;

            C04361(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                AnonymousClass19.this.val$editor.remove("needGetStatuses").commit();
                Vector vector = this.val$response;
                if (!vector.objects.isEmpty()) {
                    ArrayList<User> dbUsersStatus = new ArrayList();
                    Iterator i$ = vector.objects.iterator();
                    while (i$.hasNext()) {
                        TL_contactStatus object = i$.next();
                        User toDbUser = new User();
                        TL_contactStatus status = object;
                        if (status != null) {
                            if (status.status instanceof TL_userStatusRecently) {
                                status.status.expires = -100;
                            } else if (status.status instanceof TL_userStatusLastWeek) {
                                status.status.expires = -101;
                            } else if (status.status instanceof TL_userStatusLastMonth) {
                                status.status.expires = -102;
                            }
                            User user = MessagesController.getInstance().getUser(Integer.valueOf(status.user_id));
                            if (user != null) {
                                user.status = status.status;
                            }
                            toDbUser.status = status.status;
                            dbUsersStatus.add(toDbUser);
                        }
                    }
                    MessagesStorage.getInstance().updateUsers(dbUsersStatus, true, true, true);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
            }
        }

        AnonymousClass19(Editor editor) {
            this.val$editor = editor;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                AndroidUtilities.runOnUIThread(new C04361(response));
            }
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.2 */
    class C16752 implements RequestDelegate {

        /* renamed from: org.telegram.messenger.ContactsController.2.1 */
        class C04381 implements Runnable {
            final /* synthetic */ TL_help_inviteText val$res;

            C04381(TL_help_inviteText tL_help_inviteText) {
                this.val$res = tL_help_inviteText;
            }

            public void run() {
                ContactsController.this.updatingInviteText = false;
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                editor.putString("invitetext", this.val$res.message);
                editor.putInt("invitetexttime", (int) (System.currentTimeMillis() / 1000));
                editor.commit();
            }
        }

        C16752() {
        }

        public void run(TLObject response, TL_error error) {
            if (response != null) {
                TL_help_inviteText res = (TL_help_inviteText) response;
                if (res.message.length() != 0) {
                    AndroidUtilities.runOnUIThread(new C04381(res));
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.ContactsController.7 */
    class C16777 implements RequestDelegate {

        /* renamed from: org.telegram.messenger.ContactsController.7.1 */
        class C04511 implements Runnable {
            C04511() {
            }

            public void run() {
                synchronized (ContactsController.loadContactsSync) {
                    ContactsController.this.loadingContacts = false;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
            }
        }

        C16777() {
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                contacts_Contacts res = (contacts_Contacts) response;
                if (res instanceof TL_contacts_contactsNotModified) {
                    ContactsController.this.contactsLoaded = true;
                    if (!ContactsController.this.delayedContactsUpdate.isEmpty() && ContactsController.this.contactsBookLoaded) {
                        ContactsController.this.applyContactsUpdates(ContactsController.this.delayedContactsUpdate, null, null, null);
                        ContactsController.this.delayedContactsUpdate.clear();
                    }
                    AndroidUtilities.runOnUIThread(new C04511());
                    FileLog.m11e("tmessages", "load contacts don't change");
                    return;
                }
                ContactsController.this.processLoadedContacts(res.contacts, res.users, 0);
            }
        }
    }

    static {
        loadContactsSync = new Object();
        Instance = null;
    }

    public static ContactsController getInstance() {
        ContactsController localInstance = Instance;
        if (localInstance == null) {
            synchronized (ContactsController.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        ContactsController localInstance2 = new ContactsController();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public ContactsController() {
        this.loadingContacts = false;
        this.ignoreChanges = false;
        this.contactsSyncInProgress = false;
        this.observerLock = new Object();
        this.contactsLoaded = false;
        this.contactsBookLoaded = false;
        this.lastContactsVersions = TtmlNode.ANONYMOUS_REGION_ID;
        this.delayedContactsUpdate = new ArrayList();
        this.updatingInviteText = false;
        this.sectionsToReplace = new HashMap();
        this.loadingDeleteInfo = 0;
        this.loadingLastSeenInfo = 0;
        this.loadingGroupInfo = 0;
        this.privacyRules = null;
        this.groupPrivacyRules = null;
        this.projectionPhones = new String[]{"contact_id", "data1", "data2", "data3"};
        this.projectionNames = new String[]{"contact_id", "data2", "data3", "display_name", "data5"};
        this.contactsBook = new HashMap();
        this.contactsBookSPhones = new HashMap();
        this.phoneBookContacts = new ArrayList();
        this.contacts = new ArrayList();
        this.contactsDict = new SparseArray();
        this.usersSectionsDict = new HashMap();
        this.sortedUsersSectionsArray = new ArrayList();
        this.usersMutualSectionsDict = new HashMap();
        this.sortedUsersMutualSectionsArray = new ArrayList();
        this.contactsByPhone = new HashMap();
        if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("needGetStatuses", false)) {
            reloadContactsStatuses();
        }
        this.sectionsToReplace.put("\u00c0", "A");
        this.sectionsToReplace.put("\u00c1", "A");
        this.sectionsToReplace.put("\u00c4", "A");
        this.sectionsToReplace.put("\u00d9", "U");
        this.sectionsToReplace.put("\u00da", "U");
        this.sectionsToReplace.put("\u00dc", "U");
        this.sectionsToReplace.put("\u00cc", "I");
        this.sectionsToReplace.put("\u00cd", "I");
        this.sectionsToReplace.put("\u00cf", "I");
        this.sectionsToReplace.put("\u00c8", "E");
        this.sectionsToReplace.put("\u00c9", "E");
        this.sectionsToReplace.put("\u00ca", "E");
        this.sectionsToReplace.put("\u00cb", "E");
        this.sectionsToReplace.put("\u00d2", "O");
        this.sectionsToReplace.put("\u00d3", "O");
        this.sectionsToReplace.put("\u00d6", "O");
        this.sectionsToReplace.put("\u00c7", "C");
        this.sectionsToReplace.put("\u00d1", "N");
        this.sectionsToReplace.put("\u0178", "Y");
        this.sectionsToReplace.put("\u00dd", "Y");
        this.sectionsToReplace.put("\u0162", "Y");
    }

    public void cleanup() {
        this.contactsBook.clear();
        this.contactsBookSPhones.clear();
        this.phoneBookContacts.clear();
        this.contacts.clear();
        this.contactsDict.clear();
        this.usersSectionsDict.clear();
        this.usersMutualSectionsDict.clear();
        this.sortedUsersSectionsArray.clear();
        this.sortedUsersMutualSectionsArray.clear();
        this.delayedContactsUpdate.clear();
        this.contactsByPhone.clear();
        this.loadingContacts = false;
        this.contactsSyncInProgress = false;
        this.contactsLoaded = false;
        this.contactsBookLoaded = false;
        this.lastContactsVersions = TtmlNode.ANONYMOUS_REGION_ID;
        this.loadingDeleteInfo = 0;
        this.deleteAccountTTL = 0;
        this.loadingLastSeenInfo = 0;
        this.loadingGroupInfo = 0;
        Utilities.globalQueue.postRunnable(new C04371());
        this.privacyRules = null;
    }

    public void checkInviteText() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        this.inviteText = preferences.getString("invitetext", null);
        int time = preferences.getInt("invitetexttime", 0);
        if (!this.updatingInviteText) {
            if (this.inviteText == null || 86400 + time < ((int) (System.currentTimeMillis() / 1000))) {
                this.updatingInviteText = true;
                ConnectionsManager.getInstance().sendRequest(new TL_help_getInviteText(), new C16752(), 2);
            }
        }
    }

    public String getInviteText() {
        return LocaleController.formatString("InviteText", C0691R.string.InviteText, "https://play.google.com/store/apps/details?id=" + ApplicationLoader.applicationContext.getPackageName());
    }

    public void checkAppAccount() {
        Account[] accounts;
        int a;
        AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
        try {
            accounts = am.getAccountsByType("org.telegram.account");
            if (accounts != null && accounts.length > 0) {
                for (Account removeAccount : accounts) {
                    am.removeAccount(removeAccount, null, null);
                }
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        accounts = am.getAccountsByType("org.telegram.messenger");
        boolean recreateAccount = false;
        if (UserConfig.isClientActivated()) {
            if (accounts.length == 1) {
                Account acc = accounts[0];
                if (acc.name.equals(TtmlNode.ANONYMOUS_REGION_ID + UserConfig.getClientUserId())) {
                    this.currentAccount = acc;
                } else {
                    recreateAccount = true;
                }
            } else {
                recreateAccount = true;
            }
            readContacts();
        } else if (accounts.length > 0) {
            recreateAccount = true;
        }
        if (recreateAccount) {
            a = 0;
            while (a < accounts.length) {
                try {
                    am.removeAccount(accounts[a], null, null);
                    a++;
                } catch (Throwable e2) {
                    FileLog.m13e("tmessages", e2);
                }
            }
            if (UserConfig.isClientActivated()) {
                try {
                    this.currentAccount = new Account(TtmlNode.ANONYMOUS_REGION_ID + UserConfig.getClientUserId(), "org.telegram.messenger");
                    am.addAccountExplicitly(this.currentAccount, TtmlNode.ANONYMOUS_REGION_ID, null);
                } catch (Throwable e22) {
                    FileLog.m13e("tmessages", e22);
                }
            }
        }
    }

    public void deleteAllAppAccounts() {
        try {
            AccountManager am = AccountManager.get(ApplicationLoader.applicationContext);
            Account[] accounts = am.getAccountsByType("org.telegram.messenger");
            for (Account removeAccount : accounts) {
                am.removeAccount(removeAccount, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkContacts() {
        Utilities.globalQueue.postRunnable(new C04423());
    }

    public void forceImportContacts() {
        Utilities.globalQueue.postRunnable(new C04434());
    }

    private boolean checkContactsInternal() {
        boolean reload = false;
        try {
            if (!hasContactsPermission()) {
                return false;
            }
            ContentResolver cr = ApplicationLoader.applicationContext.getContentResolver();
            Cursor pCur = null;
            try {
                pCur = cr.query(RawContacts.CONTENT_URI, new String[]{"version"}, null, null, null);
                if (pCur != null) {
                    StringBuilder currentVersion = new StringBuilder();
                    while (pCur.moveToNext()) {
                        currentVersion.append(pCur.getString(pCur.getColumnIndex("version")));
                    }
                    String newContactsVersion = currentVersion.toString();
                    if (!(this.lastContactsVersions.length() == 0 || this.lastContactsVersions.equals(newContactsVersion))) {
                        reload = true;
                    }
                    this.lastContactsVersions = newContactsVersion;
                }
                if (pCur != null) {
                    pCur.close();
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
                if (pCur != null) {
                    pCur.close();
                }
            } catch (Throwable th) {
                if (pCur != null) {
                    pCur.close();
                }
            }
            return reload;
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
        }
    }

    public void readContacts() {
        synchronized (loadContactsSync) {
            if (this.loadingContacts) {
                return;
            }
            this.loadingContacts = true;
            Utilities.stageQueue.postRunnable(new C04445());
        }
    }

    private HashMap<Integer, Contact> readContactsFromPhoneBook() {
        HashMap<Integer, Contact> contactsMap = new HashMap();
        try {
            if (hasContactsPermission()) {
                Contact contact;
                ContentResolver cr = ApplicationLoader.applicationContext.getContentResolver();
                HashMap<String, Contact> shortContacts = new HashMap();
                ArrayList<Integer> idsArr = new ArrayList();
                Cursor pCur = cr.query(Phone.CONTENT_URI, this.projectionPhones, null, null, null);
                if (pCur != null) {
                    if (pCur.getCount() > 0) {
                        while (pCur.moveToNext()) {
                            String number = pCur.getString(1);
                            if (!(number == null || number.length() == 0)) {
                                number = PhoneFormat.stripExceptNumbers(number, true);
                                if (number.length() != 0) {
                                    String shortNumber = number;
                                    if (number.startsWith("+")) {
                                        shortNumber = number.substring(1);
                                    }
                                    if (shortContacts.containsKey(shortNumber)) {
                                        continue;
                                    } else {
                                        Integer id = Integer.valueOf(pCur.getInt(0));
                                        if (!idsArr.contains(id)) {
                                            idsArr.add(id);
                                        }
                                        int type = pCur.getInt(2);
                                        contact = (Contact) contactsMap.get(id);
                                        if (contact == null) {
                                            contact = new Contact();
                                            contact.first_name = TtmlNode.ANONYMOUS_REGION_ID;
                                            contact.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                            contact.id = id.intValue();
                                            contactsMap.put(id, contact);
                                        }
                                        contact.shortPhones.add(shortNumber);
                                        contact.phones.add(number);
                                        contact.phoneDeleted.add(Integer.valueOf(0));
                                        if (type == 0) {
                                            contact.phoneTypes.add(pCur.getString(3));
                                        } else if (type == 1) {
                                            contact.phoneTypes.add(LocaleController.getString("PhoneHome", C0691R.string.PhoneHome));
                                        } else if (type == 2) {
                                            contact.phoneTypes.add(LocaleController.getString("PhoneMobile", C0691R.string.PhoneMobile));
                                        } else if (type == 3) {
                                            contact.phoneTypes.add(LocaleController.getString("PhoneWork", C0691R.string.PhoneWork));
                                        } else if (type == 12) {
                                            contact.phoneTypes.add(LocaleController.getString("PhoneMain", C0691R.string.PhoneMain));
                                        } else {
                                            contact.phoneTypes.add(LocaleController.getString("PhoneOther", C0691R.string.PhoneOther));
                                        }
                                        shortContacts.put(shortNumber, contact);
                                    }
                                } else {
                                    continue;
                                }
                            }
                        }
                    }
                    pCur.close();
                }
                pCur = cr.query(Data.CONTENT_URI, this.projectionNames, "contact_id IN (" + TextUtils.join(",", idsArr) + ") AND " + "mimetype" + " = '" + "vnd.android.cursor.item/name" + "'", null, null);
                if (pCur != null && pCur.getCount() > 0) {
                    while (pCur.moveToNext()) {
                        int id2 = pCur.getInt(0);
                        String fname = pCur.getString(1);
                        String sname = pCur.getString(2);
                        String sname2 = pCur.getString(3);
                        String mname = pCur.getString(4);
                        contact = (Contact) contactsMap.get(Integer.valueOf(id2));
                        if (contact != null && contact.first_name.length() == 0 && contact.last_name.length() == 0) {
                            contact.first_name = fname;
                            contact.last_name = sname;
                            if (contact.first_name == null) {
                                contact.first_name = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            if (!(mname == null || mname.length() == 0)) {
                                if (contact.first_name.length() != 0) {
                                    contact.first_name += " " + mname;
                                } else {
                                    contact.first_name = mname;
                                }
                            }
                            if (contact.last_name == null) {
                                contact.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            if (contact.last_name.length() == 0 && contact.first_name.length() == 0 && sname2 != null && sname2.length() != 0) {
                                contact.first_name = sname2;
                            }
                        }
                    }
                    pCur.close();
                }
                try {
                    pCur = cr.query(RawContacts.CONTENT_URI, new String[]{"display_name", "sync1", "contact_id"}, "account_type = 'com.whatsapp'", null, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            String phone = pCur.getString(1);
                            if (!(phone == null || phone.length() == 0)) {
                                boolean withPlus = phone.startsWith("+");
                                phone = Utilities.parseIntToString(phone);
                                if (!(phone == null || phone.length() == 0)) {
                                    String shortPhone = phone;
                                    if (!withPlus) {
                                        phone = "+" + phone;
                                    }
                                    if (!shortContacts.containsKey(shortPhone)) {
                                        String name = pCur.getString(0);
                                        if (!TextUtils.isEmpty(name)) {
                                            contact = new Contact();
                                            contact.first_name = name;
                                            contact.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                            contact.id = pCur.getInt(2);
                                            contactsMap.put(Integer.valueOf(contact.id), contact);
                                            contact.phoneDeleted.add(Integer.valueOf(0));
                                            contact.shortPhones.add(shortPhone);
                                            contact.phones.add(phone);
                                            contact.phoneTypes.add(LocaleController.getString("PhoneMobile", C0691R.string.PhoneMobile));
                                            shortContacts.put(shortPhone, contact);
                                        }
                                    }
                                }
                            }
                        }
                        pCur.close();
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
            contactsMap.clear();
        }
        return contactsMap;
    }

    public HashMap<Integer, Contact> getContactsCopy(HashMap<Integer, Contact> original) {
        HashMap<Integer, Contact> ret = new HashMap();
        for (Entry<Integer, Contact> entry : original.entrySet()) {
            Contact copyContact = new Contact();
            Contact originalContact = (Contact) entry.getValue();
            copyContact.phoneDeleted.addAll(originalContact.phoneDeleted);
            copyContact.phones.addAll(originalContact.phones);
            copyContact.phoneTypes.addAll(originalContact.phoneTypes);
            copyContact.shortPhones.addAll(originalContact.shortPhones);
            copyContact.first_name = originalContact.first_name;
            copyContact.last_name = originalContact.last_name;
            copyContact.id = originalContact.id;
            ret.put(Integer.valueOf(copyContact.id), copyContact);
        }
        return ret;
    }

    protected void performSyncPhoneBook(HashMap<Integer, Contact> contactHashMap, boolean request, boolean first, boolean schedule, boolean force) {
        if (first || this.contactsBookLoaded) {
            Utilities.globalQueue.postRunnable(new C04506(contactHashMap, schedule, request, first, force));
        }
    }

    public boolean isLoadingContacts() {
        boolean z;
        synchronized (loadContactsSync) {
            z = this.loadingContacts;
        }
        return z;
    }

    public void loadContacts(boolean fromCache, boolean cacheEmpty) {
        synchronized (loadContactsSync) {
            this.loadingContacts = true;
        }
        if (fromCache) {
            FileLog.m11e("tmessages", "load contacts from cache");
            MessagesStorage.getInstance().getContacts();
            return;
        }
        FileLog.m11e("tmessages", "load contacts from server");
        TL_contacts_getContacts req = new TL_contacts_getContacts();
        req.hash = cacheEmpty ? TtmlNode.ANONYMOUS_REGION_ID : UserConfig.contactsHash;
        ConnectionsManager.getInstance().sendRequest(req, new C16777());
    }

    public void processLoadedContacts(ArrayList<TL_contact> contactsArr, ArrayList<User> usersArr, int from) {
        AndroidUtilities.runOnUIThread(new C04608(usersArr, from, contactsArr));
    }

    private void reloadContactsStatusesMaybe() {
        try {
            if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getLong("lastReloadStatusTime", 0) < System.currentTimeMillis() - 86400000) {
                reloadContactsStatuses();
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    private void saveContactsLoadTime() {
        try {
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putLong("lastReloadStatusTime", System.currentTimeMillis()).commit();
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    private void updateUnregisteredContacts(ArrayList<TL_contact> contactsArr) {
        HashMap<String, TL_contact> contactsPhonesShort = new HashMap();
        Iterator i$ = contactsArr.iterator();
        while (i$.hasNext()) {
            TL_contact value = (TL_contact) i$.next();
            User user = MessagesController.getInstance().getUser(Integer.valueOf(value.user_id));
            if (!(user == null || user.phone == null || user.phone.length() == 0)) {
                contactsPhonesShort.put(user.phone, value);
            }
        }
        ArrayList<Contact> sortedPhoneBookContacts = new ArrayList();
        for (Entry<Integer, Contact> pair : this.contactsBook.entrySet()) {
            Contact value2 = (Contact) pair.getValue();
            int id = ((Integer) pair.getKey()).intValue();
            boolean skip = false;
            int a = 0;
            while (a < value2.phones.size()) {
                if (contactsPhonesShort.containsKey((String) value2.shortPhones.get(a)) || ((Integer) value2.phoneDeleted.get(a)).intValue() == 1) {
                    skip = true;
                    break;
                }
                a++;
            }
            if (!skip) {
                sortedPhoneBookContacts.add(value2);
            }
        }
        Collections.sort(sortedPhoneBookContacts, new C04619());
        this.phoneBookContacts = sortedPhoneBookContacts;
    }

    private void buildContactsSectionsArrays(boolean sort) {
        if (sort) {
            Collections.sort(this.contacts, new Comparator<TL_contact>() {
                public int compare(TL_contact tl_contact, TL_contact tl_contact2) {
                    return UserObject.getFirstName(MessagesController.getInstance().getUser(Integer.valueOf(tl_contact.user_id))).compareTo(UserObject.getFirstName(MessagesController.getInstance().getUser(Integer.valueOf(tl_contact2.user_id))));
                }
            });
        }
        StringBuilder ids = new StringBuilder();
        HashMap<String, ArrayList<TL_contact>> sectionsDict = new HashMap();
        ArrayList<String> sortedSectionsArray = new ArrayList();
        Iterator i$ = this.contacts.iterator();
        while (i$.hasNext()) {
            TL_contact value = (TL_contact) i$.next();
            User user = MessagesController.getInstance().getUser(Integer.valueOf(value.user_id));
            if (user != null) {
                String key = UserObject.getFirstName(user);
                if (key.length() > 1) {
                    key = key.substring(0, 1);
                }
                if (key.length() == 0) {
                    key = "#";
                } else {
                    key = key.toUpperCase();
                }
                String replace = (String) this.sectionsToReplace.get(key);
                if (replace != null) {
                    key = replace;
                }
                ArrayList<TL_contact> arr = (ArrayList) sectionsDict.get(key);
                if (arr == null) {
                    arr = new ArrayList();
                    sectionsDict.put(key, arr);
                    sortedSectionsArray.add(key);
                }
                arr.add(value);
                if (ids.length() != 0) {
                    ids.append(",");
                }
                ids.append(value.user_id);
            }
        }
        UserConfig.contactsHash = Utilities.MD5(ids.toString());
        UserConfig.saveConfig(false);
        Collections.sort(sortedSectionsArray, new Comparator<String>() {
            public int compare(String s, String s2) {
                char cv1 = s.charAt(0);
                char cv2 = s2.charAt(0);
                if (cv1 == '#') {
                    return 1;
                }
                if (cv2 == '#') {
                    return -1;
                }
                return s.compareTo(s2);
            }
        });
        this.usersSectionsDict = sectionsDict;
        this.sortedUsersSectionsArray = sortedSectionsArray;
    }

    private boolean hasContactsPermission() {
        if (VERSION.SDK_INT < 23) {
            Cursor cursor = null;
            try {
                cursor = ApplicationLoader.applicationContext.getContentResolver().query(Phone.CONTENT_URI, this.projectionPhones, null, null, null);
                if (cursor == null || cursor.getCount() == 0) {
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (Throwable e) {
                            FileLog.m13e("tmessages", e);
                        }
                    }
                    return false;
                }
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Throwable e2) {
                        FileLog.m13e("tmessages", e2);
                    }
                }
                return true;
            } catch (Throwable e22) {
                FileLog.m13e("tmessages", e22);
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Throwable e222) {
                        FileLog.m13e("tmessages", e222);
                    }
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Throwable e2222) {
                        FileLog.m13e("tmessages", e2222);
                    }
                }
            }
        } else if (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
            return true;
        } else {
            return false;
        }
    }

    private void performWriteContactsToPhoneBookInternal(ArrayList<TL_contact> contactsArray) {
        try {
            if (hasContactsPermission()) {
                Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build();
                Cursor c1 = ApplicationLoader.applicationContext.getContentResolver().query(rawContactUri, new String[]{"_id", "sync2"}, null, null, null);
                HashMap<Integer, Long> bookContacts = new HashMap();
                if (c1 != null) {
                    while (c1.moveToNext()) {
                        bookContacts.put(Integer.valueOf(c1.getInt(1)), Long.valueOf(c1.getLong(0)));
                    }
                    c1.close();
                    for (int a = 0; a < contactsArray.size(); a++) {
                        TL_contact u = (TL_contact) contactsArray.get(a);
                        if (!bookContacts.containsKey(Integer.valueOf(u.user_id))) {
                            addContactToPhoneBook(MessagesController.getInstance().getUser(Integer.valueOf(u.user_id)), false);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    private void performWriteContactsToPhoneBook() {
        ArrayList<TL_contact> contactsArray = new ArrayList();
        contactsArray.addAll(this.contacts);
        Utilities.phoneBookQueue.postRunnable(new AnonymousClass12(contactsArray));
    }

    private void applyContactsUpdates(ArrayList<Integer> ids, ConcurrentHashMap<Integer, User> userDict, ArrayList<TL_contact> newC, ArrayList<Integer> contactsTD) {
        int a;
        Integer uid;
        if (newC == null || contactsTD == null) {
            newC = new ArrayList();
            contactsTD = new ArrayList();
            for (a = 0; a < ids.size(); a++) {
                uid = (Integer) ids.get(a);
                if (uid.intValue() > 0) {
                    TL_contact contact = new TL_contact();
                    contact.user_id = uid.intValue();
                    newC.add(contact);
                } else if (uid.intValue() < 0) {
                    contactsTD.add(Integer.valueOf(-uid.intValue()));
                }
            }
        }
        FileLog.m11e("tmessages", "process update - contacts add = " + newC.size() + " delete = " + contactsTD.size());
        StringBuilder toAdd = new StringBuilder();
        StringBuilder toDelete = new StringBuilder();
        boolean reloadContacts = false;
        for (a = 0; a < newC.size(); a++) {
            Contact contact2;
            int index;
            TL_contact newContact = (TL_contact) newC.get(a);
            User user = null;
            if (userDict != null) {
                user = (User) userDict.get(Integer.valueOf(newContact.user_id));
            }
            if (user == null) {
                user = MessagesController.getInstance().getUser(Integer.valueOf(newContact.user_id));
            } else {
                MessagesController.getInstance().putUser(user, true);
            }
            if (user == null || user.phone == null || user.phone.length() == 0) {
                reloadContacts = true;
            } else {
                contact2 = (Contact) this.contactsBookSPhones.get(user.phone);
                if (contact2 != null) {
                    index = contact2.shortPhones.indexOf(user.phone);
                    if (index != -1) {
                        contact2.phoneDeleted.set(index, Integer.valueOf(0));
                    }
                }
                if (toAdd.length() != 0) {
                    toAdd.append(",");
                }
                toAdd.append(user.phone);
            }
        }
        for (a = 0; a < contactsTD.size(); a++) {
            uid = (Integer) contactsTD.get(a);
            Utilities.phoneBookQueue.postRunnable(new AnonymousClass13(uid));
            user = null;
            if (userDict != null) {
                user = (User) userDict.get(uid);
            }
            if (user == null) {
                user = MessagesController.getInstance().getUser(uid);
            } else {
                MessagesController.getInstance().putUser(user, true);
            }
            if (user == null) {
                reloadContacts = true;
            } else if (user.phone != null && user.phone.length() > 0) {
                contact2 = (Contact) this.contactsBookSPhones.get(user.phone);
                if (contact2 != null) {
                    index = contact2.shortPhones.indexOf(user.phone);
                    if (index != -1) {
                        contact2.phoneDeleted.set(index, Integer.valueOf(1));
                    }
                }
                if (toDelete.length() != 0) {
                    toDelete.append(",");
                }
                toDelete.append(user.phone);
            }
        }
        if (!(toAdd.length() == 0 && toDelete.length() == 0)) {
            MessagesStorage.getInstance().applyPhoneBookUpdates(toAdd.toString(), toDelete.toString());
        }
        if (reloadContacts) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    ContactsController.this.loadContacts(false, true);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new AnonymousClass15(newC, contactsTD));
        }
    }

    public void processContactsUpdates(ArrayList<Integer> ids, ConcurrentHashMap<Integer, User> userDict) {
        ArrayList<TL_contact> newContacts = new ArrayList();
        ArrayList<Integer> contactsToDelete = new ArrayList();
        Iterator i$ = ids.iterator();
        while (i$.hasNext()) {
            Integer uid = (Integer) i$.next();
            int idx;
            if (uid.intValue() > 0) {
                TL_contact contact = new TL_contact();
                contact.user_id = uid.intValue();
                newContacts.add(contact);
                if (!this.delayedContactsUpdate.isEmpty()) {
                    idx = this.delayedContactsUpdate.indexOf(Integer.valueOf(-uid.intValue()));
                    if (idx != -1) {
                        this.delayedContactsUpdate.remove(idx);
                    }
                }
            } else if (uid.intValue() < 0) {
                contactsToDelete.add(Integer.valueOf(-uid.intValue()));
                if (!this.delayedContactsUpdate.isEmpty()) {
                    idx = this.delayedContactsUpdate.indexOf(Integer.valueOf(-uid.intValue()));
                    if (idx != -1) {
                        this.delayedContactsUpdate.remove(idx);
                    }
                }
            }
        }
        if (!contactsToDelete.isEmpty()) {
            MessagesStorage.getInstance().deleteContacts(contactsToDelete);
        }
        if (!newContacts.isEmpty()) {
            MessagesStorage.getInstance().putContacts(newContacts, false);
        }
        if (this.contactsLoaded && this.contactsBookLoaded) {
            applyContactsUpdates(ids, userDict, newContacts, contactsToDelete);
            return;
        }
        this.delayedContactsUpdate.addAll(ids);
        FileLog.m11e("tmessages", "delay update - contacts add = " + newContacts.size() + " delete = " + contactsToDelete.size());
    }

    public long addContactToPhoneBook(User user, boolean check) {
        long j = -1;
        if (!(this.currentAccount == null || user == null || user.phone == null || user.phone.length() == 0 || !hasContactsPermission())) {
            j = -1;
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
            if (check) {
                try {
                    contentResolver.delete(RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build(), "sync2 = " + user.id, null);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
            ArrayList<ContentProviderOperation> query = new ArrayList();
            Builder builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
            builder.withValue("account_name", this.currentAccount.name);
            builder.withValue("account_type", this.currentAccount.type);
            builder.withValue("sync1", user.phone);
            builder.withValue("sync2", Integer.valueOf(user.id));
            query.add(builder.build());
            builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
            builder.withValueBackReference("raw_contact_id", 0);
            builder.withValue("mimetype", "vnd.android.cursor.item/name");
            builder.withValue("data2", user.first_name);
            builder.withValue("data3", user.last_name);
            query.add(builder.build());
            builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
            builder.withValueBackReference("raw_contact_id", 0);
            builder.withValue("mimetype", "vnd.android.cursor.item/vnd.org.telegram.messenger.android.profile");
            builder.withValue("data1", Integer.valueOf(user.id));
            builder.withValue("data2", "Telegram Profile");
            builder.withValue("data3", "+" + user.phone);
            builder.withValue("data4", Integer.valueOf(user.id));
            query.add(builder.build());
            try {
                ContentProviderResult[] result = contentResolver.applyBatch("com.android.contacts", query);
                if (!(result == null || result.length <= 0 || result[0].uri == null)) {
                    j = Long.parseLong(result[0].uri.getLastPathSegment());
                }
            } catch (Throwable e2) {
                FileLog.m13e("tmessages", e2);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
        return j;
    }

    private void deleteContactFromPhoneBook(int uid) {
        if (hasContactsPermission()) {
            synchronized (this.observerLock) {
                this.ignoreChanges = true;
            }
            try {
                ApplicationLoader.applicationContext.getContentResolver().delete(RawContacts.CONTENT_URI.buildUpon().appendQueryParameter("caller_is_syncadapter", "true").appendQueryParameter("account_name", this.currentAccount.name).appendQueryParameter("account_type", this.currentAccount.type).build(), "sync2 = " + uid, null);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
            synchronized (this.observerLock) {
                this.ignoreChanges = false;
            }
        }
    }

    protected void markAsContacted(String contactId) {
        if (contactId != null) {
            Utilities.phoneBookQueue.postRunnable(new AnonymousClass16(contactId));
        }
    }

    public void addContact(User user) {
        if (user != null && user.phone != null) {
            TL_contacts_importContacts req = new TL_contacts_importContacts();
            ArrayList<TL_inputPhoneContact> contactsParams = new ArrayList();
            TL_inputPhoneContact c = new TL_inputPhoneContact();
            c.phone = user.phone;
            if (!c.phone.startsWith("+")) {
                c.phone = "+" + c.phone;
            }
            c.first_name = user.first_name;
            c.last_name = user.last_name;
            c.client_id = 0;
            contactsParams.add(c);
            req.contacts = contactsParams;
            req.replace = false;
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.ContactsController.17.1 */
                class C04321 implements Runnable {
                    final /* synthetic */ User val$u;

                    C04321(User user) {
                        this.val$u = user;
                    }

                    public void run() {
                        ContactsController.this.addContactToPhoneBook(this.val$u, true);
                    }
                }

                /* renamed from: org.telegram.messenger.ContactsController.17.2 */
                class C04332 implements Runnable {
                    final /* synthetic */ TL_contacts_importedContacts val$res;

                    C04332(TL_contacts_importedContacts tL_contacts_importedContacts) {
                        this.val$res = tL_contacts_importedContacts;
                    }

                    public void run() {
                        Iterator i$ = this.val$res.users.iterator();
                        while (i$.hasNext()) {
                            User u = (User) i$.next();
                            MessagesController.getInstance().putUser(u, false);
                            if (ContactsController.this.contactsDict.get(u.id) == null) {
                                TL_contact newContact = new TL_contact();
                                newContact.user_id = u.id;
                                ContactsController.this.contacts.add(newContact);
                                ContactsController.this.contactsDict.put(newContact.user_id, newContact);
                            }
                        }
                        ContactsController.this.buildContactsSectionsArrays(true);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.contactsDidLoaded, new Object[0]);
                    }
                }

                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        TL_contacts_importedContacts res = (TL_contacts_importedContacts) response;
                        MessagesStorage.getInstance().putUsersAndChats(res.users, null, true, true);
                        for (int a = 0; a < res.users.size(); a++) {
                            User u = (User) res.users.get(a);
                            Utilities.phoneBookQueue.postRunnable(new C04321(u));
                            TL_contact newContact = new TL_contact();
                            newContact.user_id = u.id;
                            ArrayList<TL_contact> arrayList = new ArrayList();
                            arrayList.add(newContact);
                            MessagesStorage.getInstance().putContacts(arrayList, false);
                            if (u.phone != null && u.phone.length() > 0) {
                                CharSequence name = ContactsController.formatName(u.first_name, u.last_name);
                                MessagesStorage.getInstance().applyPhoneBookUpdates(u.phone, TtmlNode.ANONYMOUS_REGION_ID);
                                Contact contact = (Contact) ContactsController.this.contactsBookSPhones.get(u.phone);
                                if (contact != null) {
                                    int index = contact.shortPhones.indexOf(u.phone);
                                    if (index != -1) {
                                        contact.phoneDeleted.set(index, Integer.valueOf(0));
                                    }
                                }
                            }
                        }
                        AndroidUtilities.runOnUIThread(new C04332(res));
                    }
                }
            }, 6);
        }
    }

    public void deleteContact(ArrayList<User> users) {
        if (users != null && !users.isEmpty()) {
            TL_contacts_deleteContacts req = new TL_contacts_deleteContacts();
            ArrayList<Integer> uids = new ArrayList();
            Iterator i$ = users.iterator();
            while (i$.hasNext()) {
                User user = (User) i$.next();
                InputUser inputUser = MessagesController.getInputUser(user);
                if (inputUser != null) {
                    uids.add(Integer.valueOf(user.id));
                    req.id.add(inputUser);
                }
            }
            ConnectionsManager.getInstance().sendRequest(req, new AnonymousClass18(uids, users));
        }
    }

    public void reloadContactsStatuses() {
        saveContactsLoadTime();
        MessagesController.getInstance().clearFullUsers();
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        editor.putBoolean("needGetStatuses", true).commit();
        ConnectionsManager.getInstance().sendRequest(new TL_contacts_getStatuses(), new AnonymousClass19(editor));
    }

    public void loadPrivacySettings() {
        if (this.loadingDeleteInfo == 0) {
            this.loadingDeleteInfo = 1;
            ConnectionsManager.getInstance().sendRequest(new TL_account_getAccountTTL(), new RequestDelegate() {

                /* renamed from: org.telegram.messenger.ContactsController.20.1 */
                class C04391 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    C04391(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        if (this.val$error == null) {
                            ContactsController.this.deleteAccountTTL = this.val$response.days;
                            ContactsController.this.loadingDeleteInfo = 2;
                        } else {
                            ContactsController.this.loadingDeleteInfo = 0;
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                    }
                }

                public void run(TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new C04391(error, response));
                }
            });
        }
        if (this.loadingLastSeenInfo == 0) {
            this.loadingLastSeenInfo = 1;
            TL_account_getPrivacy req = new TL_account_getPrivacy();
            req.key = new TL_inputPrivacyKeyStatusTimestamp();
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.ContactsController.21.1 */
                class C04401 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    C04401(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        if (this.val$error == null) {
                            TL_account_privacyRules rules = this.val$response;
                            MessagesController.getInstance().putUsers(rules.users, false);
                            ContactsController.this.privacyRules = rules.rules;
                            ContactsController.this.loadingLastSeenInfo = 2;
                        } else {
                            ContactsController.this.loadingLastSeenInfo = 0;
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                    }
                }

                public void run(TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new C04401(error, response));
                }
            });
        }
        if (this.loadingGroupInfo == 0) {
            this.loadingGroupInfo = 1;
            req = new TL_account_getPrivacy();
            req.key = new TL_inputPrivacyKeyChatInvite();
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.ContactsController.22.1 */
                class C04411 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    C04411(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        if (this.val$error == null) {
                            TL_account_privacyRules rules = this.val$response;
                            MessagesController.getInstance().putUsers(rules.users, false);
                            ContactsController.this.groupPrivacyRules = rules.rules;
                            ContactsController.this.loadingGroupInfo = 2;
                        } else {
                            ContactsController.this.loadingGroupInfo = 0;
                        }
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
                    }
                }

                public void run(TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new C04411(error, response));
                }
            });
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    public void setDeleteAccountTTL(int ttl) {
        this.deleteAccountTTL = ttl;
    }

    public int getDeleteAccountTTL() {
        return this.deleteAccountTTL;
    }

    public boolean getLoadingDeleteInfo() {
        return this.loadingDeleteInfo != 2;
    }

    public boolean getLoadingLastSeenInfo() {
        return this.loadingLastSeenInfo != 2;
    }

    public boolean getLoadingGroupInfo() {
        return this.loadingGroupInfo != 2;
    }

    public ArrayList<PrivacyRule> getPrivacyRules(boolean isGroup) {
        if (isGroup) {
            return this.groupPrivacyRules;
        }
        return this.privacyRules;
    }

    public void setPrivacyRules(ArrayList<PrivacyRule> rules, boolean isGroup) {
        if (isGroup) {
            this.groupPrivacyRules = rules;
        } else {
            this.privacyRules = rules;
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.privacyRulesUpdated, new Object[0]);
        reloadContactsStatuses();
    }

    public static String formatName(String firstName, String lastName) {
        int length;
        int i = 0;
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
        if (firstName != null) {
            length = firstName.length();
        } else {
            length = 0;
        }
        if (lastName != null) {
            i = lastName.length();
        }
        StringBuilder result = new StringBuilder((i + length) + 1);
        if (LocaleController.nameDisplayOrder == 1) {
            if (firstName != null && firstName.length() > 0) {
                result.append(firstName);
                if (lastName != null && lastName.length() > 0) {
                    result.append(" ");
                    result.append(lastName);
                }
            } else if (lastName != null && lastName.length() > 0) {
                result.append(lastName);
            }
        } else if (lastName != null && lastName.length() > 0) {
            result.append(lastName);
            if (firstName != null && firstName.length() > 0) {
                result.append(" ");
                result.append(firstName);
            }
        } else if (firstName != null && firstName.length() > 0) {
            result.append(firstName);
        }
        return result.toString();
    }
}
