package dev.maxc.os.components.memory;

import dev.maxc.logs.Logger;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class RandomAccessMemory extends ArrayList<Page> {
    private final int maxMemorySize;
    private final int initialPageSize;
    private final boolean usingVirtualMemory;
    private MemoryStatus memoryStatus = MemoryStatus.EMPTY;

    public RandomAccessMemory(int maxMemorySize, int initialPageSize, boolean usingVirtualMemory) {
        this.maxMemorySize = maxMemorySize;
        this.initialPageSize = initialPageSize;
        this.usingVirtualMemory = usingVirtualMemory;
    }

    public MemoryStatus getMemoryStatus() {
        return memoryStatus;
    }

    /**
     * Reconfigures the RAM status
     */
    protected void reconfigureMemoryStatus() {
        int freeMemory = getFreeMemory();
        if (freeMemory == 0) {
            memoryStatus = MemoryStatus.FULL;

        } else if (freeMemory == maxMemorySize) {
            memoryStatus = MemoryStatus.EMPTY;

        } else if (freeMemory >= initialPageSize) {
            memoryStatus = MemoryStatus.SPACIOUS;

        } else {
            memoryStatus = MemoryStatus.LIMITED_ROOM;
        }
    }

    public int getInitialPageSize() {
        return initialPageSize;
    }

    protected int getFreeMemory() {
        int memory = maxMemorySize;
        for (Page page : this) {
            memory -= page.getSize();
        }
        return memory;
    }

    protected void allocateToProcess(int processIdentifier, int amount) {
        for (Page page : this) {
            if (page.getParentProcess() == processIdentifier) {
                page.allocate(amount);
                Logger.log("Memory", "Page [" + page.toString() + "] was allocated an additional [" + amount + "] location addresses.");
                return;
            }
        }
    }

    protected enum MemoryStatus {
        EMPTY(true),          //ram is empty
        SPACIOUS(true),       //ram has some pages but is not full
        LIMITED_ROOM(false),   //there is room but not enough for a new page
        FULL(false);           //there is no more room in the main memory

        private final boolean canAllocateNewProcess;

        MemoryStatus(boolean canAllocateNewProcess) {
            this.canAllocateNewProcess = canAllocateNewProcess;
        }

        public boolean getAllocateNewProcess() {
            return canAllocateNewProcess;
        }
    }
}
