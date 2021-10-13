package com.github.m5rian.jdaCommandHandler.command;

import java.util.HashMap;
import java.util.Map;

/**
 * This class stores cool down of all the commands of a user.
 */
public class CoolDownData {
    /**
     * This map contains the cool down of all the commands.
     */
    private final Map<String, Long> coolDownMap;

    public CoolDownData() {
        this.coolDownMap = new HashMap<>();
    }

    /**
     * @param command the name of the command .
     * @return the cool down (time remaining) in milliseconds of the {@param command}.
     */
    public long getCoolDown(String command) {
        long current = System.currentTimeMillis();
        return coolDownMap.getOrDefault(command, current) - current;
    }

    /**
     * Checks if the user is on cool down on a particular command.
     *
     * @param command the name of the command.
     * @return true if user is on cool down else false.
     */
    public boolean hasCoolDown(String command) {
        if (!this.coolDownMap.containsKey(command)) return false;
        long expireMillis = this.coolDownMap.getOrDefault(command, -1L);
        if (expireMillis <= 0) return false;

        long currentMillis = System.currentTimeMillis();
        if (currentMillis <= expireMillis) return true;

        this.coolDownMap.remove(command);
        return false;
    }

    /**
     * Adds a command in the cool down list
     *
     * @param command         the name of the command.
     * @param commandCoolDown the cool down in milliseconds of the command.
     * @see CommandEvent#cooldown()
     */
    public void addCoolDown(String command, long commandCoolDown) {
        this.coolDownMap.put(command, System.currentTimeMillis() + commandCoolDown * 1000);
    }
}
