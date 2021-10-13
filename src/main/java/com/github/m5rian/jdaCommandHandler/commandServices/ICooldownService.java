package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.command.CooldownTarget;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * This interface provides methods to get cool down data of users for all commands
 */
public interface ICooldownService {
    /**
     * Used for guild specific cooldowns.
     * <p>
     * Map containing the guild id and another map with the user id and cooldown expire time.
     * Either this, {@link ICooldownService#memberCommandSpecificCooldown}, {@link ICooldownService#userCoolDowns} or {@link ICooldownService#userCommandSpecificCooldowns} is used. Not multiple.
     */
    Map<String, Map<String, Long>> memberCoolDowns = new HashMap<>();

    /**
     * Used for guild and command specific cooldowns.
     * <p>
     * Map containing the guild id and another map with the user id and yet another map containing the {@link CommandEvent} and the final cooldown expire time.
     * Either this, {@link ICooldownService#memberCoolDowns}, {@link ICooldownService#userCoolDowns} or {@link ICooldownService#userCommandSpecificCooldowns} is used. Not multiple.
     */
    Map<String, Map<String, Map<CommandEvent, Long>>> memberCommandSpecificCooldown = new HashMap<>();

    /**
     * Used for global cooldowns.
     * <p>
     * Map containing the user id and the cooldown expiration time.
     * Either this, {@link ICooldownService#memberCommandSpecificCooldown}, {@link ICooldownService#userCommandSpecificCooldowns} or {@link ICooldownService#userCommandSpecificCooldowns} is used. Not multiple.
     */
    Map<String, Long> userCoolDowns = new HashMap<>();

    /**
     * Used for global command specific cooldowns.
     * <p>
     * Map containing the user id and another map with the {@link CommandEvent} and the final cooldown expire time.
     * Either this, {@link ICooldownService#memberCoolDowns}, {@link ICooldownService#userCoolDowns} or {@link ICooldownService#userCoolDowns} is used. Not multiple.
     */
    Map<String, Map<CommandEvent, Long>> userCommandSpecificCooldowns = new HashMap<>();

    /**
     * @param user           The {@link User} to get the cooldown of.
     * @param command        The {@link CommandEvent} of the executed command.
     * @param cooldownTarget The set {@link CooldownTarget} (for example in {@link DefaultCommandService#cooldownTarget}).
     * @return Returns the cooldown expiration time of the {@link User}. If the {@link CooldownTarget#commandSpecific} is set to true a command specific cooldownTarget will be returned.
     */
    default Long getUserCooldown(User user, CommandEvent command, CooldownTarget cooldownTarget) {
        if (cooldownTarget.commandSpecific) {
            // User has no cooldown
            if (!userCommandSpecificCooldowns.containsKey(user.getId())
                    || !userCommandSpecificCooldowns.get(user.getId()).containsKey(command)) {
                return 0L;
            }

            return userCommandSpecificCooldowns.get(user.getId()).get(command);
        } else {
            return userCoolDowns.getOrDefault(user.getId(), 0L);
        }
    }

    default Long getMemberCooldown(Member member, CommandEvent command, CooldownTarget cooldownTarget) {
        if (cooldownTarget.commandSpecific) {
            // Member has no cooldown
            if (!memberCommandSpecificCooldown.containsKey(member.getGuild().getId())
                    || !memberCommandSpecificCooldown.get(member.getGuild().getId()).containsKey(member.getId())
                    || !memberCommandSpecificCooldown.get(member.getGuild().getId()).get(member.getId()).containsKey(command)) {
                return 0L;
            }

            return memberCommandSpecificCooldown.get(member.getId()).get(member.getGuild().getId()).get(command);
        } else {
            if (!memberCoolDowns.containsKey(member.getGuild().getId())) return 0L;
            else return memberCoolDowns.get(member.getGuild().getId()).getOrDefault(member.getId(), 0L);
        }
    }

    default void setUserCooldown(User user, CommandEvent command, CooldownTarget target) {
        if (target.commandSpecific) {
            if (!userCommandSpecificCooldowns.containsKey(user.getId())) {
                userCommandSpecificCooldowns.put(user.getId(), new HashMap<>());
            }

            userCommandSpecificCooldowns.get(user.getId()).put(command, System.currentTimeMillis() + command.cooldown() * 1000);
        } else {
            userCoolDowns.put(user.getId(), System.currentTimeMillis() + command.cooldown() * 1000);
        }
    }

    default void setMemberCooldown(Member member, CommandEvent command, CooldownTarget target) {
        if (target.commandSpecific) {
            if (!memberCommandSpecificCooldown.containsKey(member.getGuild().getId())) {
                userCommandSpecificCooldowns.put(member.getGuild().getId(), new HashMap<>());
            }
            if (!memberCommandSpecificCooldown.get(member.getGuild().getId()).containsKey(member.getId())) {
                memberCommandSpecificCooldown.get(member.getGuild().getId()).put(member.getId(), new HashMap<>());
            }

            memberCommandSpecificCooldown.get(member.getGuild().getId()).get(member.getId()).put(command, System.currentTimeMillis() + command.cooldown() * 1000);
        } else {
            if (!memberCoolDowns.containsKey(member.getGuild().getId())) {
                memberCoolDowns.put(member.getGuild().getId(), new HashMap<>());
            }

            memberCoolDowns.get(member.getGuild().getId()).put(member.getId(), System.currentTimeMillis() + command.cooldown() * 1000);
        }
    }

}
