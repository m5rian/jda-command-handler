package com.github.m5rian.jdaCommandHandler.slashCommand;

public @interface SubcommandSet {

    String name();

    String description();

    Subcommand[] subcommands() default {};

}
