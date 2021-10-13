package com.github.m5rian.jdaCommandHandler.command;

/**
 * This is a data class which stores the expiration time of a command.
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
     * @return the cool down (time remaining) in milliseconds.
     * @see CoolDown#getPrettyCoolDown()
     */
    public long getCoolDown() {
        return milliseconds - System.currentTimeMillis();
    }

    /**
     * @return the cool down (time remaining) in a pretty string format.
     * @see CoolDown#getCoolDown()
     */
    public String getPrettyCoolDown() {
        long seconds = (milliseconds - System.currentTimeMillis()) / 1000;

        return String.format("%02d Hours %02d Minutes %02d Seconds",
                seconds / 3600, (seconds / 60) % 60, seconds % 60)
                .replaceAll("00 [a-zA-Z]+", "");
    }

}
