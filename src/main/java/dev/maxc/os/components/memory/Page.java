package dev.maxc.os.components.memory;

import dev.maxc.logs.Logger;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class Page extends ArrayList<MemoryAddress> {
    private final int pageId;
    private int size;
    private final int parentProcessID;

    public Page(int parentProcessID, int pageId, int initialSize) {
        this.parentProcessID = parentProcessID;
        this.pageId = pageId;
        this.size = initialSize;
        allocate(initialSize);
    }

    /**
     * Allocates `amount` many more memory addresses in this page
     */
    public void allocate(int amount) {
        size += amount;
        for (int i = size(); i < size() + amount; i++) {
            add(new MemoryAddress(this, i));
        }
    }

    public void clear() {
        while (!this.isEmpty()) {
            this.remove(0);
        }
        Logger.log("Memory", "Page [" + toString() + "] has been cleared.");
    }

    public int getParentProcessID() {
        return parentProcessID;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return Integer.toHexString(pageId);
    }
}
