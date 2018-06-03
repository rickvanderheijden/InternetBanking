package com.ark.bank;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @author Rick van der Heijden
 */
public class Session {
    private Timer timer;
    private final String customerName;
    private final String customerResidence;
    private final int sessionTime;
    private boolean active;
    private final UUID key = UUID.randomUUID();

    /**
     * Creates an instance of Session.
     * @param sessionTime Time after which the session will expire, in milliseconds. Can not be zero or negative.
     * @param customerName Name of the customer. Can not be null or empty.
     * @param customerResidence Residence of the customer. Can not be null or empty.
     * @throws IllegalArgumentException if <tt>sessionTime</tt> is zero or negative, <tt>customerName</tt> is null or empty,
     * or if customerResidence is null or empty.
     */
    public Session(int sessionTime, String customerName, String customerResidence) throws IllegalArgumentException {

        if ((sessionTime < 0)
            || (customerName == null) || customerName.isEmpty()
            || (customerResidence == null) || customerResidence.isEmpty()){
            throw new IllegalArgumentException("Argument can not be null or empty");
        }

        this.sessionTime = sessionTime;
        this.customerName = customerName;
        this.customerResidence = customerResidence;
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

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerResidence() {
        return customerResidence;
    }

    private class PeriodicTask extends TimerTask {
        @Override
        public void run() {
            stopTimer();
            active = false;
        }
    }
}
