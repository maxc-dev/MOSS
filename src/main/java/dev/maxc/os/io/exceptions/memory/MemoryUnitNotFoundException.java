package dev.maxc.os.io.exceptions.memory;

/**
 * @author Max Carter
 * @since 09/05/2020
 */
public class MemoryUnitNotFoundException extends Exception {
    public MemoryUnitNotFoundException(int offset) {
        super("Cannot identify memory unit at offset [" + offset + "]");
    }
}
