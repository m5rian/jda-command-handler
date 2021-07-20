package com.github.m5rian.jdaCommandHandler.slashCommand;

import com.github.m5rian.jdaCommandHandler.CommandHandler;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Marian
 * <p>
 * This class stores all information about a registered slash command.
 */
public class SlashCommandData {
    private final Method method; // The actual method, which runs when the slash command is fired
    private final CommandHandler instance; // An instance of the class
    private final SlashCommandEvent slashCommand; // The SlashCommandEvent annotation

    /**
     * @param instance     The instance of the class, which is used to {@link Method#invoke(Object, Object...)} the slash command method.
     * @param method       The method we want to run, once the slash command is fired.
     * @param slashCommand A {@link SlashCommandEvent} annotation which stores all information about a slash command.
     */
    public SlashCommandData(CommandHandler instance, Method method, SlashCommandEvent slashCommand) {
        this.instance = instance;
        this.method = method;
        this.slashCommand = slashCommand;
    }

    /**
     * @return Returns an instance of the slash command class.
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
     * @return Returns the {@link SlashCommandEvent} annotation.
     */
    public SlashCommandEvent getSlashCommand() {
        return this.slashCommand;
    }
}