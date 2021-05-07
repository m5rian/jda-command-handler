package com.github.m5rian.jdaCommandHandler.commandServices;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.function.Function;

/**
 * @author Marian
 * The builder for {@link DefaultCommandService}.
 */
public class DefaultCommandServiceBuilder {
    private String defaultPrefix;
    private Function<Guild, String> customPrefix;
    private boolean allowMention = false;

    /**
     * Set the default prefix.
     * This prefix is used when:
     * <ul>
     *     <li>{@link DefaultCommandServiceBuilder#customPrefix} is not set.</li>
     *     <li>a message is received in the direct messages of the bot.</li>
     * </ul>
     *
     * @param prefix The prefix you need to type in before every command.
     * @return Returns {@link DefaultCommandServiceBuilder}.
     */
    public DefaultCommandServiceBuilder setDefaultPrefix(String prefix) {
        this.defaultPrefix = prefix;
        return this;
    }

    /**
     * Use this method to make guild specific prefixes.
     * The {@link DefaultCommandServiceBuilder#defaultPrefix} will be replaced with the default prefix.
     *
     * @param prefix A Function, which returns a guild specific prefix.
     * @return Returns {@link DefaultCommandServiceBuilder}.
     */
    public DefaultCommandServiceBuilder setVariablePrefix(Function<Guild, String> prefix) {
        this.customPrefix = prefix;
        return this;
    }

    /**
     * This method allows the bot to respond not only on commands,
     * the bot will then also respond at his mention.
     *
     * @return Returns {@link DefaultCommandServiceBuilder}.
     */
    public DefaultCommandServiceBuilder allowMention() {
        this.allowMention = true;
        return this;
    }

    /**
     * Build the command service.
     *
     * @return Returns a finished {@link DefaultCommandService}.
     */
    public DefaultCommandService build() {
        // Return command service
        return new DefaultCommandService(
                this.defaultPrefix,
                this.customPrefix,
                this.allowMention
        );
    }
}