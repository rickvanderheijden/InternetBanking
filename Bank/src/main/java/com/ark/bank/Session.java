package com.ark.bank;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @author Rick van der Heijden
 */
public class Session {
    private Timer timer;
    private String customerName;
    private String customerResidence;
    private final int sessionTime;
    private boolean active;
    private final UUID key = UUID.randomUUID();

    public Session(int sessionTime) {
        this.sessionTime = sessionTime;
        createTimer();
    }

    public boolean isActive() {
        return active;
    }

    public boolean refresh() {
        boolean result = false;

        if (isActive()) {
            stopTimer();
            createTimer();
            result = true;
        }

        return result;
    }

    public boolean terminate() {
        boolean result = false;

        if (isActive()) {
            stopTimer();
            result = true;
        }

        return result;
    }

    public String getSessionKey() {
        return isActive() ? key.toString() : null;
    }

    private void createTimer() {
        stopTimer();

        timer = new Timer();
        timer.schedule(new PeriodicTask(), sessionTime);
        active = true;
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        active = false;
    }

    private class PeriodicTask extends TimerTask {
        @Override
        public void run() {
            stopTimer();
            active = false;
        }
    }
}
