package com.github.m5rian.jdaCommandHandler.command;

/**
 * Data class which represents a cooldown.
 */
public class CoolDown {
    private final long milliseconds;

    /**
     * @param milliseconds the expiration time in milliseconds.
     */
    public CoolDown(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * @return Returns whether the cooldown expired.
     */
    public boolean isOnCooldown() {
        return getCoolDown() > 0;
    }

    /**
     * @return the cool down (time remaining) in milliseconds.
     */
    public long getCoolDown() {
        return milliseconds - System.currentTimeMillis();
    }

    /**
     * @return the cool down (time remaining) in a pretty string format.
     */
    public String getPretty() {
        long seconds = (milliseconds - System.currentTimeMillis()) / 1000;

        return String.format("%02d Hours %02d Minutes %02d Seconds",
                        seconds / 3600, (seconds / 60) % 60, seconds % 60)
                .replaceAll("00 [a-zA-Z]+", "");
    }

}
