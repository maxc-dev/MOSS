package dev.maxc.os.components.memory.model;

/**
 * @author Max Carter
 * @since 01/05/2020
 */
public class CacheMemoryNode {
    private final int parentProcessID;
    private final int offset;
    private final MemoryUnit memoryUnit;

    public CacheMemoryNode(int parentProcessID, int offset, MemoryUnit memoryUnit) {
        this.parentProcessID = parentProcessID;
        this.offset = offset;
        this.memoryUnit = memoryUnit;
    }

    public int getParentProcessID() {
        return parentProcessID;
    }

    public int getOffset() {
        return offset;
    }

    public MemoryUnit getMemoryUnit() {
        return memoryUnit;
    }
}
