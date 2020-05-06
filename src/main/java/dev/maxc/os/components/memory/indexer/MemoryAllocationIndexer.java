package dev.maxc.os.components.memory.indexer;

import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

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

    public final void throwOutOfMemory() {
        Logger.log(Status.ERROR, this, "Unable to allocate memory because the main memory is full [" + ram.getAllocatedMemory() + "/" + ram.getMemorySize() + "]");
        throw new OutOfMemoryError();
    }

    public abstract AddressPointerSet getIndexedAddressSlot(int size);
}
