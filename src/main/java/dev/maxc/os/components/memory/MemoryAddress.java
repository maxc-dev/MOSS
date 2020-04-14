package dev.maxc.os.components.memory;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class MemoryAddress {
    private final int memoryAddressCode;

    public MemoryAddress(int memoryAddressCode) {
        this.memoryAddressCode = memoryAddressCode;
    }

    public int getMemoryAddressCode() {
        return memoryAddressCode;
    }
}
