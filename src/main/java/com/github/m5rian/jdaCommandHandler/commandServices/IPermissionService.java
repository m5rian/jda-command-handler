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
        boolean hasPermissions = true;

        while (iterator.hasNext()) {
            final Class<? extends Permission> clazz = iterator.next(); // Get current permission class
            // Permission isn't registered
            if (!this.permissions.containsKey(clazz))
                throw new NotRegisteredException(clazz.getSimpleName() + " isn't registered as a role. Please register this role in a CommandService");
            final Permission permission = getPermission(clazz); // Get current permission

            if (permission.getUserId() != null) {
                if (!member.getId().equals(permission.getUserId())) {
                    hasPermissions = false;
                    break;
                }
            }

            if (!permission.getPermissions().isEmpty()) {
                if (!member.hasPermission(permission.getPermissions())) {
                    hasPermissions = false;
                    break;
                }
            }

            if (!permission.getRoleIds().isEmpty()) {
                for (String roleId : permission.getRoleIds()) {
                    if (member.getRoles().stream().noneMatch(r -> r.getId().equals(roleId))) {
                        hasPermissions = false;
                        break;
                    }
                }
            }

            if (!permission.getRoleIdsLong().isEmpty()) {
                for (Long roleId : permission.getRoleIdsLong()) {
                    if (member.getRoles().stream().noneMatch(r -> r.getIdLong() == roleId)) {
                        hasPermissions = false;
                        break;
                    }
                }
            }
        }
        return hasPermissions;
    }
}