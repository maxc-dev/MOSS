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
        if (size > getRam().getMemorySize() || getRam().isFull()) {
            super.throwOutOfMemory();
            return null;
        }

        int startPointer = -1;
        int endPointer = -1;
        final int ramSize = getRam().getMemorySize();
        Logger.log(this, "The current memory is [" + getRam().getUsedMemory() + "/" + ramSize + "]");

        for (int i = 0; i < ramSize; i++) {
            if (endPointer != -1) {
                if (getRam().get(i).getMemoryUnit().isActive()) {
                    Logger.log(Status.DEBUG, this, "RAM slot " + i + " is already active, resetting pointers.");
                    //one of the mem units is active so cant be used
                    endPointer = -1;
                    startPointer = -1;
                } else if (i == endPointer) {
                    //the whole section is clear
                    Logger.log(this, "Successfully allocated [" + size + "] to the main memory.");
                    return new GroupedMemoryAddress(startPointer, endPointer);
                }
            } else if (i + size -1 < ramSize) {
                if (!getRam().get(i + size - 1).getMemoryUnit().isActive() && !getRam().get(i).getMemoryUnit().isActive()) {
                    startPointer = i;
                    endPointer = i + size - 1;
                    i--;
                }
            }
        }

        super.throwOutOfMemory();
        return null;
    }
}
