package com.appsgeyser.sdk.server;

import com.appsgeyser.sdk.AppsgeyserSDKInternal;
import com.appsgeyser.sdk.ErrorInfo;
import com.appsgeyser.sdk.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.ui.Components.VideoPlayer;

public class ContentRequest {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$appsgeyser$sdk$server$ContentRequest$Method = null;
    public static final Header DEFAULT_HEADER_CACHE;
    public static final int DEFAULT_TIMEOUT_CONNECTION = 30000;
    public static final int DEFAULT_TIMEOUT_SOCKET = 30000;
    protected String _Url;
    protected ContentServer _cs;
    protected ErrorInfo _error;
    protected List<Header> _headers;
    public final HttpParams _httpParameters;
    protected final String _id;
    protected Method _method;
    protected Priority _priority;
    protected ContentHandlerInterface _watcher;

    /* renamed from: com.appsgeyser.sdk.server.ContentRequest.1 */
    class C01541 extends Thread {
        private final /* synthetic */ ContentRequest val$request;

        /* renamed from: com.appsgeyser.sdk.server.ContentRequest.1.1 */
        class C01521 implements Runnable {
            private final /* synthetic */ ContentRequest val$request;
            private final /* synthetic */ Response val$response;

            C01521(ContentRequest contentRequest, Response response) {
                this.val$request = contentRequest;
                this.val$response = response;
            }

            public void run() {
                ContentRequest.this._watcher.onContentRequestDone(this.val$request, this.val$response);
            }
        }

        /* renamed from: com.appsgeyser.sdk.server.ContentRequest.1.2 */
        class C01532 implements Runnable {
            private final /* synthetic */ ContentRequest val$request;

            C01532(ContentRequest contentRequest) {
                this.val$request = contentRequest;
            }

            public void run() {
                ContentRequest.this._watcher.onContentRequestFailed(this.val$request, ContentRequest.this._error);
            }
        }

        C01541(ContentRequest contentRequest) {
            this.val$request = contentRequest;
        }

        public void run() {
            Runnable runnable = null;
            try {
                Response response = ContentRequest.this._getResponse();
                if (ContentRequest.this._watcher != null) {
                    runnable = new C01521(this.val$request, response);
                }
            } catch (Exception e) {
                ContentRequest.this._error = new ErrorInfo(e.toString());
                Logger.DebugLog("Response error: " + ContentRequest.this._error.toString());
                if (ContentRequest.this._watcher != null) {
                    runnable = new C01532(this.val$request);
                }
            }
            if (runnable != null) {
                AppsgeyserSDKInternal.runOnMainThread(runnable);
            }
        }
    }

    public enum Method {
        GET,
        POST,
        PUT,
        HEAD
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$appsgeyser$sdk$server$ContentRequest$Method() {
        int[] iArr = $SWITCH_TABLE$com$appsgeyser$sdk$server$ContentRequest$Method;
        if (iArr == null) {
            iArr = new int[Method.values().length];
            try {
                iArr[Method.GET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Method.HEAD.ordinal()] = 4;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Method.POST.ordinal()] = 2;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Method.PUT.ordinal()] = 3;
            } catch (NoSuchFieldError e4) {
            }
            $SWITCH_TABLE$com$appsgeyser$sdk$server$ContentRequest$Method = iArr;
        }
        return iArr;
    }

    static {
        DEFAULT_HEADER_CACHE = new BasicHeader("Cache-Control", "no-cache,no-store");
    }

    public ContentRequest(String strUrl) {
        this._method = Method.GET;
        this._watcher = null;
        this._error = null;
        this._priority = Priority.MEDIUM;
        this._Url = strUrl;
        this._httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(this._httpParameters, DEFAULT_TIMEOUT_SOCKET);
        HttpConnectionParams.setSoTimeout(this._httpParameters, DEFAULT_TIMEOUT_SOCKET);
        this._id = String.valueOf(new Date().getTime());
        this._cs = ContentServer.getInstance();
        this._headers = new ArrayList();
    }

    public void setMethod(Method method) {
        this._method = method;
    }

    public Method method() {
        return this._method;
    }

    public void setTimeout(int iTimeout) {
        HttpConnectionParams.setConnectionTimeout(this._httpParameters, iTimeout);
        HttpConnectionParams.setSoTimeout(this._httpParameters, iTimeout);
    }

    public void addHeader(String name, String value) {
        this._headers.add(new BasicHeader(name, value));
    }

    public void addHeader(Header header) {
        this._headers.add(header);
    }

    public void setContentHandler(ContentHandlerInterface watcher) {
        this._watcher = watcher;
    }

    public void setPriority(Priority priority) {
        this._priority = priority;
    }

    public Response execute() {
        if (!_isValid()) {
            return null;
        }
        try {
            return _getResponse();
        } catch (Exception e) {
            this._error = new ErrorInfo(e.toString());
            return null;
        }
    }

    public void executeAsync() {
        if (_isValid()) {
            new C01541(this).start();
        }
    }

    public void enqueue() {
        this._cs.enqueue(this);
    }

    public ErrorInfo error() {
        return this._error;
    }

    private final HttpRequestBase _httpRequest() {
        switch ($SWITCH_TABLE$com$appsgeyser$sdk$server$ContentRequest$Method()[this._method.ordinal()]) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return new HttpGet(this._Url);
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return new HttpPost(this._Url);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return new HttpPut(this._Url);
            case VideoPlayer.STATE_READY /*4*/:
                return new HttpHead(this._Url);
            default:
                return null;
        }
    }

    private Header[] _headersArray() {
        Header[] hdResult = null;
        if (!this._headers.isEmpty()) {
            hdResult = new Header[this._headers.size()];
            for (int i = 0; i < this._headers.size(); i++) {
                hdResult[i] = (Header) this._headers.get(i);
            }
        }
        return hdResult;
    }

    private Response _getResponse() throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient(this._httpParameters);
        HttpRequestBase httpRequestBase = _httpRequest();
        httpRequestBase.setHeaders(_headersArray());
        Logger.DebugLog("ContentRequest executing:\n" + getDescription());
        Response response = new Response(client.execute(httpRequestBase));
        Logger.DebugLog("Response:\n" + response.getDescription());
        return response;
    }

    public String getDescription() {
        String strResponse = new StringBuilder(String.valueOf(TtmlNode.ANONYMOUS_REGION_ID + this._id + "\n")).append(this._Url).append("\n").toString();
        switch ($SWITCH_TABLE$com$appsgeyser$sdk$server$ContentRequest$Method()[this._method.ordinal()]) {
            case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                return new StringBuilder(String.valueOf(strResponse)).append("GET\n").toString();
            case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                return new StringBuilder(String.valueOf(strResponse)).append("POST\n").toString();
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return new StringBuilder(String.valueOf(strResponse)).append("PUT\n").toString();
            case VideoPlayer.STATE_READY /*4*/:
                return new StringBuilder(String.valueOf(strResponse)).append("HEAD\n").toString();
            default:
                return strResponse;
        }
    }

    private boolean _isValid() {
        if (this._Url.length() == 0) {
            return false;
        }
        return true;
    }
}
