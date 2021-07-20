package com.github.m5rian.jdaCommandHandler.command;

import com.github.m5rian.jdaCommandHandler.CommandHandler;

import java.lang.reflect.Method;

/**
 * @author Marian
 *
 * This class stores all information about a registered command.
 */
public class CommandData {
    private final Method method; // The actual method, which runs when the command is fired
    private final CommandHandler instance; // An instance of the class
    private final CommandEvent command; // The CommandEvent annotation

    /**
     * @param instance The instance of the class, which is used to {@link Method#invoke(Object, Object...)} the command method.
     * @param method   The method we want to run, once the command is fired.
     * @param command A {@link CommandEvent} annotation which stores all information about a command.
     */
    public CommandData(CommandHandler instance, Method method, CommandEvent command) {
        this.instance = instance;
        this.method = method;
        this.command = command;
    }

    /**
     * @return Returns an instance of the command class.
     */
    public CommandHandler getInstance() {
        return this.instance;
    }

    /**
     * @return Returns the command method.
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * @return Returns the {@link CommandEvent} annotation.
     */
    public CommandEvent getCommand() {
        return this.command;
    }
}
