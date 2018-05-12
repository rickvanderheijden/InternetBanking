package com.ark.bank;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Rick van der Heijden
 */
public class Session {
    private final Timer timer = new Timer();
    private String key;
    private String customerName;
    private String customerResidence;
    private int sessionTime;

    public Session(int sessionTime) {
        timer.schedule(new PeriodicTask(), 0, sessionTime);
    }

    public boolean isActive() {
        return false;
    }

    public boolean refresh() {
        return false;
    }

    public boolean terminate() {
        return false;
    }

    private class PeriodicTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("TASK DONE");
        }
    }
}
