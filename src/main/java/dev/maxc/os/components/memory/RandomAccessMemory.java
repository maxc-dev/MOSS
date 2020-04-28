package dev.maxc.os.components.memory;

import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.memory.indexer.MemoryAllocationIndexer;
import dev.maxc.os.components.memory.model.GroupedMemoryAddress;
import dev.maxc.os.components.memory.model.MemoryAddress;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class RandomAccessMemory extends ArrayList<MemoryAddress> {
    private final int memoryBaseSize;
    private final int memoryPowerSize;
    private MemoryAllocationIndexer mallocIndexer;
    private final boolean usingVirtualMemory;

    public <T extends MemoryAllocationIndexer> RandomAccessMemory(int memoryBaseSize, int memoryPowerSize, Class<T> mallocIndexerClass, boolean usingVirtualMemory) {
        this.memoryBaseSize = memoryBaseSize;
        this.memoryPowerSize = memoryPowerSize;
        Logger.log(this, "Main memory created of size [" + getMemorySize() + "]");
        try {
            this.mallocIndexer = mallocIndexerClass.getConstructor().newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Logger.log(Status.ERROR, this, "Unable to initialize the configured memory allocation indexer [" + mallocIndexerClass.getSimpleName() + ".class] Defaulting to Best Fit.");
            //e.printStackTrace();
            this.mallocIndexer = new FirstFit(this); //todo once bestfit is created, use that instead of first fit
        }
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
     *
     * The returned address set is used to mark all the corresponding memory
     * units as active.
     */
    public GroupedMemoryAddress getGroupedMemoryAddress(int size) {
        GroupedMemoryAddress addressSet = mallocIndexer.getIndexAddressSlot(size);
        for (int i = addressSet.getStartPointer(); i < addressSet.getEndPointer() + 1; i++) {
            get(i).getMemoryUnit().setActive(true);
        }
        return addressSet;
    }
}
