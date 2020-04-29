package dev.maxc.os.components.memory.allocation;

import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
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
    public void allocate(AddressPointerSet pointerSet) {
        for (int i = pointerSet.getStartPointer(); i < pointerSet.getEndPointer() + 1; i++) {
            while (!segment.addMemoryUnit(ram.get(i).getMemoryUnit())) {
                segment.increase();
            }
        }
    }

    @Override
    public MemoryUnit getMemoryUnit(int offset) {
        return segment.getMemoryUnit(offset);
    }

    @Override
    public void free() {
        segment.free();
        Logger.log(this, "The Memory Units for [" + super.toString() + "] have been unallocated.");
    }

    private static final class Segment extends LogicalMemoryInterface {
        public Segment(LogicalMemoryHandlerUtils utils) {
            super(utils);
        }
    }
}
