package dev.maxc.os.io.exceptions.memory;

import dev.maxc.os.components.memory.MemoryAddress;

/**
 * @author Max Carter
 * @since 24/04/2020
 */
public class MutatingLockedUnitException extends Exception {
    public MutatingLockedUnitException(MemoryAddress memoryAddress) {
        super("Process attempted to mutate a unit which is locked at address: " + memoryAddress.toString());
    }
}
