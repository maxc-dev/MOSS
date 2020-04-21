package dev.maxc.os.system.api;

import dev.maxc.os.components.memory.Page;
import dev.maxc.os.components.memory.RandomAccessMemory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class MemoryAPI {
    private final RandomAccessMemory ram;
    private final int maxSize;
    private final AtomicInteger pageCount = new AtomicInteger();

    public MemoryAPI(RandomAccessMemory ram, int maxSize) {
        this.maxSize = maxSize;
        this.ram = ram;
    }

    /**
     * Creates a new Page
     */
    private Page createNewPage() {
        return new Page(pageCount.addAndGet(1), maxSize);
    }
}
