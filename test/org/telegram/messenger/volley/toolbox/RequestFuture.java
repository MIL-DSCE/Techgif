package org.telegram.messenger.volley.toolbox;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.telegram.messenger.volley.Request;
import org.telegram.messenger.volley.Response.ErrorListener;
import org.telegram.messenger.volley.Response.Listener;
import org.telegram.messenger.volley.VolleyError;

public class RequestFuture<T> implements Future<T>, Listener<T>, ErrorListener {
    private VolleyError mException;
    private Request<?> mRequest;
    private T mResult;
    private boolean mResultReceived;

    public static <E> RequestFuture<E> newFuture() {
        return new RequestFuture();
    }

    private RequestFuture() {
        this.mResultReceived = false;
    }

    public void setRequest(Request<?> request) {
        this.mRequest = request;
    }

    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        boolean z = false;
        synchronized (this) {
            if (this.mRequest != null) {
                if (!isDone()) {
                    this.mRequest.cancel();
                    z = true;
                }
            }
        }
        return z;
    }

    public T get() throws InterruptedException, ExecutionException {
        try {
            return doGet(null);
        } catch (TimeoutException e) {
            throw new AssertionError(e);
        }
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(Long.valueOf(TimeUnit.MILLISECONDS.convert(timeout, unit)));
    }

    private synchronized T doGet(Long timeoutMs) throws InterruptedException, ExecutionException, TimeoutException {
        T t;
        if (this.mException != null) {
            throw new ExecutionException(this.mException);
        } else if (this.mResultReceived) {
            t = this.mResult;
        } else {
            if (timeoutMs == null) {
                wait(0);
            } else if (timeoutMs.longValue() > 0) {
                wait(timeoutMs.longValue());
            }
            if (this.mException != null) {
                throw new ExecutionException(this.mException);
            } else if (this.mResultReceived) {
                t = this.mResult;
            } else {
                throw new TimeoutException();
            }
        }
        return t;
    }

    public boolean isCancelled() {
        if (this.mRequest == null) {
            return false;
        }
        return this.mRequest.isCanceled();
    }

    public synchronized boolean isDone() {
        boolean z;
        z = this.mResultReceived || this.mException != null || isCancelled();
        return z;
    }

    public synchronized void onResponse(T response) {
        this.mResultReceived = true;
        this.mResult = response;
        notifyAll();
    }

    public synchronized void onErrorResponse(VolleyError error) {
        this.mException = error;
        notifyAll();
    }
}
