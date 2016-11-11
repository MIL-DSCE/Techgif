package com.appsgeyser.sdk;

public class ErrorInfo {
    private int _code;
    private String _message;

    public ErrorInfo(String msg) {
        this._message = msg;
    }

    public ErrorInfo(int code, String msg) {
        this._code = code;
        this._message = msg;
    }

    public String getMessage() {
        return this._message;
    }

    public int getCode() {
        return this._code;
    }
}
