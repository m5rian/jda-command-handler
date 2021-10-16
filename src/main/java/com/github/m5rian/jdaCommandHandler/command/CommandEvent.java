package com.github.m5rian.jdaCommandHandler.command;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.Everyone;
import com.github.m5rian.jdaCommandHandler.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a command as usable. It contains all important information to execute the command.
 */
@Retention(RetentionPolicy.RUNTIME) // Keep annotation at runtime
@Target(ElementType.METHOD) // Only addable for methods
public @interface CommandEvent {
    /**
     * Retrieves the main executor of the command
     *
     * @return The command executor.
     */
    String name();

    /**
     * Retrieves all aliases of the command.
     *
     * @return The command aliases.
     */
    String[] aliases() default {};

    /**
     * Retrieves the required permissions for this command.
     *
     * @return The required permissions.
     */
    Class<? extends Permission>[] requires() default Everyone.class;

    /**
     * Retrieves the required channel type for the command to run.
     * If {@link Channel#DEFAULT} the command will react on guild and direct messages.
     *
     * @return The channel type of the command.
     */
    Channel channel() default Channel.DEFAULT;

    /**
     * Retrieves the cooldown duration of the command.
     *
     * @return The cooldown duration in seconds.
     */
    long cooldown() default 0;

    /**
     * This is not used for any technical backend operations.
     * <p>
     * This can be used in a {@link com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessageFactory}
     * or {@link com.github.m5rian.jdaCommandHandler.commandMessages.CommandUsageFactory}.
     *
     * @return Returns required arguments for the command.
     */
    String[] args() default {};

    /**
     * This is not used for any technical backend operations.
     * <p>
     * This can be used in a {@link com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessageFactory}
     * or {@link com.github.m5rian.jdaCommandHandler.commandMessages.CommandUsageFactory}.
     *
     * @return Returns a small description on what the command does.
     */
    String description() default "";

    /**
     * This is not used for any technical backend operations.
     * <p>
     * This can be used in a {@link com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessageFactory}
     * or {@link com.github.m5rian.jdaCommandHandler.commandMessages.CommandUsageFactory}.
     *
     * @return Returns a matching emoji for the command.
     */
    String emoji() default "";
}