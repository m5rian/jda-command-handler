package com.github.m5rian.jdaCommandHandler.commandMessages;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A {@link CommandMessage} is a editable preset of a {@link CommandMessageFactory}
 * to quickly send command specific messages with pre-made values.
 */
public class CommandMessage {
    // Message
    private String message = null;
    // Embed
    private String title = null;
    private String thumbnail = null;
    private String image = null;
    private Color colour = null;
    private String author = null;
    private String hyperLink = null;
    private String authorAvatar = null;
    private String description = null;
    private final List<MessageEmbed.Field> fields;
    private String footer = null;
    // Other
    private final CommandContext ctx;
    private boolean reply;

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
            boolean reply
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
     * Overrides the current {@link CommandMessage#footer}
     *
     * @param footer The {@link MessageEmbed.Footer#text}.
     * @return Returns the current {@link CommandMessage} for method chaining.
     */
    public CommandMessage setFooter(String footer) {
        this.footer = footer;
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

    /**
     * Builds a message with a {@link CommandMessageFactory} and the later made changes.
     * This message would get send to the current channel.
     */
    public void send() {
        check(); // Check for various errors

        final EmbedBuilder embed = getEmbed(); // Get embed

        final MessageChannel channel = this.ctx.getChannel();
        if (this.message != null && embed.isEmpty()) channel.sendMessage(this.message).queue();
        if (this.message != null && !embed.isEmpty()) channel.sendMessage(this.message).embed(embed.build()).queue();
        if (this.message == null && !embed.isEmpty()) channel.sendMessage(embed.build()).queue();
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
