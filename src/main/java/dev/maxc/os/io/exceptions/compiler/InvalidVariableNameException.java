package dev.maxc.os.io.exceptions.compiler;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class InvalidVariableNameException extends Exception {
    public InvalidVariableNameException(String name) {
        super("The name [" + name + "] is invalid.");
    }
}
