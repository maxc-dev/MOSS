package dev.maxc.os.components.cpu;

import dev.maxc.os.components.memory.model.CacheMemoryNode;
import dev.maxc.os.structures.EvictingQueue;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Cache extends EvictingQueue<CacheMemoryNode> {
    public Cache(int size) {
        super(size);
    }
}
