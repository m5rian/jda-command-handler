package com.github.m5rian.jdaCommandHandler;

import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.slashCommand.*;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUtils {

    /**
     * @param method The command method
     * @return Returns a list of all executors from a command. The command doesn't need to be registered.
     */
    public static List<String> getCommandExecutors(Method method) {
        final CommandEvent commandInfo = method.getAnnotation(CommandEvent.class); // Get annotation
        final List<String> executors = new ArrayList<>(); // Create list for executors

        executors.add(commandInfo.name()); // Add command name
        Collections.addAll(executors, commandInfo.aliases()); // Add aliases

        return executors; // Return list with all executors
    }


    public static CommandData slashCommandEventToCommandData(SlashCommandEvent slashCommand) {
        final CommandData commandData = new CommandData(slashCommand.name(), slashCommand.description());

        for (Argument arg : slashCommand.args()) {
            commandData.addOptions(CommandUtils.argumentToOptionData(arg));
        }

        for (Subcommand subcommand : slashCommand.subcommands()) {
            commandData.addSubcommands(CommandUtils.subcommandToSubcommandData(subcommand));
        }

        for (SubcommandSet subcommandSet : slashCommand.subcommandsSets()) {
            commandData.addSubcommandGroups(CommandUtils.subcommandSetToSubcommandGroupData(subcommandSet));
        }

        return commandData;
    }


    /**
     * Convert a {@link Argument} to JDAs {@link OptionData}.
     *
     * @param argument A {@link Argument} from {@link SlashCommandEvent#args()}.
     * @return Returns a {@link OptionData}.
     */
    public static OptionData argumentToOptionData(Argument argument) {
        // Create base option data
        final OptionData optionData = new OptionData(argument.type(), argument.name(), argument.description(), argument.required());
        // Add all choices to option
        for (Choice choice : argument.choices()) {
            optionData.addChoice(choice.name(), choice.value()); // Add option
        }
        return optionData; // Return finished option data
    }

    /**
     * Convert a {@link Subcommand} to JDAs {@link SubcommandData}.
     *
     * @param subcommand A {@link Subcommand} from {@link SlashCommandEvent#subcommands()}.
     * @return Returns a {@link SubcommandData}.
     */
    public static SubcommandData subcommandToSubcommandData(Subcommand subcommand) {
        // Create base subcommand data
        final SubcommandData subcommandData = new SubcommandData(subcommand.name(), subcommand.description());
        // Add all arguments of subcommand
        for (Argument argument : subcommand.args()) {
            final OptionData optionData = argumentToOptionData(argument); // Convert argument to option data
            subcommandData.addOptions(optionData); // Add option to subcommand
        }
        return subcommandData; // Return finished subcommand data
    }

    /**
     * Convert a {@link SubcommandSet} to JDAs {@link SubcommandGroupData}.
     *
     * @param subcommandSet A {@link SubcommandSet} from {@link SlashCommandEvent#subcommandsSets()}.
     * @return Returns a {@link SubcommandGroupData}.
     */
    public static SubcommandGroupData subcommandSetToSubcommandGroupData(SubcommandSet subcommandSet) {
        // Create base subcommand group data
        final SubcommandGroupData subcommandGroupData = new SubcommandGroupData(subcommandSet.name(), subcommandSet.description());
        // Add all subcommands to the subcommands set
        for (Subcommand subcommand : subcommandSet.subcommands()) {
            final SubcommandData subcommandData = subcommandToSubcommandData(subcommand); // Convert subcommand to subcommand data
            subcommandGroupData.addSubcommands(subcommandData); // Add subcommand to subcommand group data
        }
        return subcommandGroupData; // Return finished subcommand group data
    }


    /**
     * Convert {@link Argument}s to JDAs {@link Command.Option}s.
     *
     * @param arguments An array of {@link Argument}s from {@link SlashCommandEvent#args()}.
     * @return Returns a list of {@link Command.Option}s.
     */
    public static List<Command.Option> argumentsToOptions(Argument[] arguments) {
        final List<Command.Option> options = new ArrayList<>(); // Create list for options

        for (Argument argument : arguments) {
            // Create JSONObject for argument
            final JSONObject json = new JSONObject()
                    .put("name", argument.name()) // Name of option
                    .put("description", argument.description()) // Description of option
                    .put("type", argument.type().getKey()); // Data type of option

            // Slash command argument has predefined choose to choose from
            if (argument.type().canSupportChoices()) {
                final JSONArray choices = new JSONArray(); // Create JSONArray for choices
                // Add all choices to JSONArray
                for (Choice choice : argument.choices()) {
                    choices.put(new JSONObject()
                            .put("name", choice.name()) // Name of choice
                            .put("value", choice.value())); // Value of choice
                }
                json.put("choices", choices); // Add choices to command
            }

            // Create Option out of JSONObject
            final Command.Option option = new Command.Option(DataObject.fromJson(json.toString()));
            options.add(option); // Add option to options list
        }
        return options;
    }

    /**
     * Convert {@link Subcommand}s to JDAs {@link Command.Subcommand}s.
     *
     * @param subcommands An array of {@link Subcommand}s from {@link SlashCommandEvent#subcommands()}.
     * @return Returns a list of {@link Command.Subcommand}s.
     */
    public static List<Command.Subcommand> subcommandsToSubcommands(Subcommand[] subcommands) {
        final List<Command.Subcommand> discordSubcommands = new ArrayList<>(); // Create list for subcommands

        for (Subcommand subcommand : subcommands) {
            // Create JSONObject for subcommand
            final JSONObject json = new JSONObject()
                    .put("name", subcommand.name()) // Name of subcommand
                    .put("description", subcommand.description()) // Description of subcommand
                    .put("type", 1); // 1 is type SUB_COMMAND

            final JSONArray options = new JSONArray(); // JSONArray for options
            // For each option of subcommand
            for (Argument argument : subcommand.args()) {
                final JSONObject option = new JSONObject()
                        .put("name", argument.name()) // Name of the option
                        .put("description", argument.description()) // Description of the option
                        .put("type", argument.type().getKey()) // Data type of the option
                        .put("required", argument.required()); // Is the option required to be used

                //  options                .put("options", new JSONArray().putAll(options));
            }
            // Create Option out of JSONObject
            final Command.Subcommand discordSubcommand = new Command.Subcommand(DataObject.fromJson(json.toString()));
            discordSubcommands.add(discordSubcommand); // Add subcommand to subcommands list
        }
        return discordSubcommands;
    }

    /**
     * Convert {@link SubcommandSet}s to JDAs {@link Command.SubcommandGroup}s.
     *
     * @param subcommandSets An array of {@link SubcommandSet}s from {@link SlashCommandEvent#subcommandsSets()}.
     * @return Returns a list of {@link Command.SubcommandGroup}s.
     */
    public static List<Command.SubcommandGroup> subcommandSetsToSubcommandGroups(SubcommandSet[] subcommandSets) {
        final List<Command.SubcommandGroup> subcommandGroups = new ArrayList<>(); // Create list for subcommand groups

        for (SubcommandSet subcommandSet : subcommandSets) {
            // Convert subcommands to discords subcommands
            final List<Command.Subcommand> subcommands = subcommandsToSubcommands(subcommandSet.subcommands());

            // Create JSONObject for subcommand set
            final JSONObject json = new JSONObject()
                    .put("name", subcommandSet.name()) // Name of subcommand
                    .put("description", subcommandSet.description()) // Description of subcommand
                    .put("type", 2) // 2 is type SUB_COMMAND_GROUP
                    .put("options", new JSONArray().putAll(subcommands)); // Available subcommands of subcommand set

            // Create subcommand group out of JSONObject
            final Command.SubcommandGroup subcommandGroup = new Command.SubcommandGroup(DataObject.fromJson(json.toString()));
            subcommandGroups.add(subcommandGroup); // Add subcommand group to subcommand groups list
        }
        return subcommandGroups;
    }


    public static List<OptionData> optionsToOptionData(List<Command.Option> options) {
        return options.stream()
                .map(option -> {
                    final OptionData optionData = new OptionData(option.getType(), option.getName(), option.getDescription(), option.isRequired());
                    option.getChoices().forEach(choice -> optionData.addChoice(choice.getName(), choice.getAsString())); // Add all choices
                    return optionData;
                })
                .collect(Collectors.toList());
    }

    public static List<SubcommandData> subcommandsToSubcommandData(List<Command.Subcommand> subcommands) {
        return subcommands.stream()
                .map(subcommand -> {
                    final SubcommandData subcommandData = new SubcommandData(subcommand.getName(), subcommand.getDescription());
                    final List<OptionData> optionData = optionsToOptionData(subcommand.getOptions()); // Convert options of subcommand to option data
                    subcommandData.addOptions(optionData); // Add all option data
                    return subcommandData;
                })
                .collect(Collectors.toList());
    }

    public static List<SubcommandGroupData> subcommandGroupsToSubcommandGroupData(List<Command.SubcommandGroup> subcommandGroups) {
        return subcommandGroups.stream()
                .map(subcommandGroup -> {
                    final SubcommandGroupData subcommandGroupData = new SubcommandGroupData(subcommandGroup.getName(), subcommandGroup.getDescription());
                    final List<SubcommandData> subcommandData = subcommandsToSubcommandData(subcommandGroup.getSubcommands());
                    subcommandGroupData.addSubcommands(subcommandData);
                    return subcommandGroupData;
                })
                .collect(Collectors.toList());
    }

    public static JSONObject commandToJson(SlashCommandEvent slashCommand) {
        final JSONObject json = new JSONObject()
                .put("name", slashCommand.name())
                .put("description", slashCommand.description());
        if (slashCommand.args().length != 0 || slashCommand.subcommands().length != 0 || slashCommand.subcommandsSets().length != 0) {
            json.put("options", new JSONArray()
                    .putAll(argumentsToJson(slashCommand.args())) // Add options
                    .putAll(subcommandsToJson(slashCommand.subcommands())) // Add subcommands
                    .putAll(subcommandSetsToJson(slashCommand.subcommandsSets()))); // Add subcommand sets
        }
        return json;
    }

    private static JSONArray argumentsToJson(Argument[] arguments) {
        final JSONArray options = new JSONArray();
        for (Argument argument : arguments) {
            options.put(new JSONObject()
                    .put("name", argument.name())
                    .put("description", argument.description())
                    .put("type", argument.type().getKey())
                    .put("required", argument.required()));
        }
        return options;
    }

    private static JSONArray subcommandsToJson(Subcommand[] subcommands) {
        final JSONArray options = new JSONArray();
        for (Subcommand subcommand : subcommands) {
            final JSONObject json = new JSONObject()
                    .put("name", subcommand.name())
                    .put("description", subcommand.description())
                    .put("type", OptionType.SUB_COMMAND.getKey());// 1 is type SUB_COMMAND
            if (subcommand.args().length != 0) json.put("options", argumentsToJson(subcommand.args()));
            options.put(json);
        }
        return options;
    }

    public static JSONArray subcommandSetsToJson(SubcommandSet[] subcommandSets) {
        final JSONArray options = new JSONArray();
        for (SubcommandSet subcommandSet : subcommandSets) {
            final JSONObject json = new JSONObject()
                    .put("name", subcommandSet.name())
                    .put("description", subcommandSet.description())
                    .put("type", OptionType.SUB_COMMAND_GROUP.getKey()); // 2 is type SUB_COMMAND_GROUP
            if (subcommandSet.subcommands().length != 0) json.put("options", subcommandsToJson(subcommandSet.subcommands()));
            options.put(json);
        }
        return options;
    }

/*
    public static CommandData commandToCommandData(Command command) {
        final CommandData commandData = new CommandData(command.getName(), command.getDescription());

        for (Command.Option option : command.getOptions()) {
            final OptionData commandOptionData = optionToOptionData(option);
            commandData.addOptions(commandOptionData);
        }

        return commandData;
    }

    public static OptionData optionToOptionData(Command.Option option) {
        final OptionData commandOptionData = new OptionData(option.getType(), option.getName(), option.getDescription(), option.isRequired());
        // Add all choices
        for (Command.Choice choice : option.getChoices()) {
            // Value is a number
            if (choice.getAsString().matches("\\d")) {
                final int value = (int) choice.getAsLong();
                commandOptionData.addChoice(choice.getName(), value); // Add choice with integer
            }
            // Value isn't a number
            else {
                commandOptionData.addChoice(choice.getName(), choice.getAsString()); // Add choice with string
            }
        }
        return commandOptionData;
    }

    public static Command.Option argumentToCommandOption(Argument argument) {
        // Create JSONObject for argument
        final JSONObject json = new JSONObject()
                .put("name", argument.name())
                .put("description", argument.description())
                .put("type", argument.type().getKey());

        // Slash command argument has choices to choose from
        if (argument.type().canSupportChoices()) {
            final JSONArray choices = new JSONArray(); // Create JSONArray for choices
            for (Choice choice : argument.choices()) {
                // Add choice to JSONArray
                choices.put(new JSONObject()
                        .put("name", choice.name()) // Name of choice
                        .put("value", choice.value())); // Value of choice
            }
            json.put("choices", choices); // Add choices to command
        }
        return new Command.Option(DataObject.fromJson(json.toString())); // Create Option out of JSONObject
    }
*/


}
