package dev.maxc.os.components.memory.indexer;

import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.model.GroupedMemoryAddress;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public abstract class MemoryAllocationIndexer {
    private final RandomAccessMemory ram;

    public MemoryAllocationIndexer(RandomAccessMemory ram) {
        this.ram = ram;
    }

    public final RandomAccessMemory getRam() {
        return ram;
    }

    public abstract GroupedMemoryAddress getIndexAddressSlot(int size);
}
