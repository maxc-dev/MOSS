package dev.maxc.os.components.memory.model;

import dev.maxc.os.components.instruction.Instruction;
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
    public static final int UNALLOCATED = -1;

    private volatile int lockedToProcess = UNLOCKED;
    private volatile Instruction content;
    private final MemoryAddress memoryAddress;
    private volatile int logicalAddress = -1;

    public MemoryUnit(MemoryAddress memoryAddress) {
        this.memoryAddress = memoryAddress;
    }

    /**
     * Gets the content of a memory unit, you must make sure that the unit is unlocked
     * with `isUnlocked` otherwise an exception will be thrown.
     *
     * @throws AccessingLockedUnitException Thrown when the unit is accessed whilst it is still locked.
     */
    public Instruction access(int processIdentifier) throws AccessingLockedUnitException {
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
    public void mutate(int processIdentifier, Instruction content) throws MutatingLockedUnitException {
        if (!isLockedToProcess(processIdentifier)) {
            Logger.log(Status.CRIT, this, "Attempting to mutate a locked memory unit at address [" + memoryAddress.toString() + "]");
            throw new MutatingLockedUnitException(memoryAddress);
        } else {
            this.content = content;
        }
    }

    /**
     * Clears the memory unit of it's data, setting the instruction to null.
     * The memory unit state will be changed to unallocated.
     */
    public void clear() {
        this.content = null;
        this.logicalAddress = UNALLOCATED;
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

    /**
     * If the unit is allocated it means it belongs to a logical handler.
     */
    public boolean isAllocated() {
        return logicalAddress != UNALLOCATED;
    }

    public void allocate(int allocation) {
        this.logicalAddress = allocation;
    }

    public int getLogicalAddress() {
        return logicalAddress;
    }

    public boolean inUse() {
        return content != null;
    }

    @Override
    public String toString() {
        return "MemoryUnit{" +
                "lockedProcess=" + lockedToProcess +
                ", logicalAddress=" + logicalAddress +
                ", content=" + (inUse() ? content.toString() : "N/A") +
                ", memoryAddress=" + memoryAddress.toString() +
                '}';
    }
}
