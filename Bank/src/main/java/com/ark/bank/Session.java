package com.ark.bank;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Rick van der Heijden
 */
public class Session {
    private Timer timer;
    private String key;
    private String customerName;
    private String customerResidence;
    private int sessionTime;
    private boolean active;

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
