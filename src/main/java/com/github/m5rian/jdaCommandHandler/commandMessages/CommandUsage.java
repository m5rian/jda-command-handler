package com.github.m5rian.jdaCommandHandler.commandMessages;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Marian
 * This class is used when sending a command usage from the {@link com.github.m5rian.jdaCommandHandler.CommandHandler}.
 * You can set up a preset with the {@link CommandUsageFactory}.
 */
public class CommandUsage {
    private final EmbedBuilder embed;
    // Other
    private final CommandContext ctx;
    private final boolean reply; // Should the message be a reply?
    private final boolean mention; // Should the member get mentioned when replying?
    // Base message
    private String message;
    // Command usages
    private BiFunction<CommandContext, CommandEvent, String> text;
    private BiFunction<CommandContext, CommandEvent, String> description;
    private BiFunction<CommandContext, CommandEvent, MessageEmbed.Field> field;
    private List<MessageEmbed.Field> fields = new ArrayList<>(); // Additional fields
    private String footer; // Footer of embed
    private Class[] classes; // Classes to get the usage from
    private String[] allowedMethods = null; // Names of methods to add as usage
    private String[] forbiddenMethods = null; // Names of methods which shouldn't get added

    public CommandUsage(String message, BiFunction<CommandContext, CommandEvent, String> text, EmbedBuilder embed, BiFunction<CommandContext, CommandEvent, String> description, BiFunction<CommandContext, CommandEvent, MessageEmbed.Field> field,
                        CommandContext ctx, boolean reply, boolean mention) {
        // Base message
        this.message = message;
        this.embed = embed;
        // Command usages
        this.text = text;
        this.description = description;
        this.field = field;
        // Other
        this.ctx = ctx;
        this.reply = reply;
        this.mention = mention;
    }

    public CommandUsage addInlineField(String name, String value) {
        this.fields.add(new MessageEmbed.Field(name, value, true));
        return this;
    }

    public CommandUsage addField(String name, String value) {
        this.fields.add(new MessageEmbed.Field(name, value, false));
        return this;
    }

    public CommandUsage setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public CommandUsage addUsages(Class... classes) {
        this.classes = classes;
        return this;
    }

    public CommandUsage allowCommands(String... methodNames) {
        this.allowedMethods = methodNames;
        return this;
    }

    public CommandUsage forbidCommands(String... methodNames) {
        this.forbiddenMethods = methodNames;
        return this;
    }

    private void buildEmbed() {
        // No command was provided
        if (this.classes == null) {
            final CommandEvent commandInfo = ctx.getCommand(); // Get info about command

            // Normal text is used to display commands
            if (this.text != null) {
                final String usage = this.text.apply(this.ctx, commandInfo); // Get usage for command
                message += "\n" + usage; // Append command usage in message
            }

            // Use description to display commands
            if (this.description != null) {
                final String usage = this.description.apply(this.ctx, commandInfo); // Get usage for command
                embed.appendDescription("\n" + usage); // Append command usage to description
            }
            // Use fields to display commands
            else if (this.field != null) {
                final MessageEmbed.Field usage = this.field.apply(this.ctx, commandInfo); // Get usage for command
                embed.addField(usage); // Add command usage as field
            }
        }
        // Specific commands were provided
        else {
            // Add command usages
            for (Class clazz : classes) { // Go through all classes
                for (Method method : clazz.getMethods()) { // Go through all methods of the class
                    // Only allowed commands were specified and the method name is one of the methods which are allowed to be added as a usage
                    if (this.allowedMethods != null && !Arrays.asList(this.allowedMethods).contains(method.getName())) continue;
                    // Some commands are forbidden to add as a usage and current command is one of them
                    if (this.forbiddenMethods != null && Arrays.asList(this.forbiddenMethods).contains(method.getName())) continue;

                    // Command annotation is present
                    if (method.isAnnotationPresent(CommandEvent.class)) {
                        final CommandEvent commandInfo = method.getAnnotation(CommandEvent.class); // Get info about command

                        // Normal text is used to display commands
                        if (this.text != null) {
                            final String usage = this.text.apply(this.ctx, commandInfo); // Get usage for command
                            this.message += "\n" + usage; // Append command usage in message
                        }

                        // Use description to display commands
                        if (this.description != null) {
                            final String usage = this.description.apply(this.ctx, commandInfo); // Get usage for command
                            this.embed.appendDescription("\n" + usage); // Append command usage to description
                        }
                        // Use fields to display commands
                        else if (this.field != null) {
                            final MessageEmbed.Field usage = this.field.apply(this.ctx, commandInfo); // Get usage for command
                            this.embed.addField(usage); // Add command usage as field
                        }
                    }
                }
            }
        }

        if (this.footer != null) this.embed.setFooter(this.footer);
        if (!this.fields.isEmpty()) this.fields.forEach(this.embed::addField);
    }

    public void send() {
        final MessageChannel channel = this.ctx.getChannel(); // Get channel
        final Message msg = ctx.getEvent().getMessage(); // Get message from author
        buildEmbed();

        // Reply
        if (this.reply) {
            if (mention) {
                if (!this.message.isEmpty() && embed.isEmpty()) ctx.getMessage().reply(this.message).mentionRepliedUser(true).queue();
                if (!this.message.isEmpty() && !embed.isEmpty()) ctx.getMessage().reply(this.message).setEmbeds(embed.build()).mentionRepliedUser(true).queue();
                if (this.message.isEmpty() && !embed.isEmpty()) ctx.getMessage().replyEmbeds(embed.build()).mentionRepliedUser(true).queue();
            }
            else {
                if (!this.message.isEmpty() && embed.isEmpty()) ctx.getMessage().reply(this.message).mentionRepliedUser(false).queue();
                if (!this.message.isEmpty() && !embed.isEmpty()) ctx.getMessage().reply(this.message).setEmbeds(embed.build()).mentionRepliedUser(false).queue();
                if (this.message.isEmpty() && !embed.isEmpty()) ctx.getMessage().replyEmbeds(embed.build()).mentionRepliedUser(false).queue();
            }
        }
        // Don't reply
        else {
            if (!this.message.isEmpty() && this.embed.isEmpty()) channel.sendMessage(this.message).queue();
            if (!this.message.isEmpty() && !this.embed.isEmpty()) channel.sendMessage(this.message).setEmbeds(this.embed.build()).queue();
            if (this.message.isEmpty() && !this.embed.isEmpty()) channel.sendMessageEmbeds(this.embed.build()).queue();
        }
    }

    public EmbedBuilder getEmbed() {
        buildEmbed();
        return this.embed;
    }

}
