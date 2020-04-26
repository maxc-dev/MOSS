package dev.maxc.os.io.exceptions.memory;

import dev.maxc.os.components.memory.LogicalMemoryHandler;

/**
 * @author Max Carter
 * @since 26/04/2020
 */
public class InvalidLogicalMemoryHandler extends Exception {
    public <T extends LogicalMemoryHandler> InvalidLogicalMemoryHandler(Class<T> type, int processIdentifier) {
        super("Unable to interpret a valid handler for [" + type.getName() + "] for process [" + processIdentifier +"]");
    }
}
