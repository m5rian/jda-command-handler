package com.github.m5rian.jdaCommandHandler.commandServices;

import com.github.m5rian.jdaCommandHandler.command.CoolDownData;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * This interface provides methods to get cool down data of users for all commands
 */
public interface ICoolDownService {

    /**
     * This map contains the cool down data of all the users.
     */
    Map<Long, CoolDownData> coolDownDataMap = new HashMap<>();

    /**
     * @return the cool down data of {@param user}
     */
    default CoolDownData getUserCoolDownData(User user) {
        return coolDownDataMap.computeIfAbsent(user.getIdLong(), userId -> new CoolDownData());
    }

    /**
     * Sets the cool down data of the user
     */
    default void setUserCoolDownData(User user, CoolDownData data) {
        coolDownDataMap.put(user.getIdLong(), data);
    }

}
