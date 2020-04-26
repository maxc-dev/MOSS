package dev.maxc.os.components.memory;

import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class Page extends LogicalMemoryHandler {
    private final RandomAccessMemory ram;
    private final LogicalMemoryHandlerUtils utils;
    private final ArrayList<SubPage> pages = new ArrayList<>();

    public Page(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, int id, int parentProcessID) {
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
    protected void allocate(GroupedMemoryAddress groupedMemoryAddress) {
        SubPage page = null;
        for (int i = 0; i < groupedMemoryAddress.getEndPointer() - groupedMemoryAddress.getStartPointer(); i++) {
            if (i % utils.getInitialSize() == 0) {
                page = new SubPage(utils.getInitialSize());
                pages.add(page);
            }
            page.addMemoryUnit(ram.get(i).getMemoryUnit());
        }
    }

    /**
     * Gets the Memory Unit which has it's own offset in the page.
     */
    @Override
    protected MemoryUnit getMemoryUnit(int offset) {
        int pageId = (int) Math.floor((double) offset/utils.getInitialSize());
        int pageOffset = offset % utils.getInitialSize();
        return pages.get(pageId).getMemoryUnit(pageOffset);
    }

    /**
     * A fixed size page which stores several Memory Units. The size is equal
     * to every other page and cannot increase in size.
     */
    private static final class SubPage {
        private final ArrayList<MemoryUnit> memoryUnits = new ArrayList<>();
        private final int maxSize;

        public SubPage(int maxSize) {
            this.maxSize = maxSize;
        }

        protected void addMemoryUnit(MemoryUnit memoryUnit) {
            if (memoryUnits.size()+1 > maxSize) {
                Logger.log(Status.ERROR, this, "Attempted to add a Memory Unit to a page which is full.");
                return;
            }
            memoryUnits.add(memoryUnit);
        }

        public MemoryUnit getMemoryUnit(int index) {
            if (Math.abs(index) >= memoryUnits.size()) {
                Logger.log(Status.ERROR, this, "Attempted to get a Memory Unit at a non existent offset.");
                return null;
            }
            return memoryUnits.get(index);
        }
    }

    @Override
    protected void free() {
        pages.clear();
        Logger.log(this, "The Memory Units for [" + super.toString() + "] have been cleared.");
    }
}
