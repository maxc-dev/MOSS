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

    public Opcode getOpcode() {
        return opcode;
    }

    public int getOperand() {
        return operand;
    }
}
