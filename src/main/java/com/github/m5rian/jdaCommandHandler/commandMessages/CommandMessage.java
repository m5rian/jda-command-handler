package com.github.m5rian.jdaCommandHandler.commandMessages;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link CommandMessage} is a editable preset of a {@link CommandMessageFactory}
 * to quickly send command specific messages with pre-made values.
 */
public class CommandMessage {
    // Other
    private final CommandContext ctx;
    // Message
    private String message = "";
    // Embed
    private String title = null;
    private String thumbnail = null;
    private Color colour = null;
    private String author = null;
    private String hyperLink = null;
    private String authorAvatar = null;
    private String description = "";
    private List<MessageEmbed.Field> fields;
    private String image = null;
    private String footer = null;
    private Instant timestamp;

    private final List<Component> components = new ArrayList<>();

    private boolean reply;
    private boolean mention;
    private TextChannel channel = null;

    /**
     * Is called by a {@link CommandMessageFactory} to create an editable preset.
     *
     * @param message      A normal text message.
     * @param title        The {@link MessageEmbed#title}.
     * @param thumbnail    The {@link MessageEmbed.Thumbnail#url}.
     * @param image        The {@link MessageEmbed.ImageInfo#url}.
     * @param author       The {@link MessageEmbed.AuthorInfo#name}.
     * @param hyperLink    The {@link MessageEmbed.AuthorInfo#url}.
     * @param authorAvatar The {@link MessageEmbed.AuthorInfo#iconUrl}.
     * @param description  The {@link MessageEmbed#description}.
     * @param fields       All {@link MessageEmbed.Field}s.
     * @param ctx          A {@link CommandContext}.
     * @param reply        Should the send message be a reply?
     * @param mention      Should the bot mention the member on a reply?
     */
    public CommandMessage(
            // Message
            Function<CommandContext, String> message,
            // Embed
            Function<CommandContext, String> title,
            Function<CommandContext, String> thumbnail,
            Function<CommandContext, String> image,
            Function<CommandContext, Color> colour,
            Function<CommandContext, String> hexColour,
            Function<CommandContext, String> author,
            Function<CommandContext, String> hyperLink,
            Function<CommandContext, String> authorAvatar,
            Function<CommandContext, String> description,
            List<Function<CommandContext, MessageEmbed.Field>> fields,
            Function<CommandContext, String> footer,
            // Other
            CommandContext ctx,
            boolean reply,
            boolean mention
    ) {
        // Message
        if (message != null) this.message = message.apply(ctx);
        // Embed
        if (title != null) this.title = title.apply(ctx);
        if (thumbnail != null) this.thumbnail = thumbnail.apply(ctx);
        if (image != null) this.image = image.apply(ctx);
        if (colour != null) this.colour = colour.apply(ctx);
        if (hexColour != null) this.colour = Color.decode(hexColour.apply(ctx));
        if (author != null) this.author = author.apply(ctx);
        if (hyperLink != null) this.hyperLink = hyperLink.apply(ctx);
        if (authorAvatar != null) this.authorAvatar = authorAvatar.apply(ctx);
        if (description != null) this.description = description.apply(ctx);
        final List<MessageEmbed.Field> embedFields = new ArrayList<>(); // Create new list for fields
        fields.forEach(field -> embedFields.add(field.apply(ctx))); // Invoke each field to get it as a Field object
        this.fields = embedFields; // Set fields
        if (this.footer != null) this.footer = footer.apply(ctx);
        // Other
        this.ctx = ctx;
        this.reply = reply;
        this.mention = mention;
    }

    /**
     * Overrides the current {@link CommandMessage#message}.
     *
     * @param message Normal text message, without embed.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Appends text to the current {@link CommandMessage#message}.
     *
     * @param message Normal text message, without embed.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessage appendMessage(String message) {
        this.message += message;
        return this;
    }

    /**
     * Overrides the current {@link CommandMessage#title}.
     *
     * @param title The {@link MessageEmbed#title}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Appends text to the current {@link CommandMessage#title}.
     *
     * @param title The {@link MessageEmbed#title}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage appendTitle(String title) {
        this.title += title;
        return this;
    }

    /**
     * Overrides the current {@link CommandMessage#thumbnail}.
     *
     * @param thumbnail The {@link MessageEmbed.Thumbnail#url}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    /**
     * Overrides the current {@link MessageEmbed#color}.
     *
     * @param colour The {@link MessageEmbed#color}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setColour(Color colour) {
        this.colour = colour;
        return this;
    }

    /**
     * Overrides the current {@link MessageEmbed#color}.
     *
     * @param hexColour The {@link MessageEmbed#color}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setColourHex(String hexColour) {
        this.colour = Color.decode(hexColour);
        return this;
    }

    /**
     * Overrides the current {@link CommandMessage#author}.
     *
     * @param author The {@link MessageEmbed.AuthorInfo#name}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Appends text to the current {@link CommandMessage#author}.
     *
     * @param author The {@link MessageEmbed.AuthorInfo#name}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage appendAuthor(String author) {
        this.author += author;
        return this;
    }

    /**
     * Overrides the current {@link CommandMessage#hyperLink}.
     *
     * @param hyperLink The {@link MessageEmbed.AuthorInfo#url} or {@link MessageEmbed#url}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setHyperLink(String hyperLink) {
        this.hyperLink = hyperLink;
        return this;
    }

    /**
     * Overrides the current {@link CommandMessage#authorAvatar}.
     *
     * @param authorAvatar The {@link MessageEmbed.AuthorInfo#iconUrl}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
        return this;
    }

    /**
     * Overrides the current {@link CommandMessage#description}.
     *
     * @param description The {@link MessageEmbed#description}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Appends text to the current {@link CommandMessage#description}.
     *
     * @param description The {@link MessageEmbed#description}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage appendDescription(String description) {
        this.description += description;
        return this;
    }

    /**
     * Add another field for the embed
     *
     * @param field The {@link MessageEmbed.Field}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage addField(MessageEmbed.Field field) {
        this.fields.add(field);
        return this;
    }

    /**
     * Overrides the current {@link CommandMessage#image}.
     *
     * @param image The {@link MessageEmbed.ImageInfo#url}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setImage(String image) {
        this.image = image;
        return this;
    }

    /**
     * Overrides the current {@link CommandMessage#footer}
     *
     * @param footer The {@link MessageEmbed.Footer#text}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public CommandMessage addTimestamp() {
        this.timestamp = Instant.now();
        return this;
    }

    public CommandMessage addComponents(ActionRow actionRow) {
        this.components.addAll(actionRow.getComponents());
        this.components.addAll(actionRow.getButtons());
        return this;
    }

    public CommandMessage addComponents(Component... components) {
        this.components.addAll(Arrays.asList(components));
        return this;
    }

    public CommandMessage addComponents(Collection<? extends Component> components) {
        this.components.addAll(components);
        return this;
    }

    /**
     * Overrides the current {@link CommandMessage#reply}.
     *
     * @param reply Should the message reply to the authors message?
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage reply(boolean reply) {
        this.reply = reply;
        return this;
    }

    public CommandMessage setChannel(TextChannel channel) {
        this.channel = channel;
        return this;
    }

    /**
     * Builds a message with a {@link CommandMessageFactory} and the later made changes.
     * This message would get send to the current channel.
     */
    public void send() {
        check(); // Check for various errors
        final EmbedBuilder embed = getEmbed(); // Get embed
        MessageAction action = null;

        // Custom channel is set
        if (this.channel != null) {
            if (!this.message.isEmpty() && embed.isEmpty()) action = this.channel.sendMessage(this.message);
            if (!this.message.isEmpty() && !embed.isEmpty()) action = this.channel.sendMessage(this.message).setEmbeds(embed.build());
            if (this.message.isEmpty() && !embed.isEmpty()) action = this.channel.sendMessageEmbeds(embed.build());
        }
        // Reply to messages
        else if (reply) {
            // Reply should mention the user
            if (mention) {
                if (!this.message.isEmpty() && embed.isEmpty()) action = ctx.getMessage().reply(this.message).mentionRepliedUser(true);
                if (!this.message.isEmpty() && !embed.isEmpty())
                    action = ctx.getMessage().reply(this.message).setEmbeds(embed.build()).mentionRepliedUser(true);
                if (this.message.isEmpty() && !embed.isEmpty()) action = ctx.getMessage().replyEmbeds(embed.build()).mentionRepliedUser(true);
            }
            // Reply shouldn't mention the user
            else {
                if (!this.message.isEmpty() && embed.isEmpty()) action = ctx.getMessage().reply(this.message).mentionRepliedUser(false);
                if (!this.message.isEmpty() && !embed.isEmpty())
                    action = ctx.getMessage().reply(this.message).setEmbeds(embed.build()).mentionRepliedUser(false);
                if (this.message.isEmpty() && !embed.isEmpty()) action = ctx.getMessage().replyEmbeds(embed.build()).mentionRepliedUser(false);
            }
        }
        // Send as normal message
        else {
            final MessageChannel channel = this.ctx.getChannel();
            if (!this.message.isEmpty() && embed.isEmpty()) action = channel.sendMessage(this.message);
            if (!this.message.isEmpty() && !embed.isEmpty()) action = channel.sendMessage(this.message).setEmbeds(embed.build());
            if (this.message.isEmpty() && !embed.isEmpty()) action = channel.sendMessageEmbeds(embed.build());
        }

        if (components.size() != 0) {
            action.setActionRow(this.components).queue();
        } else action.queue();
    }

    /**
     * Builds a message with a {@link CommandMessageFactory} and the later made changes.
     * This message would get send to the current channel.
     *
     * @param success A {@link Consumer<Message>}, which runs as soon as the message got sent.
     */
    public void send(Consumer<Message> success) {
        check(); // Check for various errors
        final EmbedBuilder embed = getEmbed(); // Get embed
        MessageAction action = null;

        // Custom channel is set
        if (this.channel != null) {
            if (!this.message.isEmpty() && embed.isEmpty()) action = this.channel.sendMessage(this.message);
            if (!this.message.isEmpty() && !embed.isEmpty()) action = this.channel.sendMessage(this.message).setEmbeds(embed.build());
            if (this.message.isEmpty() && !embed.isEmpty()) action = this.channel.sendMessageEmbeds(embed.build());
        }
        // Reply to messages
        else if (reply) {
            // Reply should mention the user
            if (mention) {
                if (!this.message.isEmpty() && embed.isEmpty()) action = ctx.getMessage().reply(this.message).mentionRepliedUser(true);
                if (!this.message.isEmpty() && !embed.isEmpty())
                    action = ctx.getMessage().reply(this.message).setEmbeds(embed.build()).mentionRepliedUser(true);
                if (this.message.isEmpty() && !embed.isEmpty()) action = ctx.getMessage().replyEmbeds(embed.build()).mentionRepliedUser(true);
            }
            // Reply shouldn't mention the user
            else {
                if (!this.message.isEmpty() && embed.isEmpty()) action = ctx.getMessage().reply(this.message).mentionRepliedUser(false);
                if (!this.message.isEmpty() && !embed.isEmpty())
                    action = ctx.getMessage().reply(this.message).setEmbeds(embed.build()).mentionRepliedUser(false);
                if (this.message.isEmpty() && !embed.isEmpty()) action = ctx.getMessage().replyEmbeds(embed.build()).mentionRepliedUser(false);
            }
        }
        // Send as normal message
        else {
            final MessageChannel channel = this.ctx.getChannel();
            if (!this.message.isEmpty() && embed.isEmpty()) action = channel.sendMessage(this.message);
            if (!this.message.isEmpty() && !embed.isEmpty()) action = channel.sendMessage(this.message).setEmbeds(embed.build());
            if (this.message.isEmpty() && !embed.isEmpty()) action = channel.sendMessageEmbeds(embed.build());
        }

        if (components.size() != 0) {
            action.setActionRow(this.components).queue(success);
        } else action.queue(success);
    }

    /**
     * Build the embed.
     *
     * @return Returns a customized {@link EmbedBuilder}.
     */
    public EmbedBuilder getEmbed() {
        check(); // Check for various errors

        final EmbedBuilder embed = new EmbedBuilder(); // Create new embed builder

        if (this.title != null && this.hyperLink == null) embed.setTitle(this.title);
        if (this.title != null && this.hyperLink != null) embed.setTitle(this.title, this.hyperLink);

        if (this.colour != null) embed.setColor(this.colour);
        if (this.author != null) embed.setAuthor(this.author, this.hyperLink, this.authorAvatar);
        if (this.description != null) embed.setDescription(this.description);
        if (this.footer != null) embed.setFooter(this.footer);
        this.fields.forEach(embed::addField); // Add all fields
        if (this.image != null) embed.setImage(this.image);

        return embed;
    }

    /**
     * Checks for various errors.
     */
    private void check() {
        // Title and author was set
        if (this.title != null && this.author != null) {
            throw new IllegalArgumentException("You can't set a title and an author.");
        }
    }
}
