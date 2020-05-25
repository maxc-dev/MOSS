package dev.maxc.os.components.cpu;

import dev.maxc.os.structures.Queue;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ProgramCounter extends Queue<Integer> {
    /**
     * Gets the next instruction address.
     *
     * @return Returns null if the queue is empty.
     */
    public Integer getNextInstructionLocation() {
        return get();
    }
}
