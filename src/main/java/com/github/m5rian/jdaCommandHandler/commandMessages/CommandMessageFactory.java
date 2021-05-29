package com.github.m5rian.jdaCommandHandler.commandMessages;

import com.github.m5rian.jdaCommandHandler.CommandContext;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Marian
 * With this class you can configure a preset which you can use
 * with a {@link com.github.m5rian.jdaCommandHandler.CommandHandler} implementation.
 * You can configure such presets for
 * <ul>
 *     <li>Info</li>
 *     <li>Warnings</li>
 *     <li>Errors</li>
 * </ul>
 */
public class CommandMessageFactory {
    // Message
    private Function<CommandContext, String> message = null;
    // Embed
    private Function<CommandContext, String> title = null; // Title of embed
    private Function<CommandContext, String> thumbnail = null; // Thumbnail image
    private Function<CommandContext, String> image = null; // Big image at the bottom
    private Function<CommandContext, Color> colour = null; // Colour
    private Function<CommandContext, String> hexColour = null; // Colour as hex
    private Function<CommandContext, String> author = null; // Author text
    private Function<CommandContext, String> hyperLink = null; // Hyper link on author text or title
    private Function<CommandContext, String> authorAvatar = null; // Author avatar image
    private Function<CommandContext, String> description = null; // Description
    private List<Function<CommandContext, MessageEmbed.Field>> fields = new ArrayList<>(); // Fields
    private Function<CommandContext, String> footer = null; // Footer text
    // Other
    private boolean reply = false; // Should the message be a reply?

    /**
     * @param message Normal text message, without embed.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setMessage(Function<CommandContext, String> message) {
        this.message = message;
        return this;
    }

    /**
     * @param title The {@link MessageEmbed#title}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setTitle(Function<CommandContext, String> title) {
        this.title = title;
        return this;
    }

    /**
     * @param thumbnail The {@link MessageEmbed.Thumbnail#url}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setThumbnail(Function<CommandContext, String> thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    /**
     * @param image The {@link MessageEmbed.ImageInfo#url}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setImage(Function<CommandContext, String> image) {
        this.image = image;
        return this;
    }

    /**
     * @param colour The {@link MessageEmbed#color}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setColour(Function<CommandContext, Color> colour) {
        this.colour = colour;
        return this;
    }

    /**
     * @param hexColour The {@link MessageEmbed#color}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setColourHex(Function<CommandContext, String> hexColour) {
        this.hexColour = hexColour;
        return this;
    }

    /**
     * @param author The {@link MessageEmbed.AuthorInfo#name}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setAuthor(Function<CommandContext, String> author) {
        this.author = author;
        return this;
    }

    /**
     * @param hyperLink The {@link MessageEmbed.AuthorInfo#url} or {@link MessageEmbed#url}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setHyperLink(Function<CommandContext, String> hyperLink) {
        this.hyperLink = hyperLink;
        return this;
    }

    /**
     * @param authorAvatar The {@link MessageEmbed.AuthorInfo#iconUrl}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setAuthorAvatar(Function<CommandContext, String> authorAvatar) {
        this.authorAvatar = authorAvatar;
        return this;
    }

    /**
     * @param description The {@link MessageEmbed#description}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setDescription(Function<CommandContext, String> description) {
        this.description = description;
        return this;
    }

    /**
     * @param field The {@link MessageEmbed.Field}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory addField(Function<CommandContext, MessageEmbed.Field> field) {
        this.fields.add(field);
        return this;
    }

    /**
     * @param footer The {@link MessageEmbed.Footer#text}.
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory setFooter(Function<CommandContext, String> footer) {
        this.footer = footer;
        return this;
    }

    /**
     * Make the message a reply
     *
     * @return Returns the current {@link CommandMessageFactory} for method chaining.
     */
    public CommandMessageFactory reply() {
        this.reply = true;
        return this;
    }

    /**
     * Creates a new Object of {@link CommandMessage} with the preset applied.
     *
     * @param ctx The current {@link CommandContext}.
     * @return Returns an editable {@link CommandMessage}.
     */
    public CommandMessage invoke(CommandContext ctx) {
        return new CommandMessage(
                // Message
                this.message,
                // Embed
                this.title,
                this.thumbnail,
                this.image,
                this.colour,
                this.hexColour,
                this.author,
                this.hyperLink,
                this.authorAvatar,
                this.description,
                this.fields,
                this.footer,
                // Other
                ctx,
                this.reply);
    }

}
