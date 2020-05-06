package dev.maxc.os.components.instruction;

/**
 * @author Max Carter
 * @since 01/05/2020
 */
public enum Opcode {
    OUT,        //OUT 4 will output 4
    STR,        //STR 2 will store 2
    ADD,        //ADD 2 3 will store 2 + 3
    SUB,        //SUB 2 3 will store 2 - 3
    MUL,        //MUL 2 3 will store 2 * 3
    DIV;        //DIV 2 3 will store 2 / 3

    @Override
    public String toString() {
        return name();
    }
}
