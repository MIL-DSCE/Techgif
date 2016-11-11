package com.appsgeyser.sdk.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class Response {
    private final HttpResponse _httpResp;

    public Response(HttpResponse response) {
        this._httpResp = response;
    }

    public final HttpResponse httpResponse() {
        return this._httpResp;
    }

    public String body() {
        String strResponse = TtmlNode.ANONYMOUS_REGION_ID;
        if (this._httpResp == null) {
            return strResponse;
        }
        HttpEntity entity = this._httpResp.getEntity();
        if (entity != null) {
            return entity.toString();
        }
        return strResponse;
    }

    public Map<String, List<String>> getHeaders() {
        Map<String, List<String>> headersMap = new HashMap();
        if (this._httpResp != null) {
            Header[] headers = this._httpResp.getAllHeaders();
            for (Header headerEntry : headers) {
                String hdName = headerEntry.getName();
                String hdValue = headerEntry.getValue();
                List<String> valuesList = (List) headersMap.get(hdName);
                if (valuesList == null) {
                    valuesList = new ArrayList();
                }
                valuesList.add(hdValue);
                headersMap.put(hdName, valuesList);
            }
        }
        return headersMap;
    }

    public long length() {
        if (this._httpResp == null) {
            return 0;
        }
        HttpEntity entity = this._httpResp.getEntity();
        if (entity != null) {
            return (long) entity.toString().length();
        }
        return 0;
    }

    public InputStream rawBody() {
        if (this._httpResp == null) {
            return null;
        }
        try {
            HttpEntity entity = this._httpResp.getEntity();
            if (entity != null) {
                return entity.getContent();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public int status() {
        if (this._httpResp == null) {
            return 0;
        }
        StatusLine statusLine = this._httpResp.getStatusLine();
        if (statusLine != null) {
            return statusLine.getStatusCode();
        }
        return 0;
    }

    public String getDescription() {
        String strResponse = new StringBuilder(String.valueOf(TtmlNode.ANONYMOUS_REGION_ID + "Status " + status() + "\n")).append("length ").append(length()).append("\n").toString();
        if (length() > 0) {
            return new StringBuilder(String.valueOf(strResponse)).append("body ").append(body()).toString();
        }
        return strResponse;
    }
}
