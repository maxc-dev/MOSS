package dev.maxc.os.io.exceptions.interpreter;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class UnparsableDataException extends Exception {
    public UnparsableDataException(String message) {
        super("Cannot parse the following: " + message);
    }
}
