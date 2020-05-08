package dev.maxc.os.io.exceptions.deadlock;

import dev.maxc.os.components.memory.model.MemoryAddress;

/**
 * @author Max Carter
 * @since 24/04/2020
 */
public class AccessingLockedUnitException extends Exception {
    public AccessingLockedUnitException() {
        super();
    }

    public AccessingLockedUnitException(MemoryAddress memoryAddress, int processLock) {
        super("Process attempted to access a unit which is locked to process [" + processLock + "] at address: " + memoryAddress.toString());
    }
}
