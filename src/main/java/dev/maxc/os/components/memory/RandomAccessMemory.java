package dev.maxc.os.components.memory;

import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.memory.indexer.MemoryAllocationIndexer;
import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.components.memory.model.MemoryAddress;
import dev.maxc.os.components.memory.virtual.VirtualMemoryInterface;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class RandomAccessMemory extends ArrayList<MemoryAddress> {
    private final int memorySize;
    private MemoryAllocationIndexer mallocIndexer;

    public <T extends MemoryAllocationIndexer> RandomAccessMemory(int memoryPowerSize, Class<T> mallocIndexerClass) {
        super((int) Math.pow(2, memoryPowerSize));
        this.memorySize = (int) Math.pow(2, memoryPowerSize);
        Logger.log(this, "Main memory created of size [" + getMemorySize() + "]");
        try {
            this.mallocIndexer = mallocIndexerClass.getConstructor(RandomAccessMemory.class).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Logger.log(Status.ERROR, this, "Unable to initialize the configured memory allocation indexer [" + mallocIndexerClass.getSimpleName() + ".class] Defaulting to manual First Fit initialization.");
            e.printStackTrace();
            this.mallocIndexer = new FirstFit(this);
        }

        /*
            Populates the main memory with individual memory addresses.
            Each address is assigned a different index which represents
            its position in the main memory.
         */
        for (int i = 0; i < getMemorySize(); i++) {
            add(new MemoryAddress(i));
        }
    }

    public void initVirtualMemoryInterface(VirtualMemoryInterface vmi) {
        mallocIndexer.initVirtualMemoryInterface(vmi);
    }

    public void initMemoryManagementUnit(MemoryManagementUnit mmu) {
        mallocIndexer.initMemoryManagementUnit(mmu);
    }

    public int getMemorySize() {
        return memorySize;
    }

    /**
     * Calculates the amount of locations in memory that are free
     */
    public int getAvailableMemory() {
        int memory = getMemorySize();
        for (MemoryAddress memoryAddress : this) {
            if (memoryAddress.getMemoryUnit().isAllocated()) {
                memory--;
            }
        }
        return memory;
    }

    /**
     * Calculates the amount of locations in memory that are in use.
     */
    public int getAllocatedMemory() {
        int memory = 0;
        for (MemoryAddress memoryAddress : this) {
            if (memoryAddress.getMemoryUnit().isAllocated()) {
                memory++;
            }
        }
        return memory;
    }

    public boolean isFull() {
        return getAvailableMemory() == 0;
    }

    /**
     * Uses the Memory Allocation Indexer to allocate a space in the memory to store
     * the process. A start pointer and an end pointer are returned and are used to
     * identify which parts of the memory will be allocated to the process.
     *
     * The returned address set is used to mark all the corresponding memory
     * units as active.
     */
    public AddressPointerSet indexMemory(int size) throws OutOfMemoryError {
        return mallocIndexer.getIndexedAddressSlot(size);
    }
}
