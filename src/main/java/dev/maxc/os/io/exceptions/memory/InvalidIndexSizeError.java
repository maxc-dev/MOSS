package dev.maxc.os.io.exceptions.memory;

/**
 * @author Max Carter
 * @since 05/05/2020
 */
public class InvalidIndexSizeError extends Error {
    public InvalidIndexSizeError(int size) {
        super("Cannot index memory of size [" + size + "] The size must be between 1 and the size of the main memory.");
    }
}
