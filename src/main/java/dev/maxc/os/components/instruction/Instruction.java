package dev.maxc.os.components.instruction;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Instruction {
    private final Opcode opcode;
    private final Operand operand1;
    private final Operand operand2;

    /**
     * Creates an instruction with an opcode and two operands.
     */
    public Instruction(Opcode opcode, Operand operand1, Operand operand2) {
        this.opcode = opcode;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    /**
     * Creates an instruction with an opcode and an operand.
     */
    public Instruction(Opcode opcode, Operand operand1) {
        this.opcode = opcode;
        this.operand1 = operand1;
        this.operand2 = null;
    }

    /**
     * Gets the opcode - the type of instruction such as add or sub.
     */
    public Opcode getOpcode() {
        return opcode;
    }

    /**
     * Gets the first operand which is the value of the instruction.
     */
    public Operand getOperand1() {
        return operand1;
    }

    /**
     * Gets the second operand which is the value of the instruction.
     */
    public Operand getOperand2() {
        return operand2;
    }

    @Override
    public String toString() {
        return opcode.toString() + " " + operand1.toString() + (operand2 == null ? "" : " " + operand2.toString());
    }
}
