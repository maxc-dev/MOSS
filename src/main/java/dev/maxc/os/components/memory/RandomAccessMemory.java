package dev.maxc.os.components.memory;

import dev.maxc.os.structures.Heap;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class RandomAccessMemory extends Heap<Page> {
    private final int maxMemorySize;

    public RandomAccessMemory(int maxMemorySize) {
        this.maxMemorySize = maxMemorySize;
    }
}
