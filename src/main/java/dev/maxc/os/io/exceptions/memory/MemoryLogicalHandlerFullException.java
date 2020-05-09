package dev.maxc.os.io.exceptions.memory;

/**
 * @author Max Carter
 * @since 09/05/2020
 */
public class MemoryLogicalHandlerFullException extends Exception {
    public MemoryLogicalHandlerFullException() {
        super("A process has requested a new logical offset but there are no more logical addresses available, more memory must be allocated.");
    }
}
