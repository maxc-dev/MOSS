package dev.maxc.os.components.cpu;

import dev.maxc.os.components.memory.model.MemoryAddress;
import dev.maxc.os.structures.Queue;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ProgramCounter extends Queue<MemoryAddress> {
    /**
     * Gets the next instruction address.
     * @return Returns null if the queue is empty.
     */
    public MemoryAddress getNextInstructionLocation() {
        return get();
    }
}
