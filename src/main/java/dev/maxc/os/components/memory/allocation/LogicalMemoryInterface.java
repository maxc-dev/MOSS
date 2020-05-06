package dev.maxc.os.components.memory.allocation;

import dev.maxc.os.components.memory.model.MemoryUnit;
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
        Logger.log(Status.DEBUG, this, "Memory Units cleared [" + memoryUnits.size() + "]");
        memoryUnits.clear();
    }

    public void increase() {
        initialSize += utils.getIncrease();
    }

    protected final boolean addMemoryUnit(MemoryUnit memoryUnit) {
        if (memoryUnits.size() + 1 > initialSize) {
            Logger.log(Status.WARN, this, "Attempted to add a Memory Unit to a handler which is full.");
            return false;
        }
        memoryUnits.add(memoryUnit);
        memoryUnit.allocate(startingLogicalPointer + memoryUnits.indexOf(memoryUnit));
        return true;
    }

    public int getStartingLogicalPointer() {
        return startingLogicalPointer;
    }

    public final MemoryUnit getMemoryUnit(int index) {
        if (Math.abs(index) >= memoryUnits.size()) {
            Logger.log(Status.ERROR, this, "Attempted to get a Memory Unit at a non existent offset.");
            return null;
        }
        return memoryUnits.get(index);
    }
}
