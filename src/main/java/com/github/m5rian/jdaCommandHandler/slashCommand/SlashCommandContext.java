package com.github.m5rian.jdaCommandHandler.slashCommand;

import com.github.m5rian.jdaCommandHandler.commandServices.IBlacklistService;
import com.github.m5rian.jdaCommandHandler.commandServices.ISlashCommandService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * @author Marian
 * <p>
 * This class contains all information about an executed slash command.
 */
public class SlashCommandContext {
    // Variables
    private final SlashCommandEvent event; // The actual event

    private final SlashCommandData slashCommandData; // The slash command service
    private final ISlashCommandService slashCommandService; // Used slash command service
    private final IBlacklistService blacklistService; // Used blacklist service

    /**
     * Constructor.
     *
     * @param event            The SlashCommandEvent.
     * @param slashCommandData Data of the fired {@link com.github.m5rian.jdaCommandHandler.slashCommand.SlashCommandEvent}.
     * @param service          The used CommandService.
     */
    public SlashCommandContext(SlashCommandEvent event, SlashCommandData slashCommandData, Object service) {
        this.event = event;
        this.slashCommandData = slashCommandData;
        this.slashCommandService = (ISlashCommandService) service;
        this.blacklistService = (IBlacklistService) service;
    }

    public ReplyAction reply(String content) {
        return this.event.reply(content);
    }

    public ReplyAction reply(Message message) {
        return this.event.reply(message);
    }

    public ReplyAction replyEmbeds(Collection<? extends MessageEmbed> embeds) {
        return this.event.replyEmbeds(embeds);
    }

    public ReplyAction replyEmbeds(MessageEmbed embed, MessageEmbed... embeds) {
        return this.event.replyEmbeds(embed, embeds);
    }

    /**
     * @return Returns {@link SlashCommandEvent}.
     */
    public SlashCommandEvent getEvent() {
        return this.event;
    }

    /**
     * @return Returns the {@link JDA}.
     */
    public JDA getBot() {
        return this.event.getJDA();
    }

    /**
     * @return Returns the {@link JDA} as a {@link Member}. If the command was fired in a direct message this method will return null.
     */
    @Nullable
    public Member getBotMember() {
        return this.getGuild().getSelfMember();
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
     * If you want to get the {@link net.dv8tion.jda.api.entities.TextChannel}
     * parse it manually. This class doesn't provide this information, because
     * the command could be fired in direct messages too.
     */
    public MessageChannel getChannel() {
        return this.event.getChannel();
    }

    /**
     * @return Returns the author as a {@link User}.
     */
    public User getUser() {
        return this.event.getUser();
    }

    /**
     * @return Return the author as a {@link Member}.
     */
    public Member getMember() {
        return this.event.getMember();
    }

    /**
     * @return Returns the current blacklisted members with their ID.
     */
    public List<String> getBlacklist() {
        return this.blacklistService.userBlacklist;
    }

    /**
     * @return Returns the {@link com.github.m5rian.jdaCommandHandler.slashCommand.SlashCommandEvent} of the invoked method.
     */
    public com.github.m5rian.jdaCommandHandler.slashCommand.SlashCommandEvent getSlashCommand() {
        return this.slashCommandData.getSlashCommand();
    }

    /**
     * @return Returns the used {@link ISlashCommandService}.
     */
    public ISlashCommandService getSlashCommandService() {
        return this.slashCommandService;
    }
}
