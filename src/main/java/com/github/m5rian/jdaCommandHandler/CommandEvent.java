package com.github.m5rian.jdaCommandHandler;

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
}