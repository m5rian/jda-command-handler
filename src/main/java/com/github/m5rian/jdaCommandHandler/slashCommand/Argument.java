package com.github.m5rian.jdaCommandHandler.slashCommand;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Keep annotation at runtime
@Target(ElementType.METHOD) // Only addable for methods
public @interface Argument {
    OptionType type();

    String name();

    String description();

    boolean required() default false;

    Choice[] choices() default {};
}
