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
    private boolean active = true;
    private int content;
    private int offset;
    private int processIdentifier;
    private final MemoryAddress memoryAddress;

    public MemoryUnit(MemoryAddress memoryAddress) {
        this.memoryAddress = memoryAddress;
    }

    /**
     * Gets the logical address in the logical memory handler (paging, segmentation etc...)
     */
    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getProcessIdentifier() {
        return processIdentifier;
    }

    public void setProcessIdentifier(int processIdentifier) {
        this.processIdentifier = processIdentifier;
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
            lock();
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
            this.active = true;
        }
    }

    /**
     * Clears the memory unit of it's data, setting the value to the default (0).
     * The memory unit state will be changed to
     *
     * @throws MutatingLockedUnitException
     */
    public void clear() throws MutatingLockedUnitException {
        if (locked) {
            Logger.log("Memory", "Attempting to clear a locked memory unit at address [" + memoryAddress.toString() + "]");
            throw new MutatingLockedUnitException(memoryAddress);
        } else {
            this.content = 0;
            this.active = false;
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

    public boolean isActive() {
        return active;
    }
}
