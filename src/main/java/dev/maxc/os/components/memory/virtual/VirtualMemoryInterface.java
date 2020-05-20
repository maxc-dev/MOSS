package dev.maxc.os.components.memory.virtual;

import dev.maxc.os.components.disk.DiskDrive;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandler;
import dev.maxc.os.io.exceptions.disk.MemoryHandlerNotFoundException;
import dev.maxc.os.io.log.Logger;

/**
 * @author Max Carter
 * @since 20/05/2020
 */
public class VirtualMemoryInterface {
    private final DiskDrive diskDrive;
    private final MemoryManagementUnit mmu;

    public VirtualMemoryInterface(MemoryManagementUnit mmu, DiskDrive diskDrive) {
        this.mmu = mmu;
        this.diskDrive = diskDrive;
    }

    /**
     * Inserts a required process back into the main memory
     */
    public synchronized void pullFromDisk(int requiredProcess) throws MemoryHandlerNotFoundException {
        for (int i = 0; i < diskDrive.size(); i++) {
            if (diskDrive.get(i).getHandler().getId() == requiredProcess) {
                Logger.log(this, "Writing process [" + requiredProcess + "] back into main memory");
                VirtualMemoryDiskNode diskNode = diskDrive.remove(i);
                diskNode.getHandler().setInMainMemory();
                mmu.allocateMemoryHandler(diskNode);
                diskNode.getHandler().writeToPhysicalMemory(diskNode.getInstructions());
                return;
            }
        }
        throw new MemoryHandlerNotFoundException(diskDrive, requiredProcess);
    }

    /**
     * Copies physical content of the memory into a node which is saved
     * into the virtual memory.
     */
    public synchronized void pushToDisk(LogicalMemoryHandler pushedHandler) {
        Logger.log(this, "Pushing process [" + pushedHandler.getId() + "] into disk drive.");
        pushedHandler.setInVirtualMemory();
        VirtualMemoryDiskNode diskNode = new VirtualMemoryDiskNode(pushedHandler);
        diskNode.addInstructions(pushedHandler.getPhysicalInstructions());
        pushedHandler.free();
        diskDrive.add(diskNode);
    }

    public void requestDiskCleanUp() {
        diskDrive.clean();
    }
}
