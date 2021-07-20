package com.github.m5rian.jdaCommandHandler.commandMessages;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.function.BiFunction;
import java.util.function.Function;

public class CommandUsageFactory {
    // Base message
    private Function<CommandContext, String> message = null;
    private Function<CommandContext, EmbedBuilder> embed = null;
    // Command usages
    private BiFunction<CommandContext, CommandEvent, String> text = null;
    private BiFunction<CommandContext, CommandEvent, String> description = null;
    private BiFunction<CommandContext, CommandEvent, MessageEmbed.Field> field = null;
    // Other
    private boolean reply = false;

    public CommandUsageFactory setDefaultMessage(Function<CommandContext, String> message) {
        this.message = message;
        return this;
    }

    public CommandUsageFactory setDefaultEmbed(Function<CommandContext, EmbedBuilder> embed) {
        this.embed = embed;
        return this;
    }

    public CommandUsageFactory addUsageAsText(BiFunction<CommandContext, CommandEvent, String> text) {
        this.text = text;
        return this;
    }

    public CommandUsageFactory addUsageAsDescription(BiFunction<CommandContext, CommandEvent, String> description) {
        this.description = description;
        return this;
    }

    public CommandUsageFactory addUsageAsField(BiFunction<CommandContext, CommandEvent, MessageEmbed.Field> field) {
        this.field = field;
        return this;
    }

    public CommandUsageFactory reply() {
        this.reply = true;
        return this;
    }

    public CommandUsage invoke(CommandContext ctx) {
        check(); // Check for errors

        String message = "";
        EmbedBuilder embed = new EmbedBuilder();

        // Default message is set
        if (this.message != null) message = this.message.apply(ctx);
        // Default embed is ste
        if (this.embed != null) embed = this.embed.apply(ctx);

        return new CommandUsage(message, this.text, embed, this.description, this.field, ctx, this.reply); // Return command usage
    }

    private void check() {
        // A way to display commands was set for a embed, but no default embed was set
        if (this.embed == null && this.description != null || this.embed == null && this.field != null) {
            throw new IllegalArgumentException("If you want to use the description or field to display command usages, you need to create a default embed");
        }
        // Description and field was chosen for command usage
        if (this.description != null && this.field != null) {
            throw new IllegalArgumentException("You can't set a description and a field. Choose only one");
        }
        // No way of displaying commands was set
        if (this.description == null && this.field == null && this.text == null) {
            throw new IllegalArgumentException("Set at least one way to display command usages. Using a field or the description.");
        }
    }
}
