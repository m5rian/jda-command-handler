package com.github.m5rian.jdaCommandHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandUtils {

    /**
     * @param method The command method
     * @return Returns a list of all executors from a command. The command doesn't need to be registered.
     */
    public static List<String> getCommandExecutors(Method method) {
        final CommandEvent commandInfo = method.getAnnotation(CommandEvent.class); // Get annotation
        final List<String> executors = new ArrayList<>(); // Create list for executors

        executors.add(commandInfo.name()); // Add command name
        Collections.addAll(executors, commandInfo.aliases()); // Add aliases

        return executors; // Return list with all executors
    }

}
