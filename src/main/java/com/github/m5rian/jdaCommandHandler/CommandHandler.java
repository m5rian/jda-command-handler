package com.github.m5rian.jdaCommandHandler;

import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessage;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandUsage;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;

import java.util.function.Consumer;

/**
 * This is the interface, which you need to implement as soon as you want your class to have commands.
 * <p>
 * This is needed, because to {@link java.lang.reflect.Method#invoke(Object, Object...)} a method
 * you need for the first argument an instance.
 */
public interface CommandHandler {

    /**
     * @param ctx A {@link CommandContext} for the current event.
     * @return Returns a preset {@link CommandMessage} to info something with as a message.
     */
    default CommandMessage info(CommandContext ctx) {
        return CommandUtils.infoFactory.invoke(ctx);
    }

    /**
     * @param ctx A {@link CommandContext} for the current event.
     * @return Returns a preset {@link CommandMessage} to warn as a message.
     */
    default CommandMessage warn(CommandContext ctx) {
        return CommandUtils.warningFactory.invoke(ctx);
    }

    /**
     * @param ctx A {@link CommandContext} for the current event.
     * @return Returns a preset {@link CommandMessage} to display an error as a message.
     */
    default CommandMessage error(CommandContext ctx) {
        return CommandUtils.errorFactory.invoke(ctx);
    }

    /**
     * @param ctx A {@link CommandContext} for the current event.
     * @return Returns a preset {@link CommandMessage} to display an error as a message.
     */
    default CommandUsage usage(CommandContext ctx) {
        return CommandUtils.usageFactory.invoke(ctx);
    }

    default void onButtonEvent(String id, Consumer<ButtonClickEvent> event) {
        CommandListener.buttons.put(id, event);
    }

    default void onSelectionMenuEvent(String id, Consumer<SelectionMenuEvent> event) {
        CommandListener.selectionMenus.put(id, event);
    }


    default void finishButton(String id) {
        CommandListener.buttons.remove(id);
    }

    default void finishSelectionMenu(String id) {
        CommandListener.selectionMenus.remove(id);
    }
}
