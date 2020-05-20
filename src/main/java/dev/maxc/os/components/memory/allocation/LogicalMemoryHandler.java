package dev.maxc.os.components.memory.allocation;

import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.io.exceptions.memory.MemoryLogicalHandlerFullException;
import dev.maxc.os.io.exceptions.memory.MemoryUnitNotFoundException;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public abstract class LogicalMemoryHandler {
    private boolean isInMainMemory = true;
    private final int id;
    private final int parentProcessID;

    public LogicalMemoryHandler(int id, int parentProcessID) {
        this.id = id;
        this.parentProcessID = parentProcessID;
    }

    public boolean isInMainMemory() {
        return isInMainMemory;
    }
    public boolean isInVirtualMemory() {
        return !isInMainMemory;
    }

    public void setInMainMemory() {
        isInMainMemory = true;
    }

    public void setInVirtualMemory() {
        isInMainMemory = false;
    }

    public final int getId() {
        return id;
    }

    public final int getParentProcessID() {
        return parentProcessID;
    }

    /**
     * Allocates a pointer range of more memory addresses
     */
    public abstract void allocate(AddressPointerSet groupedMemoryAddress);

    /**
     * Gets the memory unit at a specific offset in the logical memory
     */
    public abstract MemoryUnit getMemoryUnit(int offset) throws MemoryUnitNotFoundException;

    /**
     * Frees the memory used by the process
     */
    public abstract void free();

    /**
     * Returns all the instructions related to the current frame.
     */
    public abstract ArrayList<Instruction> getPhysicalInstructions();

    /**
     * Writes a list of instructions into physical memory.
     */
    public abstract void writeToPhysicalMemory(ArrayList<Instruction> instructions);

    /**
     * Gets the next unallocated memory unit
     */
    public abstract int getNextUnitOffset() throws MemoryLogicalHandlerFullException;

    @Override
    public String toString() {
        return Integer.toHexString(getId());
    }
}
