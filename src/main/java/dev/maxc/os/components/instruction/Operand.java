package dev.maxc.os.components.instruction;

/**
 * @author Max Carter
 * @since 06/05/2020
 */
public class Operand {
    private final boolean immediate;
    private final int value;

    public Operand(boolean immediate, int value) {
        this.immediate = immediate;
        this.value = value;
    }

    /**
     * If the operand uses immediate addressing, so the value is a reference
     * to another address in memory
     */
    public boolean isImmediate() {
        return immediate;
    }

    /**
     * If the operand uses direct addressing, so the value is an absolute
     * value
     */
    public boolean isDirect() {
        return !immediate;
    }

    public int get() {
        return value;
    }

    @Override
    public String toString() {
        return (isDirect() ? "#" : "") + get();
    }
}
