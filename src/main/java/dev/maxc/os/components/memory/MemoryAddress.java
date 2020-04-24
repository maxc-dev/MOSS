package dev.maxc.os.components.memory;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class MemoryAddress {
    private final Page parent;
    private final int offset;
    private final MemoryUnit memoryUnit = new MemoryUnit(this);

    public MemoryAddress(Page parentPage, int offset) {
        this.parent = parentPage;
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public MemoryUnit getMemoryUnit() {
        return memoryUnit;
    }

    @Override
    public String toString() {
        return parent.toString() + Integer.toHexString(offset);
    }
}
