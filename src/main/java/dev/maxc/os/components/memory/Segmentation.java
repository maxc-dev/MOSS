package dev.maxc.os.components.memory;

import dev.maxc.os.io.log.Logger;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public class Segmentation extends LogicalMemoryHandler {
    private final RandomAccessMemory ram;
    private final Segment segment;

    public Segmentation(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, int id, int parentProcessID) {
        super(id, parentProcessID);
        this.ram = ram;
        this.segment = new Segment(utils);
    }

    @Override
    protected void allocate(GroupedMemoryAddress groupedMemoryAddress) {
        for (int i = groupedMemoryAddress.getStartPointer(); i < groupedMemoryAddress.getEndPointer(); i++) {
            while (!segment.addMemoryUnit(ram.get(i).getMemoryUnit())) { //todo needs testing
                segment.increase();
            }
        }
    }

    @Override
    protected MemoryUnit getMemoryUnit(int offset) {
        return segment.getMemoryUnit(offset);
    }

    @Override
    protected void free() {
        segment.free();
        Logger.log(this, "The Memory Units for [" + super.toString() + "] have been cleared.");
    }

    private static final class Segment extends LogicalMemoryInterface {
        public Segment(LogicalMemoryHandlerUtils utils) {
            super(utils);
        }
    }
}
