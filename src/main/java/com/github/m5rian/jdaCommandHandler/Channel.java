package com.github.m5rian.jdaCommandHandler;

import com.github.m5rian.jdaCommandHandler.command.CommandEvent;

/**
 * @author Marian
 * <p>
 * This enum stores all types of channels.
 * You can set these channels in the {@link CommandEvent#channel()} annotation.
 */
public enum Channel {
    /**
     * Guild channels and direct messages
     */
    DEFAULT,
    /**
     * Guild only channels
     */
    GUILD,
    /**
     * Only direct messages
     */
    DM
}
