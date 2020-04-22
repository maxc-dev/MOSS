package dev.maxc.os.components.memory;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class Page extends ArrayList<MemoryAddress> {
    private final int pageId;
    private int size;
    private final int parentProcess;

    public Page(int parentProcess, int pageId, int initialSize) {
        this.parentProcess = parentProcess;
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

    public int getParentProcess() {
        return parentProcess;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return Integer.toHexString(pageId);
    }
}
