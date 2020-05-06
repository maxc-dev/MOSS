package dev.maxc.os.io.exceptions.memory;

import dev.maxc.os.components.memory.model.MemoryUnit;

/**
 * @author Max Carter
 * @since 05/05/2020
 */
public class MemoryUnitAddressException extends Exception {
    public MemoryUnitAddressException(MemoryUnit unit) {
        super("The memory unit [" + unit.toString() + "] does not belong to a registered address in memory.");
    }
}
