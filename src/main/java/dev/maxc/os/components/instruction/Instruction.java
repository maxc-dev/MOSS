package dev.maxc.os.components.instruction;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Instruction {
    private final Opcode opcode;
    private final int operand;

    public Instruction(Opcode opcode, int operand) {
        this.opcode = opcode;
        this.operand = operand;
    }

    /**
     * Gets the opcode - the type of instruction such as add or sub.
     */
    public Opcode getOpcode() {
        return opcode;
    }

    /**
     * Gets the operand which is the value of the instruction.
     */
    public int getOperand() {
        return operand;
    }
}
