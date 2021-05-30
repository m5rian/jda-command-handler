package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessageFactory;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandUsageFactory;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Marian
 * The builder for {@link DefaultCommandService}.
 */
public class DefaultCommandServiceBuilder {
    private String defaultPrefix;
    private Function<Guild, String> customPrefix;
    private boolean allowMention = false;
    private CommandMessageFactory infoFactory;
    private List<String> userBlacklist;
    private CommandMessageFactory warningFactory;
    private CommandMessageFactory errorFactory;
    private CommandUsageFactory usageFactory;

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

    public DefaultCommandServiceBuilder setUserBlacklist(Supplier<List<String>> userBlacklist) {
        this.userBlacklist = userBlacklist.get();
        return this;
    }

    public DefaultCommandServiceBuilder setInfoFactory(CommandMessageFactory infoFactory) {
        this.infoFactory = infoFactory;
        return this;
    }

    public DefaultCommandServiceBuilder setWarningFactory(CommandMessageFactory infoFactory) {
        this.infoFactory = infoFactory;
        return this;
    }

    public DefaultCommandServiceBuilder setErrorFactory(CommandMessageFactory infoFactory) {
        this.infoFactory = infoFactory;
        return this;
    }

    public DefaultCommandServiceBuilder setUsageFactory(CommandUsageFactory usageFactory) {
        this.usageFactory = usageFactory;
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
                this.allowMention,
                this.userBlacklist,

                this.infoFactory,
                this.warningFactory,
                this.errorFactory,
                this.usageFactory
        );
    }
}