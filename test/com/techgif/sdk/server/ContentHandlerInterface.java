package com.appsgeyser.sdk.server;

import com.appsgeyser.sdk.ErrorInfo;

public interface ContentHandlerInterface {
    void onContentRequestDone(ContentRequest contentRequest, Response response);

    void onContentRequestFailed(ContentRequest contentRequest, ErrorInfo errorInfo);
}
