package com.github.m5rian.jdaCommandHandler.command;

public enum CooldownTarget {
    GLOBAL,
    GUILD,
    NONE;

    public boolean commandSpecific = false;

    public CooldownTarget makeCommandSpecific() {
        this.commandSpecific = true;
        return this;
    }
}
