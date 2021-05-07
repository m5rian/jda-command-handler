package com.github.m5rian.jdaCommandHandler.exceptions;

/**
 * @author Marian
 * <p>
 * Will fire, once a {@link com.github.m5rian.jdaCommandHandler.Permission} is used, but not registered.
 */
public class NotRegisteredException extends Exception {

    /**
     * Will fire, once a {@link com.github.m5rian.jdaCommandHandler.Permission} is used, but not registered.
     *
     * @param message A message to display.
     */
    public NotRegisteredException(String message) {
        super(message);
    }

}
