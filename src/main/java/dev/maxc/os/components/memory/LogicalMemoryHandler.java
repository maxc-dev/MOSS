package dev.maxc.os.components.memory;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public abstract class LogicalMemoryHandler {
    private final int id;
    private final int parentProcessID;

    public LogicalMemoryHandler(int id, int parentProcessID) {
        this.id = id;
        this.parentProcessID = parentProcessID;
    }

    public final int getId() {
        return id;
    }

    public final int getParentProcessID() {
        return parentProcessID;
    }

    /**
     * Allocates a pointer range of more memory addresses
     */
    protected abstract void allocate(GroupedMemoryAddress groupedMemoryAddress);

    /**
     * Gets the memory unit at a specific offset in the logical memory
     */
    protected abstract MemoryUnit getMemoryUnit(int offset);

    /**
     * Frees the memory used by the process
     */
    protected abstract void free();

    @Override
    public String toString() {
        return Integer.toHexString(getId());
    }
}
