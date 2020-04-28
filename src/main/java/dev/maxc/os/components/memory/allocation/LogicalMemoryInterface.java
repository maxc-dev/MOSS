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
    private final ArrayList<MemoryUnit> memoryUnits = new ArrayList<>();
    private final LogicalMemoryHandlerUtils utils;
    private int initialSize;

    public LogicalMemoryInterface(LogicalMemoryHandlerUtils utils) {
        this.initialSize = utils.getInitialSize();
        this.utils = utils;
    }

    public final void free() {
        for (MemoryUnit unit : memoryUnits) {
            unit.setActive(false);
        }
        memoryUnits.clear();
    }

    public void increase() {
        initialSize += utils.getIncrease();
    }

    protected final boolean addMemoryUnit(MemoryUnit memoryUnit) {
        if (memoryUnits.size() + 1 > initialSize) {
            Logger.log(Status.WARN, this, "Attempted to add a Memory Unit to a page which is full.");
            return false;
        }
        memoryUnits.add(memoryUnit);
        return true;
    }

    public final MemoryUnit getMemoryUnit(int index) {
        if (Math.abs(index) >= memoryUnits.size()) {
            Logger.log(Status.ERROR, this, "Attempted to get a Memory Unit at a non existent offset.");
            return null;
        }
        return memoryUnits.get(index);
    }
}
