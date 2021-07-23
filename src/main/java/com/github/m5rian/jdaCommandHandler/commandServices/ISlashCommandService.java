package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.CommandListener;
import com.github.m5rian.jdaCommandHandler.CommandUtils;
import com.github.m5rian.jdaCommandHandler.slashCommand.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Marian
 * <p>
 * This is the basic interface, which all CommandServices have implemented.
 * This interface is only for slash commands.
 */
@SuppressWarnings("unused")
public interface ISlashCommandService {
    final Logger LOGGER = LoggerFactory.getLogger(ISlashCommandService.class);
    /**
     * Stores all registered slash commands.
     */
    List<SlashCommandData> slashCommands = new ArrayList<>();

    /**
     * Register a class with slash command methods.
     * <p>A method which you wish to be a slash command must have the {@link SlashCommandEvent} annotation</p>
     *
     * @param object The initialized object where command methods are.
     */
    default void registerSlashCommandClass(CommandHandler object) {
        // Go through each method
        for (Method method : object.getClass().getMethods()) {
            // Method is slash command
            if (method.isAnnotationPresent(SlashCommandEvent.class)) {
                final SlashCommandEvent commandEventAnnotation = method.getAnnotation(SlashCommandEvent.class); // Get slash command annotation
                final SlashCommandData commandData = new SlashCommandData(object, method, commandEventAnnotation); // Create method info object
                this.slashCommands.add(commandData); // Put command in list
            }
        }
    }

    /**
     * Register more than one class with slash command methods.
     * <p>A method which you wish to be a slash command must have the {@link SlashCommandEvent} annotation</p>
     *
     * @param objects The initialized objects where slash command methods are.
     */
    default void registerSlashCommandClasses(CommandHandler... objects) {
        // Go through each class
        for (CommandHandler object : objects) {
            registerSlashCommandClass(object); // Register slash command
        }
    }

    /**
     * Unregister a slash command class.
     * <p>A method which you wish to be a slash command must have the {@link SlashCommandEvent} annotation</p>
     *
     * @param object The initialized object where to unregister all command methods.
     */
    default void unregisterSlashCommand(CommandHandler object) {
        // For every item
        this.slashCommands.forEach(methodInfo -> {
            // Instance is equal to given object
            if (methodInfo.getInstance() == object) {
                this.slashCommands.remove(methodInfo); // Remove method
            }
        });
    }

    /**
     * Unregister all registered slash commands.
     */
    default void unregisterAllSlashCommands() {
        this.slashCommands.clear();
    }

    /**
     * @return Returns a Map with all registered slash commands.
     */
    default List<SlashCommandData> getSlashCommands() {
        return this.slashCommands;
    }

    /**
     * Update all slash command changes to Discord.
     */
    default void pushChanges(JDA jda) {
        int updatedSlashCommands = 0;
        int addedSlashCommands = 0;
        int deletedSlashCommands = 0;

        final List<Command> discordSlashCommands = jda.retrieveCommands().complete(); // Get slash commands which discord stored
        final List<SlashCommandData> missingSlashCommands = new ArrayList<>(this.slashCommands); // Commands which need to get registered

        // For every slash command which is already registered in discord
        for (Command discordSlashCommand : discordSlashCommands) {
            // Try finding a matching slash command which is already registered by the command handler
            final Optional<SlashCommandData> matchingRegisteredSlashCommand = this.slashCommands.stream().filter(data -> data.getSlashCommand().name().equals(discordSlashCommand.getName())).findFirst();

            // Slash command of discord doesn't exist anymore
            if (matchingRegisteredSlashCommand.isEmpty()) {
                discordSlashCommand.delete().queue(); // Delete discord's slash command
                deletedSlashCommands++; // Increase amount of deleted slash commands
            }
            // Slash command is registered
            else {
                // Get all data of the registered slash command by the command handler
                final SlashCommandEvent slashCommandEvent = matchingRegisteredSlashCommand.get().getSlashCommand(); // Get main data of slash command
                missingSlashCommands.remove(matchingRegisteredSlashCommand.get()); // Remove slash command

                // Get basic data of slash command
                final String description = slashCommandEvent.description(); // Get the description of the slash command
                final String name = slashCommandEvent.name(); // Get the name of the slash command

                CommandEditAction editAction = discordSlashCommand.editCommand(); // Create a action to edit the command

                // Update basic values
                if (!discordSlashCommand.getName().equals(name)) editAction = editAction.setName(name); // Update name of command
                if (!discordSlashCommand.getDescription().equals(description))
                    editAction = editAction.setDescription(description); // Update description of command

                final List<Command.Option> arguments = new ArrayList<>();
                for (Argument arg : slashCommandEvent.args()) {
                    final Command.Option option = CommandUtils.argumentToCommandOption(arg); // Convert argument to command option
                    arguments.add(option); // Add option
                }

                // Arguments are different from each other
                if (!discordSlashCommand.getOptions().equals(arguments)) {
                    editAction = editAction.clearOptions(); // Clear options
                    for (Command.Option option : arguments) {
                        final OptionData argument = new OptionData(option.getType(), option.getName(), option.getDescription(), option.isRequired());
                        option.getChoices().forEach(choice -> argument.addChoice(choice.getName(), choice.getAsString()));
                        editAction = editAction.addOptions(argument);
                    }
                }

                editAction.queue(); // Execute editing action
            }
        }

        // Register missing slash commands
        missingSlashCommands.forEach(slashCommandData -> {
            final SlashCommandEvent slashCommand = slashCommandData.getSlashCommand(); // Get current missing slash command
            final CommandData command = new CommandData(slashCommand.name(), slashCommand.description()); // Create basic slash command data

            // Add all subcommand sets
            for (SubcommandSet subcommandSet : slashCommand.subcommandsSets()) {
                final SubcommandGroupData subcommandGroupData = new SubcommandGroupData(subcommandSet.name(), subcommandSet.description()); // Create basic subcommand group data
                // Add all subcommands to the subcommands set
                for (Subcommand subcommand : subcommandSet.subcommands()) {
                    final SubcommandData subcommandData = new SubcommandData(subcommand.name(), subcommand.description()); // Create basic subcommand data
                    for (Argument argument : subcommand.args()) {
                        final OptionData optionData = new OptionData(argument.type(), argument.name(), argument.description(), argument.required());
                        for (Choice choice : argument.choices()) {
                            optionData.addChoice(choice.name(), choice.value());
                        }

                        subcommandData.addOptions(optionData); // Add option to subcommand
                    }

                    subcommandGroupData.addSubcommands(subcommandData); // Add subcommand to subcommand set data
                }

                command.addSubcommandGroups(subcommandGroupData); // Add subcommand set data to command
            }
            // Add all subcommands
            for (Subcommand subcommand : slashCommand.subcommands()) {
                final SubcommandData subcommandData = new SubcommandData(subcommand.name(), subcommand.description()); // Create basic subcommand data
                // Add all arguments of subcommand
                for (Argument argument : subcommand.args()) {
                    final OptionData optionData = new OptionData(argument.type(), argument.name(), argument.description(), argument.required()); // Create basic subcommand option data
                    // Add all choices to option data
                    for (Choice choice : argument.choices()) {
                        optionData.addChoice(choice.name(), choice.value()); // Add current choice
                    }
                    subcommandData.addOptions(optionData); // Add options to subcommand
                }
                command.addSubcommands(subcommandData); // Add subcommand to slash command
            }
            // Add all normal type arguments
            for (Argument argument : slashCommandData.getSlashCommand().args()) {
                // Option type is a normal value
                final OptionData optionData = new OptionData(argument.type(), argument.name(), argument.description(), argument.required());
                for (Choice choice : argument.choices()) {
                    optionData.addChoice(choice.name(), choice.value());
                }
                command.addOptions(optionData);
            }

            jda.upsertCommand(command).queue(); // Add command to discord
        });
    }

    /**
     * Runs once a message received.
     *
     * @param event The MessageReceivedEvent.
     * @throws Exception Any exceptions will be thrown to the {@link CommandListener}.
     */
    void processSlashCommandExecution(net.dv8tion.jda.api.events.interaction.SlashCommandEvent event) throws Exception;

}
