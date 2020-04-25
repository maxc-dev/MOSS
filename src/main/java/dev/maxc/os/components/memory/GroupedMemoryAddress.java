package dev.maxc.os.components.memory;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public class GroupedMemoryAddress {
    private int startPointer;
    private int endPointer;

    public GroupedMemoryAddress(int startPointer, int endPointer) {
        this.startPointer = startPointer;
        this.endPointer = endPointer;
    }

    public int getStartPointer() {
        return startPointer;
    }

    public void setStartPointer(int startPointer) {
        this.startPointer = startPointer;
    }

    public int getEndPointer() {
        return endPointer;
    }

    public void setEndPointer(int endPointer) {
        this.endPointer = endPointer;
    }
}
