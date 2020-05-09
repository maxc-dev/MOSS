package dev.maxc.os.components.memory.allocation;

import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.io.exceptions.memory.MemoryUnitAddressException;
import dev.maxc.os.io.exceptions.memory.MemoryUnitNotFoundException;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 26/04/2020
 */
public abstract class LogicalMemoryInterface {
    protected final ArrayList<MemoryUnit> memoryUnits = new ArrayList<>();
    private final LogicalMemoryHandlerUtils utils;
    private int initialSize;
    private final int startingLogicalPointer;

    public LogicalMemoryInterface(int startingLogicalPointer, LogicalMemoryHandlerUtils utils) {
        this.initialSize = utils.getInitialSize();
        this.utils = utils;
        this.startingLogicalPointer = startingLogicalPointer;
    }

    public final void free() {
        for (MemoryUnit unit : memoryUnits) {
            unit.clear();
        }
        memoryUnits.clear();
    }

    public void increase() {
        initialSize += utils.getIncrease();
    }

    protected final boolean addMemoryUnit(MemoryUnit memoryUnit) {
        if (memoryUnits.size() + 1 > initialSize) {
            return false;
        }
        memoryUnits.add(memoryUnit);
        memoryUnit.allocate(startingLogicalPointer + memoryUnits.indexOf(memoryUnit));
        return true;
    }

    public int getStartingLogicalPointer() {
        return startingLogicalPointer;
    }

    public final MemoryUnit getMemoryUnit(int index) throws MemoryUnitNotFoundException {
        if (index >= memoryUnits.size() || index < 0) {
            throw new MemoryUnitNotFoundException(index);
        }
        return memoryUnits.get(index);
    }
}
