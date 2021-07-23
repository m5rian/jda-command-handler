package com.github.m5rian.jdaCommandHandler;

import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.slashCommand.Argument;
import com.github.m5rian.jdaCommandHandler.slashCommand.Choice;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

}
