package dev.maxc.os.components.memory;

import dev.maxc.logs.Logger;
import dev.maxc.os.io.exceptions.memory.AccessingLockedUnitException;
import dev.maxc.os.io.exceptions.memory.MutatingLockedUnitException;

/**
 * @author Max Carter
 * @since 24/04/2020
 */
public class MemoryUnit {
    private boolean locked = false;
    private int content;
    private final MemoryAddress memoryAddress;

    public MemoryUnit(MemoryAddress memoryAddress) {
        this.memoryAddress = memoryAddress;
    }

    /**
     * Gets the content of a memory unit, you must make sure that the unit is unlocked
     * with `isUnlocked` otherwise an exception will be thrown.
     *
     * @throws AccessingLockedUnitException Thrown when the unit is accessed whilst it is still locked.
     */
    public int access() throws AccessingLockedUnitException {
        if (locked) {
            Logger.log("Memory", "Attempting to access a locked memory unit at address [" + memoryAddress.toString() + "]");
            throw new AccessingLockedUnitException(memoryAddress);
        } else {
            return content;
        }
    }

    /**
     * Sets the contents of the memory unit to the parameter specified. If a unit is set
     * without making sure that the unit is unlocked, an exception will be thrown.
     *
     * @throws MutatingLockedUnitException Thrown when the unit is mutated whilst is it still locked.
     */
    public void mutate(int content) throws MutatingLockedUnitException {
        if (locked) {
            Logger.log("Memory", "Attempting to mutate a locked memory unit at address [" + memoryAddress.toString() + "]");
            throw new MutatingLockedUnitException(memoryAddress);
        } else {
            this.content = content;
        }
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }
}
