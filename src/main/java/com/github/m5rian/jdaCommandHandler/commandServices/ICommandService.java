package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.CommandListener;
import com.github.m5rian.jdaCommandHandler.EventWaiter;
import com.github.m5rian.jdaCommandHandler.command.CommandData;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    List<CommandData> commands = new ArrayList<>();

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
                final CommandData commandData = new CommandData(object, method, annotation); // Create method info object
                this.commands.add(commandData); // Put command in list
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
        this.commands.forEach(methodInfo -> {
            // Instance is equal to given object
            if (methodInfo.getInstance() == object) {
                this.commands.remove(methodInfo); // Remove method
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
    default List<CommandData> getCommands() {
        return this.commands;
    }

    /**
     * Runs once a message received.
     *
     * @param event The MessageReceivedEvent.
     * @throws Exception Any exceptions will be thrown to the {@link CommandListener}.
     */
    void processCommandExecution(MessageReceivedEvent event) throws Exception;

}
