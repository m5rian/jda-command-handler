package com.github.m5rian.jdaCommandHandler;

import com.github.m5rian.jdaCommandHandler.commandServices.ICommandService;
import com.github.m5rian.jdaCommandHandler.commandServices.ISlashCommandService;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author Marian
 * <p>
 * This class is responsable for running {@link ICommandService#processCommandExecution(MessageReceivedEvent)}.
 */
public class CommandListener extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(ICommandService.class);
    private final ICommandService commandService; // A command service
    private final ISlashCommandService slashCommandService; // A command service for slash commands

    /**
     * @param commandService A command service.
     */
    public CommandListener(Object commandService) {
        this.commandService = (ICommandService) commandService;
        this.slashCommandService = (ISlashCommandService) commandService;
    }


    /**
     * Fires once the bot is fully loaded up.
     *
     * @param event A {@link ReadyEvent}.
     */
    @Override
    public void onReady(ReadyEvent event) {
        this.slashCommandService.pushChanges(event.getJDA()); // Push slash command changes to Discord|
        event.getJDA().addEventListener(this.commandService.getEventWaiter()); // Register event waiter

        LOGGER.info("Bot started successfully");
        LOGGER.info("Loaded " + this.commandService.getCommands().size() + " commands");
        event.getJDA().retrieveCommands().queue(slashCommands -> LOGGER.info("Loaded " + slashCommands.size() + " slash commands"));
    }

    /**
     * Runs once a SlashCommandEvent is fired.
     * On that event the {@link ISlashCommandService#processSlashCommandExecution(SlashCommandEvent)} will fire too.
     *
     * @param event The SlashCommandEvent.
     */
    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        try {
            this.slashCommandService.processSlashCommandExecution(event);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Runs once a MessageReceivedEvent is fired.
     * On that event the {@link ICommandService#processCommandExecution(MessageReceivedEvent)} will fire too.
     *
     * @param event The MessageReceivedEvent.
     */
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        try {
            this.commandService.processCommandExecution(event);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}