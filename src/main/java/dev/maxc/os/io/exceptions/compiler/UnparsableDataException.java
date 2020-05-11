package dev.maxc.os.io.exceptions.compiler;

/**
 * @author Max Carter
 * @since 03/05/2020
 */
public class UnparsableDataException extends Exception {
    public UnparsableDataException(int lineNumber, String message) {
        super("Cannot parse the following: #" + lineNumber + ":" + message);
    }
}
