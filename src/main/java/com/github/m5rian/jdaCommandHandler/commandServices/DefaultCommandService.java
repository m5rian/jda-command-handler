package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.CommandUtils;
import com.github.m5rian.jdaCommandHandler.Everyone;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandData;
import com.github.m5rian.jdaCommandHandler.command.CoolDown;
import com.github.m5rian.jdaCommandHandler.command.CoolDownData;
import com.github.m5rian.jdaCommandHandler.exceptions.NotRegisteredException;
import com.github.m5rian.jdaCommandHandler.slashCommand.SlashCommandContext;
import com.github.m5rian.jdaCommandHandler.slashCommand.SlashCommandData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A command service for default needs.
 */
public class DefaultCommandService implements ICommandService, ISlashCommandService, IPermissionService, IBlacklistService, ICoolDownService {
    private final String defaultPrefix;
    private final Function<Guild, String> customPrefix;
    private final boolean allowMention;
    private final boolean ignoreBots;
    private final boolean ignoreSystem;
    private final boolean ignoreWebhooks;
    private final BiConsumer<MessageReceivedEvent, Throwable> errorHandler;
    private final BiConsumer<MessageReceivedEvent, CoolDown> coolDownHandler;
    private final BiFunction<MessageReceivedEvent, CommandData, Boolean> customCheck;

    /**
     * Constructor
     * This constructor builds the actual DefaultCommandService
     *
     * @param defaultPrefix  The default prefix, which is used when a no variable prefix is set.
     *                       The default prefix is also used when you fire commands in the direct messages.
     * @param customPrefix   A variable prefix, which can depend on each guild.
     * @param allowMention   Should the bot respond on mentions too?
     * @param ignoreBots     Should bot messages be ignored?
     * @param ignoreSystem   Should the system messages be ignored?
     * @param ignoreWebhooks Should webhooks be ignored?
     */
    public DefaultCommandService(String defaultPrefix, Function<Guild, String> customPrefix, boolean allowMention,
                                 boolean ignoreBots, boolean ignoreSystem, boolean ignoreWebhooks,
                                 List<CommandHandler> commands, List<CommandHandler> slashCommands, List<String> userBlacklist,
                                 BiConsumer<MessageReceivedEvent, Throwable> errorHandler, BiConsumer<MessageReceivedEvent, CoolDown> coolDownHandler,
                                 BiFunction<MessageReceivedEvent, CommandData, Boolean> customCheck) {
        // No default prefix set
        if (defaultPrefix == null) throw new IllegalArgumentException("You need to specify a default prefix");

        this.defaultPrefix = defaultPrefix;
        this.customPrefix = customPrefix;
        this.allowMention = allowMention;

        this.ignoreBots = ignoreBots;
        this.ignoreSystem = ignoreSystem;
        this.ignoreWebhooks = ignoreWebhooks;
        // Blacklist
        if (!userBlacklist.isEmpty()) this.userBlacklist.addAll(userBlacklist); // Add already blacklisted users

        commands.forEach(this::registerCommandClass); // Register all commands
        slashCommands.forEach(this::registerSlashCommandClass); // Register slashCommands

        this.errorHandler = errorHandler;
        this.coolDownHandler = coolDownHandler;
        this.customCheck = customCheck;

        this.registerPermission(new Everyone()); // Register default role
    }

    @Override
    public void processSlashCommandExecution(SlashCommandEvent event) {
        if (this.userBlacklist.contains(event.getUser().getId())) return; // User is on blacklist

        int i = 0;
        while (i < this.slashCommands.size())
            try {
                final SlashCommandData slashCommand = this.slashCommands.get(i);

                if (slashCommand.getSlashCommand().name().equals(event.getName())) {
                    slashCommand.getMethod().invoke(slashCommand.getInstance(), new SlashCommandContext(event, slashCommand, this)); // Run slash command
                    break; // Only run slash command once
                }
                i++;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            // Error is thrown in the original method
            catch (InvocationTargetException e) {

                e.getCause().printStackTrace();
            }
    }

    @Override
    public void processCommandExecution(MessageReceivedEvent event) {
        final User user = event.getAuthor();
        if (this.ignoreBots && user.isBot()) return; // User is marked as a bot
        if (this.ignoreSystem && user.isSystem()) return; // User is marked as system account (community updates, ...)
        if (this.ignoreWebhooks && event.getMessage().isWebhookMessage()) return; // Message is a webhook
        if (this.userBlacklist.contains(user.getId())) return; // User is on blacklist
        if (event.isFromGuild() && !event.getGuild().getSelfMember().hasPermission((GuildChannel) event.getChannel(), Permission.MESSAGE_WRITE))
            return;

        final String rawMsg = event.getMessage().getContentRaw().replace("<@!", "<@"); // Get raw content

        String prefix = defaultPrefix; // Get default prefix
        if (customPrefix != null && event.isFromGuild())
            prefix = this.customPrefix.apply(event.getGuild()); // Get guild specific prefix

        final String m = event.getJDA().getSelfUser().getAsMention(); // Get bot as mention

        String msg;
        if (rawMsg.startsWith(prefix)) msg = rawMsg.substring(prefix.length()); // Fired using prefix
        else if (allowMention && rawMsg.startsWith(m)) msg = rawMsg.substring(m.length()); // Fired using mention
        else return; // No command was fired

        String finalPrefix = prefix;

        int i = 0;
        while (i < this.commands.size())
            try {
                final CommandData command = this.commands.get(i);

                boolean hasPermissions = hasPermissions(event.getMember(), command.getCommand().requires());// Does the member have the required permission?
                boolean rightChannel = isType(command.getCommand().channel(), event); // Was the command executed in the right channel?

                if (rightChannel && hasPermissions) {
                    final List<String> executors = CommandUtils.getCommandExecutors(command.getMethod()); // Get all command executors
                    // Check for every command executor
                    for (String executor : executors) {
                        final String regex = "(?i)" + executor + "($|\\s[\\S\\s]*)"; // Command regex
                        // Message matches command regex
                        if (msg.matches(regex)) {
                            if (this.customCheck != null) {
                                if (!this.customCheck.apply(event, command)) continue;
                            }

                            long cooldown = command.getCommand().cooldown();
                            if (cooldown != 0) {
                                CoolDownData userCoolDownData = this.getUserCoolDownData(user); // User's cooldown data
                                if (userCoolDownData.hasCoolDown(executor)) { // Check if user is on cooldown
                                    coolDownHandler.accept(event, new CoolDown(userCoolDownData.getCoolDown(executor)));
                                    continue;
                                }
                                userCoolDownData.addCoolDown(executor, cooldown);
                                this.setUserCoolDownData(user, userCoolDownData);
                            }

                            String commandArguments = msg.substring(executor.length()); // Filter arguments
                            if (!commandArguments.equals("")) commandArguments = commandArguments.substring(1);

                            command.getMethod().invoke(command.getInstance(), new CommandContext(finalPrefix, event, commandArguments, command, this, this)); // Run command
                            break; // Only run command once
                        }
                    }
                }
                i++;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                break;
            }
            // A required permissions isn't registered
            catch (NotRegisteredException notRegisteredException) {
                notRegisteredException.printStackTrace();
                break;
            }
            // Error is thrown in the original method
            catch (InvocationTargetException e) {
                if (this.errorHandler != null) {
                    this.errorHandler.accept(event, e.getCause()); // Handle errors
                } else {
                    e.getCause().printStackTrace();
                }
                break;
            }
    }

    /**
     * Checks for the right channel type.
     *
     * @param type  JDA's channel type.
     * @param event The msgReceivedEvent.
     * @return Returns if the right channel type was used.
     */
    private boolean isType(Channel type, MessageReceivedEvent event) {
        final ChannelType channel = event.getChannelType();

        switch (type) {
            case DEFAULT -> { // Used for all channels except group
                return channel != ChannelType.GROUP;
            }
            case GUILD -> { // Guild only
                return channel.isGuild();
            }
            case DM -> { // Direct messages
                return channel == ChannelType.PRIVATE;
            }
            default -> {
                return false;
            }
        }
    }

}
