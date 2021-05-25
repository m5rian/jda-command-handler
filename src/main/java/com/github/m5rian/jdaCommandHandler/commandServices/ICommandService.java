package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.*;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessage;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessageFactories;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandUsage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marian
 * <p>
 * This is the basic interface, which all CommandServices have implemented.
 */
@SuppressWarnings("unused")
public interface ICommandService {
    /**
     * Stores all registered commands.
     */
    Map<MethodInfo, CommandEvent> commands = new HashMap<>();
    /**
     * new one
     */
    List<MethodInfo> commandsNew = new ArrayList<>();

    CommandMessageFactories commandMessageFactories = new CommandMessageFactories();

    /**
     * Stores the {@link EventWaiter}.
     */
    EventWaiter eventWaiter = new EventWaiter();

    /**
     * @return Returns an {@link EventWaiter}
     */
    default EventWaiter getEventWaiter() {
        return eventWaiter;
    }

    /**
     * Register a class with command methods.
     * <p>A method which you wish to be a command must have the {@link CommandEvent} annotation</p>
     *
     * @param object The initialized object where command methods are.
     */
    default void registerCommandClass(CommandHandler object) {
        // Go through each method
        for (Method method : object.getClass().getMethods()) {
            // Method is command
            if (method.isAnnotationPresent(CommandEvent.class)) {
                final CommandEvent annotation = method.getAnnotation(CommandEvent.class); // Get command annotation
                final MethodInfo methodInfo = new MethodInfo(object, method, annotation); // Create method info object
                commandsNew.add(methodInfo); // Put command in list
            }
        }
    }

    /**
     * Register more than one class with command methods.
     * <p>A method which you wish to be a command must have the {@link CommandEvent} annotation</p>
     *
     * @param objects The initialized objects where command methods are.
     */
    default void registerCommandClasses(CommandHandler... objects) {
        // Go through each class
        for (CommandHandler object : objects) {
            registerCommandClass(object); // Register command
        }
    }

    /**
     * Unregister a command class.
     * <p>A method which you wish to be a command must have the {@link CommandEvent} annotation</p>
     *
     * @param object The initialized object where to unregister all command methods.
     */
    default void unregisterCommandClass(CommandHandler object) {
        // For every item
        this.commands.forEach((key, value) -> {
            // Instance is equal to given object
            if (key.getInstance() == object) {
                this.commands.remove(key); // Remove method
            }
        });
    }

    /**
     * Unregister all registered commands.
     */
    default void unregisterAllCommands() {
        this.commands.clear();
    }

    /**
     * @return Returns a Map with all registered commands.
     */
    default Map<MethodInfo, CommandEvent> getCommands() {
        return this.commands;
    }

    /**
     * Runs once a message received.
     *
     * @param event The MessageReceivedEvent.
     * @throws Exception Any exceptions will be thrown to the {@link CommandListener}.
     */
    void processCommandExecution(MessageReceivedEvent event) throws Exception;

    default CommandMessage executeInfo(CommandContext ctx) {
        return this.commandMessageFactories.getInfoFactory().invoke(ctx);
    }

    default CommandMessage executeWarn(CommandContext ctx) {
        return this.commandMessageFactories.getWarningFactory().invoke(ctx);
    }

    default CommandMessage executeError(CommandContext ctx) {
        return this.commandMessageFactories.getErrorFactory().invoke(ctx);
    }

    default CommandUsage executeUsage(CommandContext ctx) {
        return this.commandMessageFactories.getUsageFactory().invoke(ctx);
    }

}
