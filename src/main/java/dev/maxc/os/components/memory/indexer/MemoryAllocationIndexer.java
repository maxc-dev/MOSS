package dev.maxc.os.components.memory.indexer;

import dev.maxc.os.components.memory.model.GroupedMemoryAddress;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public abstract class MemoryAllocationIndexer {
    public abstract GroupedMemoryAddress getIndexAddressSlot(int size);
}
