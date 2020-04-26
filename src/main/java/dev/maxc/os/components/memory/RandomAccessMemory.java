package dev.maxc.os.components.memory;

import dev.maxc.os.components.memory.indexer.MemoryAllocationIndexer;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class RandomAccessMemory extends ArrayList<MemoryAddress> {
    private final int memoryBaseSize;
    private final int memoryPowerSize;
    private final MemoryAllocationIndexer mallocIndexer;
    private final boolean usingVirtualMemory;

    public RandomAccessMemory(int memoryBaseSize, int memoryPowerSize, MemoryAllocationIndexer mallocIndexer, boolean usingVirtualMemory) {
        this.memoryBaseSize = memoryBaseSize;
        this.mallocIndexer = mallocIndexer;
        this.memoryPowerSize = memoryPowerSize;
        this.usingVirtualMemory = usingVirtualMemory;

        /*
            Populates the main memory with individual memory addresses.
            Each address is assigned a different index which represents
            its position in the main memory.
         */
        for (int i = 0; i < getMemorySize(); i++) {
            add(new MemoryAddress(i));
        }
    }

    public int getMemorySize() {
        return (int) Math.pow(memoryBaseSize, memoryPowerSize);
    }

    /**
     * Calculates the amount of locations in memory that are free
     */
    protected int getFreeMemory() {
        int memory = getMemorySize();
        for (MemoryAddress memoryAddress : this) {
            if (!memoryAddress.getMemoryUnit().isActive()) {
                memory--;
            }
        }
        //TODO if virtual memory is being used, find out how much can be allocated
        return memory;
    }

    /**
     * Uses the Memory Allocation Indexer to allocate a space in the memory to store
     * the process. A start pointer and an end pointer are returned and are used to
     * identify which parts of the memory will be allocated to the process.
     */
    protected GroupedMemoryAddress getGroupedMemoryAddress(int size) {
        return mallocIndexer.getIndexAddressSlot(size);
    }
}
