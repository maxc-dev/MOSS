package dev.maxc.os.components.instruction;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Instruction {
    private final Opcode opcode;
    private final int offset;
    private final int operand1;
    private final int operand2;

    /**
     * Creates an instruction with an opcode and two operands.
     */
    public Instruction(Opcode opcode, int offset, int operand1, int operand2) {
        this.opcode = opcode;
        this.offset = offset;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    /**
     * Gets the opcode - the type of instruction such as add or sub.
     */
    public Opcode getOpcode() {
        return opcode;
    }

    /**
     * Gets the logical address where to store the result of the instruction
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Gets the first operand which is the value of the instruction.
     */
    public int getOperand1() {
        return operand1;
    }

    /**
     * Gets the second operand which is the value of the instruction.
     */
    public int getOperand2() {
        return operand2;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "opcode=" + opcode.name() +
                ", offset=" + offset +
                ", operand1=" + operand1 +
                ", operand2=" + operand2 +
                '}';
    }
}
