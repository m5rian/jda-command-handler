package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.CommandListener;
import com.github.m5rian.jdaCommandHandler.CommandUtils;
import com.github.m5rian.jdaCommandHandler.slashCommand.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import org.json.JSONArray;
import org.json.JSONObject;
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
        List<SlashCommandData> commandsToRegister = new ArrayList<>(this.slashCommands);
        System.out.println("before: " + commandsToRegister.size());

        Route.CompiledRoute route = Route.Interactions.GET_COMMANDS.compile(jda.getSelfUser().getApplicationId());
        RestActionImpl<DataArray> restAction = new RestActionImpl<>(jda, route, (res, req) -> res.getArray());
        restAction.queue(json -> {
            final JSONArray commands = new JSONArray(json);
            for (int i = 0; i < commands.length(); i++) {
                final JSONObject command = commands.getJSONObject(i);
                final String id = command.getString("id"); // Get id of command
                // Remove not used variables by my command handler
                command.remove("default_permission");
                command.remove("id");
                command.remove("type");
                command.remove("application_id");
                command.remove("version");
                final String name = command.getString("name");

                // Try finding a matching slash command from my command handler
                final Optional<SlashCommandData> handlerCommandData = this.slashCommands.stream().filter(slashCommandData -> slashCommandData.getSlashCommand().name().equals(name)).findFirst();
                // Command doesn't exist anymore
                if (handlerCommandData.isEmpty()) {
                    LOGGER.info("Deleting slash command: " + command.getString("name"));
                    jda.deleteCommandById(id).queue(); // Delete command
                    continue; // Stop further code and continue with next command item
                }

                if (handlerCommandData.isPresent()) {
                    System.out.println("command already exists !!!!!!!!!");
                    commandsToRegister.remove(handlerCommandData.get());
                    System.out.println("afteer: " + commandsToRegister.size());
                }

                final SlashCommandEvent handlerCommand = handlerCommandData.get().getSlashCommand(); // Get handler command with all important data
                final JSONObject handlerJson = CommandUtils.commandToJson(handlerCommand);
                // Command didn't change
                if (handlerJson.equals(command)) continue;

                System.out.println(handlerJson);
                System.out.println(command);

                CommandEditAction editAction = jda.editCommandById(id); // Create edit action
                editAction = editAction.setDescription(handlerCommand.description()); // Update description

                // Any option changed
                if (!command.getJSONArray("options").toList().equals(handlerJson.getJSONArray("options").toList())) {
                    LOGGER.info(handlerCommand.name() + "'s options have changed");
                    editAction = editAction.clearOptions();
                    if (handlerCommand.args().length != 0) for (Argument argument : handlerCommand.args()) {
                        editAction = editAction.addOptions(CommandUtils.argumentToOptionData(argument));
                    }
                    if (handlerCommand.subcommands().length != 0) for (Subcommand subcommand : handlerCommand.subcommands()) {
                        editAction = editAction.addSubcommands(CommandUtils.subcommandToSubcommandData(subcommand));
                    }
                    if (handlerCommand.subcommandsSets().length != 0) for (SubcommandSet subcommandSet : handlerCommand.subcommandsSets()) {
                        editAction = editAction.addSubcommandGroups(CommandUtils.subcommandSetToSubcommandGroupData(subcommandSet));
                    }
                }

                editAction.queue(); // Execute editing action
            }

            for (SlashCommandData slashCommandData : commandsToRegister) {
                LOGGER.info("Registering slash command: " + slashCommandData.getSlashCommand().name());
                // Register slash command in discord
                jda.upsertCommand(CommandUtils.slashCommandEventToCommandData(slashCommandData.getSlashCommand())).queue();
            }
        });
    }
        /*
        final List<String> deleted = Storage.SlashCommands.getIds(); // Slash command ids which don't exist anymore
        final List<SlashCommandData> missing = new ArrayList<>(this.slashCommands); // Commands which aren't registered yet

        jda.retrieveCommands().queue(commands -> {
            for (Command discordCommand : commands) {
                final String commandId = discordCommand.getId();

                // Slash command is valid
                if (Storage.SlashCommands.exists(commandId)) {
                    LOGGER.info(String.format("%s has invalid data, updating", discordCommand.getName()));

                    // Remove slash command from missing slash commands
                    missing.remove(missing.stream().filter(slashCommandData -> slashCommandData.getSlashCommand().name().equals(discordCommand.getName())).findFirst().get());
                    deleted.remove(commandId); // Remove slash command id from deleted ones

                    final String json = Storage.SlashCommands.read(commandId);
                    final Command command = new Command((JDAImpl) jda, null, DataObject.fromJson(json));
                    if (command.equals(discordCommand)) continue;

                    CommandEditAction editAction = jda.editCommandById(commandId); // Create edit action

                    // Update name
                    if (discordCommand.getName().equals(command.getName())) {
                        editAction = editAction.setName(command.getName());
                    }
                    /// Update description
                    if (discordCommand.getDescription().equals(command.getDescription())) {
                        editAction = editAction.setDescription(command.getDescription());
                    }

                    // Any option changed
                    if (!discordCommand.getOptions().equals(command.getOptions()) || !discordCommand.getSubcommands().equals(command.getSubcommands()) || !discordCommand.getSubcommandGroups().equals(command.getSubcommandGroups())) {
                        LOGGER.info(String.format("%s has invalid options, updating", discordCommand.getName()));
                        editAction = editAction.clearOptions()
                                .addOptions(CommandUtils.optionsToOptionData(command.getOptions()))
                                .addSubcommands(CommandUtils.subcommandsToSubcommandData(command.getSubcommands()))
                                .addSubcommandGroups(CommandUtils.subcommandGroupsToSubcommandGroupData(command.getSubcommandGroups()));
                    }

                    editAction.queue(slashCommand -> {
                        final SlashCommandData slashCommandData = this.slashCommands.stream().filter(s -> s.getSlashCommand().name().equals(slashCommand.getName())).findFirst().get();
                        final JSONObject updatedJson = CommandUtils.commandToJson(slashCommandData.getSlashCommand())
                                .put("id", slashCommand.getId());// Add id to slash command json
                        Storage.SlashCommands.write(slashCommand.getId(), json);
                    }); // Execute editing action
                    CommandEditAction editAction = jda.editCommandById(commandId); // Create edit action

                    // Update name
                    if (discordCommand.getName().equals(command.getName())) {
                        editAction = editAction.setName(command.getName());
                    }
                    /// Update description
                    if (discordCommand.getDescription().equals(command.getDescription())) {
                        editAction = editAction.setDescription(command.getDescription());
                    }

                    // Any option changed
                    if (!discordCommand.getOptions().equals(command.getOptions()) || !discordCommand.getSubcommands().equals(command.getSubcommands()) || !discordCommand.getSubcommandGroups().equals(command.getSubcommandGroups())) {
                        LOGGER.info(String.format("%s has invalid options, updating", discordCommand.getName()));
                        editAction = editAction.clearOptions()
                                .addOptions(CommandUtils.optionsToOptionData(command.getOptions()))
                                .addSubcommands(CommandUtils.subcommandsToSubcommandData(command.getSubcommands()))
                                .addSubcommandGroups(CommandUtils.subcommandGroupsToSubcommandGroupData(command.getSubcommandGroups()));
                    }

                    editAction.queue(slashCommand -> {
                        final SlashCommandData slashCommandData = this.slashCommands.stream().filter(s -> s.getSlashCommand().name().equals(slashCommand.getName())).findFirst().get();
                        final JSONObject updatedJson = CommandUtils.commandToJson(slashCommandData.getSlashCommand())
                                .put("id", slashCommand.getId());// Add id to slash command json
                        Storage.SlashCommands.write(slashCommand.getId(), json);
                    }); // Execute editing action
                }
                // Slash command doesn't exist anymore
                else {
                    discordCommand.delete().queue(); // Delete command
                }
            }
        });

        // Register missing slash commands
        for (SlashCommandData slashCommandData : missing) {
            // Register slash command in discord
            jda.upsertCommand(CommandUtils.slashCommandEventToCommandData(slashCommandData.getSlashCommand())).queue(slashCommand -> {
                LOGGER.info(String.format("Registered slash command %s", slashCommand.getName()));

                deleted.remove(slashCommand.getId()); // Remove slash command id from deleted ones
                // Save slash command in file
                final JSONObject json = CommandUtils.commandToJson(slashCommandData.getSlashCommand())
                        .put("id", slashCommand.getId());// Add id to slash command json
                Storage.SlashCommands.write(slashCommand.getId(), json.toString());
            });
        }

        for (String id : deleted) {
            Storage.SlashCommands.delete(id); // Delete unused files
        }
    }

/*
        Route.CompiledRoute route = Route.Interactions.GET_COMMANDS.compile(jda.getSelfUser().getApplicationId());
        RestActionImpl<DataArray> restAction = new RestActionImpl<>(jda, route, (res, req) -> res.getArray());
        restAction.queue(json -> {      Route.CompiledRoute route = Route.Interactions.GET_COMMANDS.compile(jda.getSelfUser().getApplicationId());
        RestActionImpl<DataArray> restAction = new RestActionImpl<>(jda, route, (res, req) -> res.getArray());
        restAction.queue(json -> {
            if (!Storage.SlashCommands.exists()) {
                Storage.SlashCommands.create();
            }

            System.out.println(json.toString());
            final String[] slashCommands = Storage.SlashCommands.read();
            final JSONArray discordCommands = new JSONArray(json.toString());

            for (int i = 0; i < discordCommands.length(); i++) {
                final JSONObject discordCommand = discordCommands.getJSONObject(i).equals()

                this.slashCommands.stream().filter(slashCommand -> {
                    final JSONObject slashCommand = CommandUtils.commandToJson(slashCommand.getSlashCommand());
                    return true;
                });
            }


            try {
                File folder = new File("commandHandler");
                if (!folder.exists()) {
                    folder.mkdir();

                    File file = new File("commandHandler/slashCommands.txt");
                    if (!file.exists()) file.createNewFile();
                }
                PrintWriter writer = new PrintWriter("commandHandler/slashCommands.txt", StandardCharsets.UTF_8);
                writer.println("857981589137129472:868811029528854548");
                writer.close();

                final String s = new String(Files.readAllBytes(Path.of("commandHandler/slashCommands.txt")));
                System.out.println(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


      /*  int updatedSlashCommands = 0;
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

                // Description has changed
                if (!discordSlashCommand.getDescription().equals(description)) {
                    editAction = editAction.setDescription(description); // Update description of command
                }


                final boolean equalsOptions = CommandUtils.argumentsToOptions(slashCommandEvent.args()).equals(discordSlashCommand.getOptions());
                final boolean equalsSubcommands = CommandUtils.subcommandsToSubcommands(slashCommandEvent.subcommands()).equals(discordSlashCommand.gets());
                final boolean equalsSubcommandGroups = CommandUtils.subcommandSetsToSubcommandGroups(slashCommandEvent.subcommandsSets()).equals(discordSlashCommand.getSubcommandGroups());

                // A option doesn't match anymore
                if (!equalsOptions || !equalsSubcommands || !equalsSubcommandGroups) {
                    editAction = editAction.clearOptions(); // Clear options

                    final List<OptionData> optionData = CommandUtils.optionsToOptionData(CommandUtils.argumentsToOptions(slashCommandEvent.args()));
                    final List<SubcommandData> subcommandData = CommandUtils.subcommandsToSubcommandData(CommandUtils.subcommandsToSubcommands(slashCommandEvent.subcommands()));
                    final List<SubcommandGroupData> subcommandGroupData = CommandUtils.subcommandGroupsToSubcommandGroupData(CommandUtils.subcommandSetsToSubcommandGroups(slashCommandEvent.subcommandsSets()));

                    editAction = editAction
                            .addOptions(optionData)
                            .addSubcommands(subcommandData)
                            .addSubcommandGroups(subcommandGroupData);
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
                final SubcommandGroupData subcommandGroupData = CommandUtils.subcommandSetToSubcommandGroupData(subcommandSet);
                command.addSubcommandGroups(subcommandGroupData); // Add subcommand set data to command
            }
            // Add all subcommands
            for (Subcommand subcommand : slashCommand.subcommands()) {
                final SubcommandData subcommandData = CommandUtils.subcommandToSubcommandData(subcommand);
                command.addSubcommands(subcommandData); // Add subcommand to slash command
            }
            // Add all normal type arguments
            for (Argument argument : slashCommandData.getSlashCommand().args()) {
                final OptionData optionData = CommandUtils.argumentToOptionData(argument);
                command.addOptions(optionData);
            }

            jda.upsertCommand(command).queue(); // Add command to discord
        });*/

    /**
     * Runs once a message received.
     *
     * @param event The MessageReceivedEvent.
     * @throws Exception Any exceptions will be thrown to the {@link CommandListener}.
     */
    void processSlashCommandExecution(net.dv8tion.jda.api.events.interaction.SlashCommandEvent event) throws Exception;

}
