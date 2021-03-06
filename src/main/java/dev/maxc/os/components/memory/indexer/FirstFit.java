package dev.maxc.os.components.memory.indexer;

import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.io.log.Logger;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public class FirstFit extends MemoryAllocationIndexer {
    public FirstFit(RandomAccessMemory ram) {
        super(ram);
    }

    @Override
    public AddressPointerSet getIndexedAddressSlot(int size) {
        Logger.log(this, "Finding an allocation of size [" + size + "] in the main memory [" + getRam().getAllocatedMemory() + "/" + getRam().getMemorySize() + "]...");
        if (size > getRam().getMemorySize() || getRam().isFull() || size + getRam().getAllocatedMemory() > getRam().getMemorySize()) {
            //resorts to virtual memory
            return handlerOutOfMemory(size);
        }

        int startPointer = -1;
        int endPointer = -1;
        final int ramSize = getRam().getMemorySize();

        for (int i = 0; i < ramSize; i++) {
            if (endPointer != -1) {
                if (getRam().get(i).getMemoryUnit().isAllocated()) {
                    //one of the mem units is active so cant be used
                    endPointer = -1;
                    startPointer = -1;
                } else if (i == endPointer) {
                    //the whole section is clear
                    return new AddressPointerSet(startPointer, endPointer);
                }
            } else if (i + size - 1 < ramSize) {
                if (!getRam().get(i + size - 1).getMemoryUnit().isAllocated() && !getRam().get(i).getMemoryUnit().isAllocated()) {
                    startPointer = i;
                    endPointer = i + size - 1;
                    i--;
                }
            }
        }

        throw new OutOfMemoryError();
    }
}
