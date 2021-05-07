package com.github.m5rian.jdaCommandHandler;

/**
 * This is the interface, which you need to implement as soon as you want your class to have commands.
 * <p>
 * This is needed, because to {@link java.lang.reflect.Method#invoke(Object, Object...)} a method
 * you need for the first argument an instance.
 */
public interface CommandHandler {
}
