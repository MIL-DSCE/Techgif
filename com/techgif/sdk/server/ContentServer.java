package com.appsgeyser.sdk.server;

public class ContentServer {

    private static class SingletonHolder {
        public static final ContentServer HOLDER_INSTANCE;

        private SingletonHolder() {
        }

        static {
            HOLDER_INSTANCE = new ContentServer();
        }
    }

    public static ContentServer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    protected void enqueue(ContentRequest request) {
        request.executeAsync();
    }
}
