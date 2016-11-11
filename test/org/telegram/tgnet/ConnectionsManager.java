package org.telegram.tgnet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.aspectj.lang.JoinPoint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.tgnet.TLRPC.TL_config;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;

public class ConnectionsManager {
    public static final int ConnectionStateConnected = 3;
    public static final int ConnectionStateConnecting = 1;
    public static final int ConnectionStateUpdating = 4;
    public static final int ConnectionStateWaitingForNetwork = 2;
    public static final int ConnectionTypeDownload = 2;
    public static final int ConnectionTypeDownload2 = 65538;
    public static final int ConnectionTypeGeneric = 1;
    public static final int ConnectionTypePush = 8;
    public static final int ConnectionTypeUpload = 4;
    public static final int DEFAULT_DATACENTER_ID = Integer.MAX_VALUE;
    private static volatile ConnectionsManager Instance = null;
    public static final int RequestFlagCanCompress = 4;
    public static final int RequestFlagEnableUnauthorized = 1;
    public static final int RequestFlagFailOnServerErrors = 2;
    public static final int RequestFlagForceDownload = 32;
    public static final int RequestFlagInvokeAfter = 64;
    public static final int RequestFlagNeedQuickAck = 128;
    public static final int RequestFlagTryDifferentDc = 16;
    public static final int RequestFlagWithoutLogin = 8;
    private boolean appPaused;
    private int connectionState;
    private boolean isUpdating;
    private int lastClassGuid;
    private long lastPauseTime;
    private AtomicInteger lastRequestToken;
    private WakeLock wakeLock;

    /* renamed from: org.telegram.tgnet.ConnectionsManager.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ boolean val$value;

        AnonymousClass11(boolean z) {
            this.val$value = z;
        }

        public void run() {
            if (ConnectionsManager.this.isUpdating != this.val$value) {
                ConnectionsManager.this.isUpdating = this.val$value;
                if (ConnectionsManager.this.connectionState == ConnectionsManager.ConnectionStateConnected) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
                }
            }
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager.1 */
    class C08741 implements Runnable {
        final /* synthetic */ int val$connetionType;
        final /* synthetic */ int val$datacenterId;
        final /* synthetic */ int val$flags;
        final /* synthetic */ boolean val$immediate;
        final /* synthetic */ TLObject val$object;
        final /* synthetic */ RequestDelegate val$onComplete;
        final /* synthetic */ QuickAckDelegate val$onQuickAck;
        final /* synthetic */ int val$requestToken;

        /* renamed from: org.telegram.tgnet.ConnectionsManager.1.1 */
        class C17371 implements RequestDelegateInternal {

            /* renamed from: org.telegram.tgnet.ConnectionsManager.1.1.1 */
            class C08731 implements Runnable {
                final /* synthetic */ TL_error val$finalError;
                final /* synthetic */ TLObject val$finalResponse;

                C08731(TLObject tLObject, TL_error tL_error) {
                    this.val$finalResponse = tLObject;
                    this.val$finalError = tL_error;
                }

                public void run() {
                    C08741.this.val$onComplete.run(this.val$finalResponse, this.val$finalError);
                    if (this.val$finalResponse != null) {
                        this.val$finalResponse.freeResources();
                    }
                }
            }

            C17371() {
            }

            public void run(int response, int errorCode, String errorText) {
                Throwable e;
                TLObject resp = null;
                TL_error error = null;
                if (response != 0) {
                    try {
                        NativeByteBuffer buff = NativeByteBuffer.wrap(response);
                        buff.reused = true;
                        resp = C08741.this.val$object.deserializeResponse(buff, buff.readInt32(true), true);
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.m13e("tmessages", e);
                        return;
                    }
                } else if (errorText != null) {
                    TL_error error2 = new TL_error();
                    try {
                        error2.code = errorCode;
                        error2.text = errorText;
                        FileLog.m11e("tmessages", C08741.this.val$object + " got error " + error2.code + " " + error2.text);
                        error = error2;
                    } catch (Exception e3) {
                        e = e3;
                        error = error2;
                        FileLog.m13e("tmessages", e);
                        return;
                    }
                }
                FileLog.m10d("tmessages", "java received " + resp + " error = " + error);
                Utilities.stageQueue.postRunnable(new C08731(resp, error));
            }
        }

        C08741(TLObject tLObject, int i, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, int i2, int i3, int i4, boolean z) {
            this.val$object = tLObject;
            this.val$requestToken = i;
            this.val$onComplete = requestDelegate;
            this.val$onQuickAck = quickAckDelegate;
            this.val$flags = i2;
            this.val$datacenterId = i3;
            this.val$connetionType = i4;
            this.val$immediate = z;
        }

        public void run() {
            FileLog.m10d("tmessages", "send request " + this.val$object + " with token = " + this.val$requestToken);
            try {
                NativeByteBuffer buffer = new NativeByteBuffer(this.val$object.getObjectSize());
                this.val$object.serializeToStream(buffer);
                this.val$object.freeResources();
                ConnectionsManager.native_sendRequest(buffer.address, new C17371(), this.val$onQuickAck, this.val$flags, this.val$datacenterId, this.val$connetionType, this.val$immediate, this.val$requestToken);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager.2 */
    class C08752 extends BroadcastReceiver {
        C08752() {
        }

        public void onReceive(Context context, Intent intent) {
            ConnectionsManager.this.checkConnection();
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager.3 */
    static class C08763 implements Runnable {
        C08763() {
        }

        public void run() {
            if (ConnectionsManager.getInstance().wakeLock.isHeld()) {
                FileLog.m10d("tmessages", "release wakelock");
                ConnectionsManager.getInstance().wakeLock.release();
            }
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager.4 */
    static class C08774 implements Runnable {
        final /* synthetic */ TLObject val$message;

        C08774(TLObject tLObject) {
            this.val$message = tLObject;
        }

        public void run() {
            MessagesController.getInstance().processUpdates((Updates) this.val$message, false);
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager.5 */
    static class C08785 implements Runnable {
        C08785() {
        }

        public void run() {
            MessagesController.getInstance().updateTimerProc();
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager.6 */
    static class C08796 implements Runnable {
        C08796() {
        }

        public void run() {
            MessagesController.getInstance().getDifference();
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager.7 */
    static class C08807 implements Runnable {
        final /* synthetic */ int val$state;

        C08807(int i) {
            this.val$state = i;
        }

        public void run() {
            ConnectionsManager.getInstance().connectionState = this.val$state;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager.8 */
    static class C08818 implements Runnable {
        C08818() {
        }

        public void run() {
            if (UserConfig.getClientUserId() != 0) {
                UserConfig.clearConfig();
                MessagesController.getInstance().performLogout(false);
            }
        }
    }

    /* renamed from: org.telegram.tgnet.ConnectionsManager.9 */
    static class C08829 implements Runnable {
        final /* synthetic */ TL_config val$message;

        C08829(TL_config tL_config) {
            this.val$message = tL_config;
        }

        public void run() {
            MessagesController.getInstance().updateConfig(this.val$message);
        }
    }

    public static native void native_applyDatacenterAddress(int i, String str, int i2);

    public static native void native_bindRequestToGuid(int i, int i2);

    public static native void native_cancelRequest(int i, boolean z);

    public static native void native_cancelRequestsForGuid(int i);

    public static native void native_cleanUp();

    public static native int native_getConnectionState();

    public static native int native_getCurrentTime();

    public static native long native_getCurrentTimeMillis();

    public static native int native_getTimeDifference();

    public static native void native_init(int i, int i2, int i3, String str, String str2, String str3, String str4, String str5, String str6, int i4, boolean z);

    public static native void native_pauseNetwork();

    public static native void native_resumeNetwork(boolean z);

    public static native void native_sendRequest(int i, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setJava(boolean z);

    public static native void native_setNetworkAvailable(boolean z);

    public static native void native_setPushConnectionEnabled(boolean z);

    public static native void native_setUseIpv6(boolean z);

    public static native void native_setUserId(int i);

    public static native void native_switchBackend();

    public static native void native_updateDcSettings();

    static {
        Instance = null;
    }

    public static ConnectionsManager getInstance() {
        ConnectionsManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (ConnectionsManager.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        ConnectionsManager localInstance2 = new ConnectionsManager();
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

    public ConnectionsManager() {
        this.lastPauseTime = System.currentTimeMillis();
        this.appPaused = true;
        this.lastClassGuid = RequestFlagEnableUnauthorized;
        this.isUpdating = false;
        this.connectionState = native_getConnectionState();
        this.lastRequestToken = new AtomicInteger(RequestFlagEnableUnauthorized);
        this.wakeLock = null;
        try {
            this.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(RequestFlagEnableUnauthorized, JoinPoint.SYNCHRONIZATION_LOCK);
            this.wakeLock.setReferenceCounted(false);
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public long getCurrentTimeMillis() {
        return native_getCurrentTimeMillis();
    }

    public int getCurrentTime() {
        return native_getCurrentTime();
    }

    public int getTimeDifference() {
        return native_getTimeDifference();
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock) {
        return sendRequest(object, completionBlock, null, 0);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags) {
        return sendRequest(object, completionBlock, null, flags, DEFAULT_DATACENTER_ID, RequestFlagEnableUnauthorized, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, int flags, int connetionType) {
        return sendRequest(object, completionBlock, null, flags, DEFAULT_DATACENTER_ID, connetionType, true);
    }

    public int sendRequest(TLObject object, RequestDelegate completionBlock, QuickAckDelegate quickAckBlock, int flags) {
        return sendRequest(object, completionBlock, quickAckBlock, flags, DEFAULT_DATACENTER_ID, RequestFlagEnableUnauthorized, true);
    }

    public int sendRequest(TLObject object, RequestDelegate onComplete, QuickAckDelegate onQuickAck, int flags, int datacenterId, int connetionType, boolean immediate) {
        int requestToken = this.lastRequestToken.getAndIncrement();
        Utilities.stageQueue.postRunnable(new C08741(object, requestToken, onComplete, onQuickAck, flags, datacenterId, connetionType, immediate));
        return requestToken;
    }

    public void cancelRequest(int token, boolean notifyServer) {
        native_cancelRequest(token, notifyServer);
    }

    public void cleanup() {
        native_cleanUp();
    }

    public void cancelRequestsForGuid(int guid) {
        native_cancelRequestsForGuid(guid);
    }

    public void bindRequestToGuid(int requestToken, int guid) {
        native_bindRequestToGuid(requestToken, guid);
    }

    public void applyDatacenterAddress(int datacenterId, String ipAddress, int port) {
        native_applyDatacenterAddress(datacenterId, ipAddress, port);
    }

    public int getConnectionState() {
        if (this.connectionState == ConnectionStateConnected && this.isUpdating) {
            return RequestFlagCanCompress;
        }
        return this.connectionState;
    }

    public void setUserId(int id) {
        native_setUserId(id);
    }

    private void checkConnection() {
        native_setUseIpv6(useIpv6Address());
        native_setNetworkAvailable(isNetworkOnline());
    }

    public void setPushConnectionEnabled(boolean value) {
        native_setPushConnectionEnabled(value);
    }

    public void init(int version, int layer, int apiId, String deviceModel, String systemVersion, String appVersion, String langCode, String configPath, String logPath, int userId, boolean enablePushConnection) {
        native_init(version, layer, apiId, deviceModel, systemVersion, appVersion, langCode, configPath, logPath, userId, enablePushConnection);
        checkConnection();
        ApplicationLoader.applicationContext.registerReceiver(new C08752(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void switchBackend() {
        native_switchBackend();
    }

    public void resumeNetworkMaybe() {
        native_resumeNetwork(true);
    }

    public void updateDcSettings() {
        native_updateDcSettings();
    }

    public long getPauseTime() {
        return this.lastPauseTime;
    }

    public void setAppPaused(boolean value, boolean byScreenState) {
        if (!byScreenState) {
            this.appPaused = value;
            FileLog.m10d("tmessages", "app paused = " + value);
        }
        if (value) {
            if (this.lastPauseTime == 0) {
                this.lastPauseTime = System.currentTimeMillis();
            }
            native_pauseNetwork();
        } else if (!this.appPaused) {
            FileLog.m11e("tmessages", "reset app pause time");
            if (this.lastPauseTime != 0 && System.currentTimeMillis() - this.lastPauseTime > HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS) {
                ContactsController.getInstance().checkContacts();
            }
            this.lastPauseTime = 0;
            native_resumeNetwork(false);
        }
    }

    public static void onUnparsedMessageReceived(int address) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            TLObject message = TLClassStore.Instance().TLdeserialize(buff, buff.readInt32(true), true);
            if (message instanceof Updates) {
                FileLog.m10d("tmessages", "java received " + message);
                AndroidUtilities.runOnUIThread(new C08763());
                Utilities.stageQueue.postRunnable(new C08774(message));
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public static void onUpdate() {
        Utilities.stageQueue.postRunnable(new C08785());
    }

    public static void onSessionCreated() {
        Utilities.stageQueue.postRunnable(new C08796());
    }

    public static void onConnectionStateChanged(int state) {
        AndroidUtilities.runOnUIThread(new C08807(state));
    }

    public static void onLogout() {
        AndroidUtilities.runOnUIThread(new C08818());
    }

    public static void onUpdateConfig(int address) {
        try {
            NativeByteBuffer buff = NativeByteBuffer.wrap(address);
            buff.reused = true;
            TL_config message = TL_config.TLdeserialize(buff, buff.readInt32(true), true);
            if (message != null) {
                Utilities.stageQueue.postRunnable(new C08829(message));
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
    }

    public static void onInternalPushReceived() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                try {
                    if (!ConnectionsManager.getInstance().wakeLock.isHeld()) {
                        ConnectionsManager.getInstance().wakeLock.acquire(10000);
                        FileLog.m10d("tmessages", "acquire wakelock");
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        });
    }

    public int generateClassGuid() {
        int i = this.lastClassGuid;
        this.lastClassGuid = i + RequestFlagEnableUnauthorized;
        return i;
    }

    public static boolean isRoaming() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
            if (netInfo != null) {
                return netInfo.isRoaming();
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        return false;
    }

    public static boolean isConnectedToWiFi() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(RequestFlagEnableUnauthorized);
            if (netInfo != null && netInfo.getState() == State.CONNECTED) {
                return true;
            }
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        return false;
    }

    public void applyCountryPortNumber(String number) {
    }

    public void setIsUpdating(boolean value) {
        AndroidUtilities.runOnUIThread(new AnonymousClass11(value));
    }

    @SuppressLint({"NewApi"})
    protected static boolean useIpv6Address() {
        if (VERSION.SDK_INT < 19) {
            return false;
        }
        Enumeration<NetworkInterface> networkInterfaces;
        NetworkInterface networkInterface;
        List<InterfaceAddress> interfaceAddresses;
        int a;
        InetAddress inetAddress;
        if (BuildVars.DEBUG_VERSION) {
            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                    if (!(!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.getInterfaceAddresses().isEmpty())) {
                        FileLog.m11e("tmessages", "valid interface: " + networkInterface);
                        interfaceAddresses = networkInterface.getInterfaceAddresses();
                        for (a = 0; a < interfaceAddresses.size(); a += RequestFlagEnableUnauthorized) {
                            inetAddress = ((InterfaceAddress) interfaceAddresses.get(a)).getAddress();
                            if (BuildVars.DEBUG_VERSION) {
                                FileLog.m11e("tmessages", "address: " + inetAddress.getHostAddress());
                            }
                            if (!(inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isMulticastAddress() || !BuildVars.DEBUG_VERSION)) {
                                FileLog.m11e("tmessages", "address is good");
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
            }
        }
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            boolean hasIpv4 = false;
            boolean hasIpv6 = false;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    interfaceAddresses = networkInterface.getInterfaceAddresses();
                    for (a = 0; a < interfaceAddresses.size(); a += RequestFlagEnableUnauthorized) {
                        inetAddress = ((InterfaceAddress) interfaceAddresses.get(a)).getAddress();
                        if (!(inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isMulticastAddress())) {
                            if (inetAddress instanceof Inet6Address) {
                                hasIpv6 = true;
                            } else if ((inetAddress instanceof Inet4Address) && !inetAddress.getHostAddress().startsWith("192.0.0.")) {
                                hasIpv4 = true;
                            }
                        }
                    }
                }
            }
            if (hasIpv4 || !hasIpv6) {
                return false;
            }
            return true;
        } catch (Throwable e2) {
            FileLog.m13e("tmessages", e2);
            return false;
        }
    }

    public static boolean isNetworkOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity");
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && (netInfo.isConnectedOrConnecting() || netInfo.isAvailable())) {
                return true;
            }
            netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            netInfo = cm.getNetworkInfo(RequestFlagEnableUnauthorized);
            if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
            return true;
        }
    }
}
