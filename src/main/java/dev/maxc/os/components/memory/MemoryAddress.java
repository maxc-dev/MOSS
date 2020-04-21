package dev.maxc.os.components.memory;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class MemoryAddress {
    private final Page parentPage;
    private final int index;

    public MemoryAddress(Page parentPage, int index) {
        this.parentPage = parentPage;
        this.index = index;
    }

    public Page getParentPage() {
        return parentPage;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return parentPage.toString() + Integer.toHexString(index);
    }
}
