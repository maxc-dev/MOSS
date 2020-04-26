package dev.maxc.os.components.memory.model;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class MemoryAddress {
    private final int index;
    private final MemoryUnit memoryUnit = new MemoryUnit(this);

    public MemoryAddress(int index) {
        this.index = index;
    }

    /**
     * Gets the physical index of the memory address in the RAM
     */
    public int getIndex() {
        return index;
    }

    public MemoryUnit getMemoryUnit() {
        return memoryUnit;
    }

    @Override
    public String toString() {
        return Integer.toHexString(index);
    }
}
