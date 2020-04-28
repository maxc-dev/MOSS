package dev.maxc.os.components.memory.indexer;

import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.model.GroupedMemoryAddress;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public class FirstFit extends MemoryAllocationIndexer {
    public FirstFit(RandomAccessMemory ram) {
        super(ram);
    }

    @Override
    public GroupedMemoryAddress getIndexAddressSlot(int size) {
        Logger.log(this, "Attempting to find an allocation of size [" + size + "]");
        int startPointer = -1;
        int endPointer = -1;

        for (int i = 0; i < getRam().getMemorySize(); i++) {
            if (endPointer != -1) {
                if (getRam().get(i).getMemoryUnit().isActive()) {
                    Logger.log(Status.DEBUG, this, "RAM slot " + i + " is already active, resetting pointers");
                    //one of the mem units is active so cant be used
                    endPointer = -1;
                    startPointer = -1;
                } else if (i == endPointer) {
                    //the whole section is clear
                    return new GroupedMemoryAddress(startPointer, endPointer);
                }
            } else if (!getRam().get(i + size - 1).getMemoryUnit().isActive() && !getRam().get(i).getMemoryUnit().isActive()) {
                startPointer = i;
                endPointer = i + size - 1;
            }
        }
        Logger.log(Status.WARN, this, "Unable to allocate memory to process because there is no room available in the main memory.");
        //todo at this point we would request an index at virtual memory
        throw new OutOfMemoryError();
    }
}
