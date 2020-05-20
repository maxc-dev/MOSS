package dev.maxc.os.components.memory.allocation;

import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.io.exceptions.memory.MemoryLogicalHandlerFullException;
import dev.maxc.os.io.exceptions.memory.MemoryUnitNotFoundException;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class Paging extends LogicalMemoryHandler {
    private final RandomAccessMemory ram;
    private final LogicalMemoryHandlerUtils utils;
    private final ArrayList<Frame> frames = new ArrayList<>();

    public Paging(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, int id, int parentProcessID) {
        super(id, parentProcessID);
        this.utils = utils;
        this.ram = ram;
    }

    /**
     * The page logical memory handler handles allocations by creating
     * additional frames of a fixed size and appending them to a paging
     * list.
     */
    @Override
    public void allocate(AddressPointerSet point) {
        Frame frame = null;
        int count = 0;
        for (int i = point.getStartPointer(); i <= point.getEndPointer(); i++) {
            if (count % utils.getInitialSize() == 0) {
                int startingLogicalPointer = frames.size()*utils.getInitialSize();
                frame = new Frame(startingLogicalPointer, utils);
                frames.add(frame);
            }
            frame.addMemoryUnit(ram.get(i).getMemoryUnit());
            count++;
        }
    }

    /**
     * Gets the Memory Unit which has it's own offset in the frame.
     */
    @Override
    public MemoryUnit getMemoryUnit(int offset) throws MemoryUnitNotFoundException {
        int pageId = (int) Math.floor((double) offset/utils.getInitialSize());
        int pageOffset = offset % utils.getInitialSize();
        return frames.get(pageId).getMemoryUnit(pageOffset);
    }

    /**
     * Clears up all the memory used by all the different frames.
     */
    @Override
    public void free() {
        for (Frame frame : frames) {
            frame.free();
        }
        frames.clear();
    }

    @Override
    public int getNextUnitOffset() throws MemoryLogicalHandlerFullException {
        for (Frame frame : frames) {
            for (MemoryUnit unit : frame.memoryUnits) {
                if (!unit.inUse()) {
                    return unit.getLogicalAddress();
                }
            }
        }
        throw new MemoryLogicalHandlerFullException();
    }

    /**
     * A fixed size frame which stores several Memory Units. The size is equal
     * to every other frame and cannot increase in size.
     */
    private static final class Frame extends LogicalMemoryInterface {
        public Frame(int startingLogicalPointer, LogicalMemoryHandlerUtils utils) {
            super(startingLogicalPointer, utils);
        }

        /**
         * Overrides the increase method on a frame since frames are static
         * and they cannot increase in size.
         */
        @Override
        public final void increase() {
        }
    }
}
