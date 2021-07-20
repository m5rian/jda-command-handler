package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.CommandListener;
import com.github.m5rian.jdaCommandHandler.CommandUtils;
import com.github.m5rian.jdaCommandHandler.slashCommand.Argument;
import com.github.m5rian.jdaCommandHandler.slashCommand.SlashCommandData;
import com.github.m5rian.jdaCommandHandler.slashCommand.SlashCommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
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
                System.out.println("Added 1 slash command");
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

            // Slash command isn't a registered command
            if (missingSlashCommands.isEmpty()) {
                discordSlashCommand.delete().queue(); // Delete discord's slash command
                deletedSlashCommands++; // Increase amount of deleted slash commands
            }
            // Slash command is registered
            else {
                // Get all data of the registered slash command by the command handler
                final SlashCommandEvent slashCommandEvent = matchingRegisteredSlashCommand.get().getSlashCommand(); // Get main data of slash command

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

                if (discordSlashCommand.getOptions() != arguments) {
                    System.out.println("Updating command arguments");
                    editAction = editAction.clearOptions(); // Clear options
                    for (Command.Option option : arguments) {
                        editAction = editAction.addOptions(CommandUtils.optionToOptionData(option)); // Add option
                    }
                }

                editAction.queue(); // Execute editing action

/*
                // Update all arguments of the slash command
                final boolean updateOptions = slashCommand.getOptions().stream().anyMatch(option -> {
                    // Get a matching argument
                    final Optional<SlashCommandArgument> matchingArgument = slashCommandArguments.stream().filter(argument -> option.getName().equals(argument.name())).findFirst();
                    if (matchingArgument.isEmpty()) return true;

                    // Create JSONObject for argument
                    final JSONObject json = new JSONObject()
                            .put("name", matchingArgument.get().name())
                            .put("description", matchingArgument.get().description())
                            .put("type", matchingArgument.get().optionType().getKey());

                    // Slash command argument has choices to choose from
                    if (matchingArgument.get().optionType().canSupportChoices()) {
                        final JSONArray choices = new JSONArray(); // Create JSONArray for choices
                        matchingArgument.get().choices().forEach(choice -> {
                            System.out.println(choice.getAsLong());
                            System.out.println(choice.getAsString());
                            System.out.println("--------------------------------------");
                            choices.put(new JSONObject().put(choice.getName(), choice.getAsLong())); // Add choice to JSONArray
                        });
                    }
                    final Command.Option currentOption = new Command.Option(DataObject.fromJson(json.toString())); // Create Option out of JSONObject
                    return !option.equals(currentOption);
                });

                // A option isn't up to date
                if (updateOptions) {
                    editAction = editAction.clearOptions();
                    for (SlashCommandArgument arg : slashCommandArguments) {
                        OptionData newArgument = new OptionData(arg.optionType(), arg.name(), arg.description(), arg.required()); // Create argument
                        // Loop through all choices
                        for (Command.Choice choice : arg.choices()) {
                            // Value is a number
                            if (choice.getAsString().matches("\\d")) {
                                newArgument = newArgument.addChoices(new Command.Choice(choice.getName(), choice.getAsLong())); // Add choice with long
                            }
                            // Value isn't a number
                            else {
                                newArgument = newArgument.addChoices(new Command.Choice(choice.getName(), choice.getAsString())); // Add choice with string
                            }
                        }
                        editAction = editAction.addOptions(newArgument);
                    }
                }

                if (!editAction.equals(jda.editCommandById(slashCommand.getId()))) {
                    editAction.queue(); // Push all changes of the slash command
                    updatedSlashCommands.getAndIncrement();
                }

                missingSlashCommands.remove(matchingRegisteredSlashCommand.get()); // Remove current slash command from the ones which we need to add
            }
        }


        currentDiscordSlashCommands.forEach(slashCommand -> {


        });

        // Register not registered slash commands
        missingSlashCommands.stream().map(SlashCommandData::getSlashCommand).forEach(slashCommand -> {
            jda.upsertCommand(slashCommand.name(), slashCommand.description()).queue(); // Add slash command
            addedSlashCommands.getAndIncrement();
        });

        LOGGER.info("Added " + addedSlashCommands.get() + " Slash Commands");
        LOGGER.info("Updated " + updatedSlashCommands.get() + " Slash Commands");
        LOGGER.info("Deleted " + deletedSlashCommands.get() + " Slash Commands");
    });*/
            }
        }
    }

    /**
     * Runs once a message received.
     *
     * @param event The MessageReceivedEvent.
     * @throws Exception Any exceptions will be thrown to the {@link CommandListener}.
     */
    void processSlashCommandExecution(net.dv8tion.jda.api.events.interaction.SlashCommandEvent event) throws Exception;

}
