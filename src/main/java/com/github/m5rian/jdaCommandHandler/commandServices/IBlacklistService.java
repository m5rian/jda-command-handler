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
    List<String> userBlacklist = new ArrayList<>();
}
