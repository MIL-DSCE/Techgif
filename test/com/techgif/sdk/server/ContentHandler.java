package com.appsgeyser.sdk.server;

import com.appsgeyser.sdk.ErrorInfo;

public abstract class ContentHandler implements ContentHandlerInterface {
    public abstract void onContentRequestDone(ContentRequest contentRequest, Response response);

    public abstract void onContentRequestFailed(ContentRequest contentRequest, ErrorInfo errorInfo);
}
