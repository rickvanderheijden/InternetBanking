package com.ark.bankingapplication;

public class ReturnObject {
    private final boolean success;
    private final String title;
    private final String body;
    private final String sessionKey;

    public ReturnObject(boolean success, String title, String body) {
        this.success = success;
        this.title = title;
        this.body = body;
        this.sessionKey = null;
    }

    public ReturnObject(boolean success, String title, String body, String sessionKey) {
        this.success = success;
        this.title = title;
        this.body = body;
        this.sessionKey = null;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getSessionKey() {
        return sessionKey;
    }
}
