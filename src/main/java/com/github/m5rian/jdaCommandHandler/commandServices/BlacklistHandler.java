package com.github.m5rian.jdaCommandHandler.commandServices;

import net.dv8tion.jda.api.entities.User;

import java.util.function.Consumer;

public class BlacklistHandler {
    public Consumer<User> blacklistAddAction;
    public Consumer<User> blacklistRemoveAction;
}
