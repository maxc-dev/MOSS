package dev.maxc.os.io.exceptions.instruction;

/**
 * @author Max Carter
 * @since 07/05/2020
 */
public class UnknownOpcodeException extends Exception {
    public UnknownOpcodeException(String opcode) {
        super("The following opcode is not in the recognised instruction set [" + opcode + "]");
    }
}
