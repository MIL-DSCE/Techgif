package com.appsgeyser.sdk.server;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class BaseServerClient {
    protected static final int CONNECTION_TIMEOUT = 30000;
    protected static final int FORBIDDEN_RESPONSE = 403;
    protected static final int OK_RESPONSE = 200;
    protected static final int SOCKET_TIMEOUT = 30000;
    private static final String TAG;
    protected HttpParams _httpParameters;

    /* renamed from: com.appsgeyser.sdk.server.BaseServerClient.1 */
    class C01511 extends Thread {
        private final /* synthetic */ OnRequestDoneListener val$onResponseListener;
        private final /* synthetic */ String val$requestUrl;
        private final /* synthetic */ int val$tag;

        C01511(String str, OnRequestDoneListener onRequestDoneListener, int i) {
            this.val$requestUrl = str;
            this.val$onResponseListener = onRequestDoneListener;
            this.val$tag = i;
        }

        public void run() {
            HttpClient client = new DefaultHttpClient(BaseServerClient.this._httpParameters);
            HttpGet httpGet = new HttpGet(this.val$requestUrl);
            httpGet.setHeader("Cache-Control", "no-cache,no-store");
            HttpResponse response = null;
            try {
                response = client.execute(httpGet);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            this.val$onResponseListener.onRequestDone(this.val$requestUrl, this.val$tag, response);
        }
    }

    public interface OnRequestDoneListener {
        void onRequestDone(String str, int i, HttpResponse httpResponse);
    }

    static {
        TAG = BaseServerClient.class.getSimpleName();
    }

    public BaseServerClient() {
        this._httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(this._httpParameters, SOCKET_TIMEOUT);
        HttpConnectionParams.setSoTimeout(this._httpParameters, SOCKET_TIMEOUT);
    }

    public void sendRequestAsync(String requestUrl, int tag, OnRequestDoneListener onResponseListener) {
        new C01511(requestUrl, onResponseListener, tag).start();
    }

    public String SendRequestSync(String requestURL) {
        try {
            HttpClient client = new DefaultHttpClient(this._httpParameters);
            HttpGet httpGet = new HttpGet(requestURL);
            httpGet.setHeader("Cache-Control", "no-cache,no-store");
            return (String) client.execute(httpGet, new BasicResponseHandler());
        } catch (Exception e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }
}
