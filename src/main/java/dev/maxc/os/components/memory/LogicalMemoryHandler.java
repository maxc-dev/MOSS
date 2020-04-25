package dev.maxc.os.components.memory;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public abstract class LogicalMemoryHandler {
    private final RandomAccessMemory ram;
    private final int id;
    private final int parentProcessID;
    private ArrayList<GroupedMemoryAddress> memoryAddresses = new ArrayList<>();

    public LogicalMemoryHandler(RandomAccessMemory ram, int id, int parentProcessID) {
        this.ram = ram;
        this.id = id;
        this.parentProcessID = parentProcessID;
    }

    public int getId() {
        return id;
    }

    public int getParentProcessID() {
        return parentProcessID;
    }

    protected ArrayList<GroupedMemoryAddress> getMemoryAddresses() {
        return memoryAddresses;
    }

    private int getSize() {
        int size = 0;
        for (GroupedMemoryAddress groupedMemoryAddress : memoryAddresses) {
            size += groupedMemoryAddress.getEndPointer() - groupedMemoryAddress.getStartPointer();
        }
        return size;
    }

    /**
     * Frees the memory used by the process
     */
    public void free() {
        memoryAddresses.clear();
    }

    /**
     * Allocates a pointer range of more memory addresses
     */
    protected void allocate(GroupedMemoryAddress groupedMemoryAddress) {
        memoryAddresses.add(groupedMemoryAddress);
        handle(groupedMemoryAddress);
    }

    protected abstract void handle(GroupedMemoryAddress groupedMemoryAddress);

    public int getUsedSpace() {
        int usedSpace = 0;
        for (GroupedMemoryAddress groupedMemoryAddress : memoryAddresses) {
            for (int i = groupedMemoryAddress.getStartPointer(); i < groupedMemoryAddress.getEndPointer(); i++) {
                if (ram.get(i).getMemoryUnit().isActive()) {
                    usedSpace++;
                }
            }
        }
        return usedSpace;
    }

    public int getFreeSpace() {
        return getSize() - getUsedSpace();
    }

    @Override
    public String toString() {
        return Integer.toHexString(getId());
    }
}
