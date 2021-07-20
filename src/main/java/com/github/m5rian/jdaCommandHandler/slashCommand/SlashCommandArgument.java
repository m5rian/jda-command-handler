package com.github.m5rian.jdaCommandHandler.slashCommand;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

/**
 * @param optionType  The {@link OptionType} of the argument.
 * @param name        The name of the argument.
 * @param description A description of the argument.
 * @param required    Is the argument required?
 * @author Marian
 * <p>
 * This record represents an argument of a slash command.
 * Add {@link SlashCommandArgument}s to a {@link SlashCommandEvent} annotation.
 */
public record SlashCommandArgument(OptionType optionType, String name, String description, List<Command.Choice> choices, Boolean required) {

    /**
     * @param optionType  The {@link OptionType} of the argument.
     * @param name        The name of the argument.
     * @param description A description of the argument.
     */
    public SlashCommandArgument(OptionType optionType, String name, String description, List<Command.Choice> choices) {
        this(optionType, name, description, choices, false);
    }
}