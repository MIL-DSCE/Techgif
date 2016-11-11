package com.appsgeyser.sdk.ads;

import com.appsgeyser.sdk.configuration.Configuration;
import com.appsgeyser.sdk.configuration.Constants;
import java.net.URLEncoder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class StatServerClient {
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int SOCKET_TIMEOUT = 30000;
    private HttpParams _httpParameters;

    /* renamed from: com.appsgeyser.sdk.ads.StatServerClient.1 */
    class C01471 extends Thread {
        private final /* synthetic */ String val$requestUrl;

        C01471(String str) {
            this.val$requestUrl = str;
        }

        public void run() {
            try {
                HttpClient client = new DefaultHttpClient(StatServerClient.this._httpParameters);
                HttpGet httpGet = new HttpGet(this.val$requestUrl);
                httpGet.setHeader("Cache-Control", "no-cache,no-store");
                client.execute(httpGet);
            } catch (Exception e) {
            }
        }
    }

    /* renamed from: com.appsgeyser.sdk.ads.StatServerClient.2 */
    class C01482 extends Thread {
        private final /* synthetic */ String val$requestUrl;

        C01482(String str) {
            this.val$requestUrl = str;
        }

        public void run() {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(this.val$requestUrl);
                httpGet.setHeader("Cache-Control", "no-cache,no-store");
                client.execute(httpGet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public StatServerClient() {
        this._httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(this._httpParameters, SOCKET_TIMEOUT);
        HttpConnectionParams.setSoTimeout(this._httpParameters, SOCKET_TIMEOUT);
    }

    public void sendClickInfo(String reportUrl) {
        _sendAsyncGetRequest(reportUrl);
    }

    private void _sendAsyncGetRequest(String requestUrl) {
        new C01471(requestUrl).start();
    }

    public void sendPushReceivedAsync(String messageUrl) {
        try {
            _sendRequestAsync(_createStatPushUrl("request", messageUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _sendRequestAsync(String requestUrl) {
        new C01482(requestUrl).start();
    }

    private String _createStatPushUrl(String action, String messageUrl) {
        Configuration configuration = Configuration.getInstance();
        return "http://stat.appsgeyser.com/push.php?a=" + action + "&url=" + URLEncoder.encode(messageUrl) + "&app=" + configuration.getApplicationId() + "&guid=" + URLEncoder.encode(configuration.getAppGuid());
    }

    public void sendMessageAcceptedAsync(String messageUrl) {
        try {
            _sendRequestAsync(_createStatUrl("request", messageUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String _createStatUrl(String action, String messageUrl) {
        String statDomain = Constants.STAT_DOMAIN_URL;
        String guid = Configuration.getInstance().getAppGuid();
        return new StringBuilder(String.valueOf(statDomain)).append("pull.php?").append("a=").append(action).append("&url=").append(URLEncoder.encode(messageUrl)).append("&app=").append(Configuration.getInstance().getApplicationId()).append("&v=").append("&guid=").append(URLEncoder.encode(guid)).toString();
    }
}
