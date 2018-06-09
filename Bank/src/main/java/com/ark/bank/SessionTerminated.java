package com.ark.bank;

public class SessionTerminated {
    private final String sessionKey;

    public SessionTerminated(String sessionkey) {
        this.sessionKey = sessionkey;
    }

    public String getSessionKey() {
        return sessionKey;
    }
}
