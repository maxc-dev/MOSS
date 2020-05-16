package dev.maxc.os.io.exceptions.compiler;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class InUseVariableNameException extends Exception {
    public InUseVariableNameException(String name) {
        super("The identifier [" + name + "] is already in use by another variable, or has an identifier which can create conflict issues with other variables.");
    }
}
