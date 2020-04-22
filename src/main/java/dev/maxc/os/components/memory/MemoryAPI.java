package dev.maxc.os.components.memory;

import dev.maxc.logs.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class MemoryAPI {
    private final RandomAccessMemory ram;
    private final int pageIncreaseIncrement;
    private final boolean isDynamicAllocation;
    private final AtomicInteger pageCount = new AtomicInteger(-1);

    public MemoryAPI(RandomAccessMemory ram, boolean isDynamicAllocation, int pageIncreaseIncrement) {
        this.ram = ram;
        this.pageIncreaseIncrement = pageIncreaseIncrement;
        this.isDynamicAllocation = isDynamicAllocation;
    }

    /**
     * Creates a new Page
     */
    protected Page createNewPage(int processIdentifier) {
        return new Page(processIdentifier, pageCount.addAndGet(1), ram.getInitialPageSize());
    }

    /**
     * Allocates static memory to a new process
     */
    public boolean allocateMemoryNewProcess(int processIdentifier) {
        if (ram.getMemoryStatus().getAllocateNewProcess()) {
            createNewPage(processIdentifier);
            ram.reconfigureMemoryStatus();
            return true;
        }
        Logger.log("Memory", "Unable to allocate memory to process [" + processIdentifier + "] for reason [" + ram.getMemoryStatus().toString() + "]");
        return false;
    }

    /**
     * Allocates additional memory to a page, this operation fails if dynamic memory allocation is disabled in the config
     */
    public boolean allocateMoreMemory(int processIdentifier) {
        if (isDynamicAllocation) {
            if (pageIncreaseIncrement <= ram.getFreeMemory()) {
                ram.allocateToProcess(processIdentifier, pageIncreaseIncrement);
            }
        } else {
            Logger.log("Memory", "Unable to allocate more memory to process [" + processIdentifier + "] because dynamic memory allocation is disabled in the config.");
        }
        return false;
    }
}
