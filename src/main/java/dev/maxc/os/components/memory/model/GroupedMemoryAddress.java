package dev.maxc.os.components.memory.model;

import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public class GroupedMemoryAddress {
    private final int startPointer;
    private final int endPointer;

    public GroupedMemoryAddress(int startPointer, int endPointer) {
        if (endPointer < startPointer) {
            Logger.log(Status.ERROR, this, "A grouped memory address pointer set was created whereby the end pointer is greater than the start pointer.");
            throw new IllegalArgumentException("The end pointer [" + endPointer + "] must be greater than the start pointer [" + startPointer + "].");
        }
        this.startPointer = startPointer;
        this.endPointer = endPointer;
    }

    public int getStartPointer() {
        return startPointer;
    }

    public int getEndPointer() {
        return endPointer;
    }
}
