package com.github.m5rian.jdaCommandHandler.slashCommand;

public @interface Subcommand {

    /**
     * Retrieves the executor of the subcommand.
     *
     * @return The subcommand executor.
     */
    String name();

    /**
     * Retrieves the  description of the subcommand
     *
     * @return Returns a brief description of the subcommand.
     */
    String description();

    Argument[] args() default {};

}
