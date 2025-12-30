package com.dfac.common;

import java.util.concurrent.atomic.AtomicLong;

public class ClockService {
    private static ClockService instance;
    private final AtomicLong lamportTime;

    private ClockService() {
        this.lamportTime = new AtomicLong(0);
    }

    public static synchronized ClockService getInstance() {
        if (instance == null) {
            instance = new ClockService();
        }
        return instance;
    }

    /**
     * Increments the local clock. Should be called before sending a message
     * (event).
     * 
     * @return The new local time.
     */
    public long incrementAndGet() {
        return this.lamportTime.incrementAndGet();
    }

    /**
     * Updates the local clock based on the received time.
     * Logic: max(local, received) + 1
     * 
     * @param receivedTime The time from the received message.
     * @return The new local time.
     */
    public long updateOnReceive(long receivedTime) {
        return this.lamportTime.updateAndGet(current -> Math.max(current, receivedTime) + 1);
    }

    /**
     * Gets the current time without incrementing.
     * 
     * @return The current local time.
     */
    public long getTime() {
        return this.lamportTime.get();
    }
}
