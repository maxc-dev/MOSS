package dev.maxc.os.components.memory.indexer;

import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandler;
import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.components.memory.virtual.VirtualMemoryDiskNode;
import dev.maxc.os.components.memory.virtual.VirtualMemoryInterface;
import dev.maxc.os.io.exceptions.memory.virtual.OutOfHandlersException;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public abstract class MemoryAllocationIndexer {
    private final RandomAccessMemory ram;
    private MemoryManagementUnit mmu;
    private VirtualMemoryInterface virtualMemoryInterface = null;

    public MemoryAllocationIndexer(RandomAccessMemory ram) {
        this.ram = ram;
    }

    public void initMemoryManagementUnit(MemoryManagementUnit mmu) {
        this.mmu = mmu;
    }

    public void initVirtualMemoryInterface(VirtualMemoryInterface vmi) {
        this.virtualMemoryInterface = vmi;
    }

    public boolean usingVirtualMemory() {
        return virtualMemoryInterface != null;
    }

    public final RandomAccessMemory getRam() {
        return ram;
    }

    public final AddressPointerSet handlerOutOfMemory(int requiredSize) {
        if (usingVirtualMemory()) {
            Logger.log(this, "Main memory is full, attempting to swap unused processes to disk drive to make room.");
            int freedSpace = 0;
            do {
                LogicalMemoryHandler handler;
                try {
                    handler = mmu.getLeastUsedHandler();
                } catch (OutOfHandlersException e) {
                    Logger.log(Status.CRIT, this, "Incoming process size is too large and cannot fit amongst the processes currently running in main memory.");
                    e.printStackTrace();
                    freedSpace = requiredSize + 1;
                    continue;
                }
                VirtualMemoryDiskNode node = new VirtualMemoryDiskNode(handler);
                node.addInstructions(node.getHandler().getPhysicalInstructions());
                freedSpace += node.getInstructions().size();
                virtualMemoryInterface.pushToDisk(node);
            } while (freedSpace < requiredSize);
            return getIndexedAddressSlot(requiredSize);
        }
        //if no virtual memory is in use the program can't physically allocate any memory
        Logger.log(Status.ERROR, this, "Unable to allocate memory because the main memory is full [" + ram.getAllocatedMemory() + "/" + ram.getMemorySize() + "] terminating process.");
        throw new OutOfMemoryError();
    }

    public abstract AddressPointerSet getIndexedAddressSlot(int size) throws OutOfMemoryError;
}
