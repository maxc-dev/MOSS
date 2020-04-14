package dev.maxc.os.system;

import dev.maxc.os.components.virtual.Instruction;
import dev.maxc.os.structures.Stack;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class InstructionStack extends Stack<Instruction> {
    private Instruction pop(int pointer) {
        if (pointer > super.pointer) {
            return null;
        }
        Instruction item = super.stack.get(pointer);
        stack.remove(item);
        super.pointer--;
        return item;
    }
}
