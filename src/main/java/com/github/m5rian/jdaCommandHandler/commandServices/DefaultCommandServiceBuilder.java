package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.CommandUtils;
import com.github.m5rian.jdaCommandHandler.command.CommandData;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessageFactory;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandUsageFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;

/**
 * @author Marian
 * The builder for {@link DefaultCommandService}.
 */
@SuppressWarnings("unused")
public class DefaultCommandServiceBuilder {
    private final List<CommandHandler> commands = new ArrayList<>();
    private final List<CommandHandler> slashCommands = new ArrayList<>();

    private String defaultPrefix;
    private Function<Guild, String> customPrefix;
    private boolean allowMention = false;

    private List<String> userBlacklist = new ArrayList<>();

    private BiFunction<MessageReceivedEvent, CommandData, Boolean> customCheck;
    private BiConsumer<MessageReceivedEvent, Throwable> errorHandler;

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
     * @return Returns {@link DefaultCommandServiceBuilder} for chaining purpose.
     */
    public DefaultCommandServiceBuilder setVariablePrefix(Function<Guild, String> prefix) {
        this.customPrefix = prefix;
        return this;
    }

    /**
     * This method allows the bot to respond not only on commands,
     * the bot will then also respond at his mention.
     *
     * @return Returns {@link DefaultCommandServiceBuilder} for chaining purpose.
     */
    public DefaultCommandServiceBuilder allowMention() {
        this.allowMention = true;
        return this;
    }

    public DefaultCommandServiceBuilder registerCommandClass(CommandHandler clazz) {
        this.commands.add(clazz);
        return this;
    }

    public DefaultCommandServiceBuilder registerCommandClasses(CommandHandler... classes) {
        this.commands.addAll(Arrays.asList(classes));
        return this;
    }

    public DefaultCommandServiceBuilder registerSlashCommandClass(CommandHandler clazz) {
        this.slashCommands.add(clazz);
        return this;
    }

    public DefaultCommandServiceBuilder registerSlashCommandClasses(CommandHandler... classes) {
        this.slashCommands.addAll(Arrays.asList(classes));
        return this;
    }

    public DefaultCommandServiceBuilder setUserBlacklist(Supplier<List<String>> userBlacklist) {
        this.userBlacklist.addAll(userBlacklist.get());
        return this;
    }

    public DefaultCommandServiceBuilder setInfoFactory(CommandMessageFactory infoFactory) {
        CommandUtils.infoFactory = infoFactory;
        return this;
    }

    public DefaultCommandServiceBuilder setWarningFactory(CommandMessageFactory warningFactory) {
        CommandUtils.warningFactory = warningFactory;
        return this;
    }

    public DefaultCommandServiceBuilder setErrorFactory(CommandMessageFactory errorFactory) {
        CommandUtils.errorFactory = errorFactory;
        return this;
    }

    public DefaultCommandServiceBuilder setUsageFactory(CommandUsageFactory usageFactory) {
        CommandUtils.usageFactory = usageFactory;
        return this;
    }

    /**
     * @param error {@link Consumer} which contains the {@link Exception} as a parameter.
     * @return Returns {@link DefaultCommandServiceBuilder} for chaining purpose.
     */
    public DefaultCommandServiceBuilder handleErrors(BiConsumer<MessageReceivedEvent, Throwable> error) {
        this.errorHandler = error;
        return this;
    }

    /**
     * @param check A {@link Function<MessageReceivedEvent, Boolean>}, which is fired before the command gets executed.
     *              If the returned value is false, the command execution will get interrupted.
     * @return Returns {@link DefaultCommandServiceBuilder} for chaining purpose.
     */
    public DefaultCommandServiceBuilder addCheck(BiFunction<MessageReceivedEvent, CommandData, Boolean> check) {
        this.customCheck = check;
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

                this.commands,
                this.slashCommands,

                this.userBlacklist,

                this.errorHandler,
                this.customCheck
        );
    }
}