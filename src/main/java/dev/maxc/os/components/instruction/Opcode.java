package dev.maxc.os.components.instruction;

/**
 * @author Max Carter
 * @since 01/05/2020
 */
public enum Opcode {
    OUT(false),        //OUT 4 will output 4
    STR(false),        //STR 2 will store 2
    ADD(true),         //ADD 2 3 will store 2 + 3
    SUB(true),         //SUB 2 3 will store 2 - 3
    MUL(true),         //MUL 2 3 will store 2 * 3
    DIV(true);         //DIV 2 3 will store 2 / 3

    private final boolean secondOperand;

    public boolean needsSecondOperand() {
        return secondOperand;
    }

    Opcode(boolean secondOperand) {
        this.secondOperand = secondOperand;
    }

    @Override
    public String toString() {
        return name();
    }
}
