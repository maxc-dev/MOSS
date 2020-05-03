package dev.maxc.os.components.instruction;

/**
 * @author Max Carter
 * @since 01/05/2020
 */
public enum Opcode {
    STR,        //STR 1 2 will store 2 in offset 1
    ADD,        //ADD 1 2 3 will store 2 + 3 in offset 1
    SUB,        //SUB 1 2 3 will store 2 - 3 in offset 1
    MUL,        //MUL 1 2 3 will store 2 * 3 in offset 1
    DIV         //DIV 1 2 3 will store 2 / 3 in offset 1
}
