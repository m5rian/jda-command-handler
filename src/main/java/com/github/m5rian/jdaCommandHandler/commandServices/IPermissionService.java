package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.Everyone;
import com.github.m5rian.jdaCommandHandler.Permission;
import com.github.m5rian.jdaCommandHandler.exceptions.NotRegisteredException;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Marian
 * Interface for roles.
 */
@SuppressWarnings("unused")
public interface IPermissionService {
    /**
     *
     */
    Map<Class<? extends Permission>, Permission> permissions = new HashMap<>();

    /**
     * Register a permission.
     *
     * @param permission The permission to register.
     */
    default void registerPermission(Permission permission) {
        this.permissions.put(permission.getClass(), permission);
    }

    /**
     * Register multiple permission with one method.
     *
     * @param permissions The permission to register.
     */
    default void registerPermission(Permission... permissions) {
        for (Permission permission : permissions) {
            this.registerPermission(permission);
        }
    }

    /**
     * Remove a registered permission.
     *
     * @param permission The permission to unregister.
     */
    default void unregisterPermission(Class<? extends Permission> permission) {
        this.permissions.remove(permission);
    }

    /**
     * Removes all registered permissions.
     */
    default void unregisterAllPermissions() {
        this.permissions.clear();
    }

    /**
     * @param clazz A class, which implements Role.
     * @return Returns a class, which implements the Role interface as Role.
     * This can return null if the class is not in the map.
     */
    @Nullable
    default Permission getPermission(Class<? extends Permission> clazz) {
        return this.permissions.get(clazz);
    }

    /**
     * @return Returns all registered permissions as a HashMap.
     */
    default Map<Class<? extends Permission>, Permission> getPermissions() {
        return this.permissions;
    }

    /**
     * /**
     * Checks if a member is allowed to execute the command.
     *
     * @param member      The author, who executed the command.
     * @param permissions The permissions the member needs to execute the command.
     * @return Returns if the member can execute the command.
     * @throws NotRegisteredException Is thrown when the provided permission isn't registered in a CommandService
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    default boolean hasPermissions(Member member, Class<? extends Permission>... permissions) throws NotRegisteredException {
        final List<Class<? extends Permission>> roles = Arrays.asList(permissions); // Get permissions array as list

        if (roles.contains(Everyone.class)) return true; // Everyone permission

        Iterator<Class<? extends Permission>> iterator = roles.iterator(); // Get required roles

        while (iterator.hasNext()) {
            final Class<? extends Permission> clazz = iterator.next(); // Get current permission class
            // Permission isn't registered
            if (!this.permissions.containsKey(clazz))
                throw new NotRegisteredException(clazz.getSimpleName() + " isn't registered as a role. Please register this role in a CommandService");
            final Permission permission = getPermission(clazz); // Get current permission

            if (permission.getUserId() != null && !permission.getUserId().equals(member.getId())) return false;
            if (!permission.getPermissions().isEmpty() && !member.hasPermission(permission.getPermissions())) return false;
            if (!permission.getRoleIds().isEmpty() && permission.getRoleIds().stream().noneMatch(roleId -> member.getRoles().contains(member.getGuild().getRoleById(roleId)))) return false;
            if (!permission.getRoleIdsLong().isEmpty() && permission.getRoleIdsLong().stream().noneMatch(roleId -> member.getRoles().contains(member.getGuild().getRoleById(roleId)))) return false;
        }
        return true;
    }
}