package com.github.m5rian.jdaCommandHandler.commandServices;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marian
 * <p>
 * This interface provides methods to ban users from using all commands.
 * This can be implemented in a CommandService. like it's done in the {@link DefaultCommandService}.
 */
public interface IBlacklistService {
    /**
     * This list contains all member ids as Strings who are banned.
     * These users won't be able to use any commands.
     */
    List<String> blacklist = new ArrayList<>();
    /**
     * The {@link BlacklistHandler} provides the {@link java.util.function.Consumer}s which
     * are fired when {@link IBlacklistService#addUserToBlackList(User)} or {@link IBlacklistService#removeUserFromBlacklist(User)}
     * is called.
     */
    BlacklistHandler blacklistHandler = new BlacklistHandler();

    /**
     * @param user The user to add to the blacklist.
     */
    default void addUserToBlackList(User user) {
        this.blacklist.add(user.getId());
        this.blacklistHandler.blacklistAddAction.accept(user);
    }

    /**
     * @param user The user to remove from the blacklist.
     */
    default void removeUserFromBlacklist(User user) {
        this.blacklist.remove(user.getId());
        this.blacklistHandler.blacklistRemoveAction.accept(user);
    }

}
