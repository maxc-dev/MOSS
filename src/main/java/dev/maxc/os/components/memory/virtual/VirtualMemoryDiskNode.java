package dev.maxc.os.components.memory.virtual;

import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandler;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 20/05/2020
 */
public class VirtualMemoryDiskNode {
    private final LogicalMemoryHandler handler;
    private final ArrayList<Instruction> instructions = new ArrayList<>();

    public VirtualMemoryDiskNode(LogicalMemoryHandler handler) {
        this.handler = handler;
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void addInstructions(ArrayList<Instruction> instructions) {
        this.instructions.addAll(instructions);
    }

    public LogicalMemoryHandler getHandler() {
        return handler;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }
}
