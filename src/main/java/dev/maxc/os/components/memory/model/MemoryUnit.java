package dev.maxc.os.components.memory.model;

import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.exceptions.deadlock.AccessingLockedUnitException;
import dev.maxc.os.io.exceptions.deadlock.MutatingLockedUnitException;
import dev.maxc.os.io.log.Status;

/**
 * @author Max Carter
 * @since 24/04/2020
 */
public class MemoryUnit {
    public static final int UNLOCKED = -1;

    private int lockedToProcess = UNLOCKED;
    private boolean allocated = false;
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
    public int access(int processIdentifier) throws AccessingLockedUnitException {
        if (!isLockedToProcess(processIdentifier)) {
            Logger.log(Status.CRIT, this, "Attempting to access a locked memory unit at address [" + memoryAddress.toString() + "]");
            throw new AccessingLockedUnitException(memoryAddress);
        } else {
            lock(processIdentifier);
            return content;
        }
    }

    /**
     * Sets the contents of the memory unit to the parameter specified. If a unit is set
     * without making sure that the unit is unlocked, an exception will be thrown.
     *
     * @throws MutatingLockedUnitException Thrown when the unit is mutated whilst is it still locked.
     */
    public void mutate(int processIdentifier, int content) throws MutatingLockedUnitException {
        if (!isLockedToProcess(processIdentifier)) {
            Logger.log(Status.CRIT, this, "Attempting to mutate a locked memory unit at address [" + memoryAddress.toString() + "]");
            throw new MutatingLockedUnitException(memoryAddress);
        } else {
            this.content = content;
            this.allocated = true;
        }
    }

    /**
     * Clears the memory unit of it's data, setting the value to the default (0).
     * The memory unit state will be changed to
     *
     * @throws MutatingLockedUnitException
     */
    public void clear(int processIdentifier) throws MutatingLockedUnitException {
        if (!isLockedToProcess(processIdentifier)) {
            Logger.log(Status.CRIT, this, "Attempting to clear a locked memory unit at address [" + memoryAddress.toString() + "]");
            throw new MutatingLockedUnitException(memoryAddress);
        } else {
            this.content = 0;
            this.allocated = false;
        }
    }

    public boolean isLockedToProcess(int processIdentifier) {
        return lockedToProcess == processIdentifier;
    }

    public boolean isLocked() {
        return lockedToProcess != UNLOCKED;
    }

    public void lock(int processIdentifier) {
        lockedToProcess = processIdentifier;
    }

    public void unlock() {
        lockedToProcess = UNLOCKED;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

    @Override
    public String toString() {
        return "MemoryUnit{" +
                "lockedProcess=" + lockedToProcess +
                ", allocated=" + allocated +
                ", content=" + content +
                ", memoryAddress=" + memoryAddress.toString() +
                '}';
    }
}
