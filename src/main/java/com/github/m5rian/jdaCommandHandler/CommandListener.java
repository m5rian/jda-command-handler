package com.github.m5rian.jdaCommandHandler;

import com.github.m5rian.jdaCommandHandler.commandServices.ICommandService;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * @author Marian
 * <p>
 * This class is responsable for running {@link ICommandService#processCommandExecution(MessageReceivedEvent)}.
 */
public class CommandListener extends ListenerAdapter {
    private final ICommandService commandService; // A command service

    /**
     * @param commandService A command service.
     */
    public CommandListener(ICommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * Runs once a MessageReceivedEvent is fired.
     * On that event the {@link ICommandService#processCommandExecution(MessageReceivedEvent)} will fire too.
     *
     * @param event The MessageReceivedEvent.
     */
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        try {
            commandService.processCommandExecution(event);
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    /**
     * Fires once the bot is fully loaded up.
     *
     * @param event A {@link ReadyEvent}.
     */
    public void onReady(ReadyEvent event) {
        event.getJDA().addEventListener(
                this.commandService.getEventWaiter() // Register event waiter
        );
    }
}