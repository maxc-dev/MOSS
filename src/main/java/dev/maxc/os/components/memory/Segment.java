package dev.maxc.os.components.memory;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public class Segment extends LogicalMemoryHandler {
    private final RandomAccessMemory ram;
    private final LogicalMemoryHandlerUtils utils;

    public Segment(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, int id, int parentProcessID) {
        super(id, parentProcessID);
        this.ram = ram;
        this.utils = utils;
    }


    @Override
    protected void allocate(GroupedMemoryAddress groupedMemoryAddress) {

    }

    @Override
    protected MemoryUnit getMemoryUnit(int offset) {
        return null;
    }

    @Override
    protected void free() {

    }
}
