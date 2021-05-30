package com.github.m5rian.jdaCommandHandler;

import com.github.m5rian.jdaCommandHandler.commandServices.IBlacklistService;
import com.github.m5rian.jdaCommandHandler.commandServices.ICommandService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class contains all information about an executed command.
 */
@SuppressWarnings("unused")
public class CommandContext {
    // Variables
    private final String prefix; // Prefix used
    private final MessageReceivedEvent event; // The actual event
    private final String[] arguments; // Arguments, split in spaces
    private final String argumentsRaw; // Non-split arguments

    private final MethodInfo methodInfo; // The command service
    private final ICommandService commandService; // Used command service
    private final IBlacklistService blacklistService; // Used blacklist service

    private final EventWaiter waiter; // Event waiter

    /**
     * Constructor
     *
     * @param prefix    Used prefix.
     * @param event     The MessageReceivedEvent.
     * @param arguments message, without the executor.
     */
    public CommandContext(String prefix, MessageReceivedEvent event, String arguments, MethodInfo methodInfo, ICommandService commandService, IBlacklistService blacklistService) {
        this.prefix = prefix;
        this.event = event;
        if (arguments.equals("")) this.arguments = new String[0]; // If there no arguments, make an empty array
        else this.arguments = arguments.split("\\s+"); // If there are arguments, split them in spaces
        this.argumentsRaw = arguments;

        this.methodInfo = methodInfo;
        this.commandService = commandService;
        this.waiter = commandService.getEventWaiter();
        this.blacklistService = blacklistService;
    }

    /**
     * @return Returns the used prefix.
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * @return Returns MessageReceivedEvent.
     */
    public MessageReceivedEvent getEvent() {
        return this.event;
    }

    /**
     * @return Returns the Guild. If the command was activated in a direct message this method will return Null.
     */
    @Nullable
    public Guild getGuild() {
        return this.event.getGuild();
    }

    /**
     * @return Returns the channel, in which the command was executed.
     * <p>
     * You can't get the {@link Guild} out of it, because the event
     * can be fired in direct messages or in a guild. To get the {@link Guild}
     * you can use {@link CommandContext#getGuild()} (which can be null).
     */
    public MessageChannel getChannel() {
        return this.event.getChannel();
    }

    /**
     * @return Returns the author as a {@link User}.
     */
    public User getAuthor() {
        return this.event.getAuthor();
    }

    /**
     * @return Return the author as a {@link Member}.
     */
    public Member getMember() {
        return this.event.getMember();
    }

    /**
     * @return Returns the split arguments.
     */
    public String[] getArguments() {
        return this.arguments;
    }

    /**
     * @return Returns the arguments as a single string.
     */
    public String getArgumentsRaw() {
        return this.argumentsRaw;
    }

    public List<String> getBlacklist() {
        return this.blacklistService.blacklist;
    }

    void addUserToBlackList(User user) {
        this.blacklistService.addUserToBlackList(user);
    }

    void removeUserFromBlacklist(User user) {
        this.blacklistService.removeUserFromBlacklist(user);
    }

    /**
     * @return Returns the {@link MethodInfo} of the invoked method.
     */
    public MethodInfo getMethodInfo() {
        return this.methodInfo;
    }

    /**
     * @return Returns the used {@link ICommandService}.
     */
    public ICommandService getCommandService() {
        return this.commandService;
    }

    /**
     * @return Returns the event waiter.
     */
    public EventWaiter getWaiter() {
        return this.waiter;
    }
}
