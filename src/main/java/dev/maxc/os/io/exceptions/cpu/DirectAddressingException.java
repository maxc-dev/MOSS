package dev.maxc.os.io.exceptions.cpu;

/**
 * @author Max Carter
 * @since 14/05/2020
 */
public class DirectAddressingException extends Exception {
    public DirectAddressingException(int value) {
        super("Unable to parse an immediate address as a direct address: " + value);
    }
}
