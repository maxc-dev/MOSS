package dev.maxc.os.components.memory.allocation;

import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.io.log.Logger;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class Paging extends LogicalMemoryHandler {
    private final RandomAccessMemory ram;
    private final LogicalMemoryHandlerUtils utils;
    private final ArrayList<Page> pages = new ArrayList<>();

    public Paging(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, int id, int parentProcessID) {
        super(id, parentProcessID);
        this.utils = utils;
        this.ram = ram;
    }

    /**
     * The page logical memory handler handles allocations by creating
     * additional pages of a fixed size and appending them to a paging
     * list.
     */
    @Override
    public void allocate(AddressPointerSet point) {
        Page page = null;
        int count = 0;
        for (int i = point.getStartPointer(); i <= point.getEndPointer(); i++) {
            if (count % utils.getInitialSize() == 0) {
                int startingLogicalPointer = pages.size()*utils.getInitialSize();
                page = new Page(startingLogicalPointer, utils);
                pages.add(page);
            }
            page.addMemoryUnit(ram.get(i).getMemoryUnit());
            count++;
        }
    }

    /**
     * Gets the Memory Unit which has it's own offset in the page.
     */
    @Override
    public MemoryUnit getMemoryUnit(int offset) {
        int pageId = (int) Math.floor((double) offset/utils.getInitialSize());
        int pageOffset = offset % utils.getInitialSize();
        return pages.get(pageId).getMemoryUnit(pageOffset);
    }

    /**
     * Clears up all the memory used by all the different pages.
     */
    @Override
    public void free() {
        for (Page page : pages) {
            page.free();
        }
        pages.clear();
        Logger.log(this, "The Memory Units for [" + super.toString() + "] have been unallocated.");
    }

    @Override
    public int getNextUnitOffset() {
        for (Page page : pages) {
            for (MemoryUnit unit : page.memoryUnits) {
                if (!unit.inUse()) {
                    return unit.getLogicalAddress();
                }
            }
        }
        return -1;
    }

    /**
     * A fixed size page which stores several Memory Units. The size is equal
     * to every other page and cannot increase in size.
     */
    private static final class Page extends LogicalMemoryInterface {
        public Page(int startingLogicalPointer, LogicalMemoryHandlerUtils utils) {
            super(startingLogicalPointer, utils);
        }

        /**
         * Overrides the increase method on a page since pages are static
         * and they cannot override.
         */
        @Override
        public final void increase() {
        }
    }
}
