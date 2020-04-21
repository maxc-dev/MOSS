package dev.maxc.os.components.memory;

/**
 * @author Max Carter
 * @since 21/04/2020
 */
public class Page {
    private final int pageId;
    private final int maxSize;

    public Page(int pageId, int maxSize) {
        this.pageId = pageId;
        this.maxSize = maxSize;
    }

    @Override
    public String toString() {
        return Integer.toHexString(pageId);
    }
}
