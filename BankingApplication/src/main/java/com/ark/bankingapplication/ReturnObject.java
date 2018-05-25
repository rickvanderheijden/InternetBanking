package com.ark.bankingapplication;

public class ReturnObject {
    private boolean success;
    private String sessionKey;
    private String title;
    private String body;

    public ReturnObject(boolean success, String title, String body) {
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

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

}
