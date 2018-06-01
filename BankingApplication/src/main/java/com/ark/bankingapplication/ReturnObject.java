package com.ark.bankingapplication;

public class ReturnObject {
    private final boolean success;
    private final String title;
    private final String body;

    public ReturnObject(boolean success, String title, String body) {
        this.success = success;
        this.title = title;
        this.body = body;
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

}
