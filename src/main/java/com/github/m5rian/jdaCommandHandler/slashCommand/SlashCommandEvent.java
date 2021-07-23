package com.github.m5rian.jdaCommandHandler.slashCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a slash command as usable. It contains all important information to execute the slash command.
 */
@Retention(RetentionPolicy.RUNTIME) // Keep annotation at runtime
@Target(ElementType.METHOD) // Only addable for methods
public @interface SlashCommandEvent {
    /**
     * Retrieves the executor of the slash command
     *
     * @return The slash command executor.
     */
    String name();

    /**
     * Retrieves the  description of the slash command.
     *
     * @return Returns a brief description of the slash command.
     */
    String description();

    SubcommandSet[] subcommandsSets() default {};

    Subcommand[] subcommands() default {};

    Argument[] args() default {};
}