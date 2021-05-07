package com.github.m5rian.jdaCommandHandler;

/**
 * @author Marian
 *
 * This role is used as a default value in every CommandService.
 * For this role you don't need any permissions.
 */
public class Everyone implements Permission {
    @Override
    public String getName() {
        return "everyone";
    }
}
