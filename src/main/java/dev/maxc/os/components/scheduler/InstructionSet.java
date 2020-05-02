package dev.maxc.os.components.scheduler;

import dev.maxc.os.components.instruction.Instruction;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public final class InstructionSet {
    private final Instruction instruction;
    private final int processIdentifier;
    private final int offset;

    public InstructionSet(int processIdentifier, int offset, Instruction instruction) {
        this.instruction = instruction;
        this.processIdentifier = processIdentifier;
        this.offset = offset;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public int getProcessIdentifier() {
        return processIdentifier;
    }

    public int getOffset() {
        return offset;
    }
}
