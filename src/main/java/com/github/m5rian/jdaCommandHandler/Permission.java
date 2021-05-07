package com.github.m5rian.jdaCommandHandler;

import java.util.Collections;
import java.util.List;

/**
 * @author Marian
 */
public interface Permission {
    /**
     * @return Returns the name of the role.
     */
    String getName();

    /**
     * @return Returns a list of {@link net.dv8tion.jda.api.Permission}, which are required to execute the command.
     * By default it returns an empty list.
     */
    default List<net.dv8tion.jda.api.Permission> getPermissions() {
        return Collections.emptyList();
    }

    /**
     * This method only works if isForOneGuild is true.
     *
     * @return Returns role ids which have access to this permission.
     * By default this method returns an empty list.
     */
    default List<String> getRoleIds() {
        return Collections.emptyList();
    }

    /**
     * This method only works if isForOneGuild is true.
     *
     * @return Returns role ids which have access to this permission.
     * By default this method returns an empty list.
     */
    default List<Long> getRoleIdsLong() {
        return Collections.emptyList();
    }

    /**
     * @return Returns a required user id.
     * By default this method returns null.
     */
    default String getUserId() {
        return null;
    }
}
