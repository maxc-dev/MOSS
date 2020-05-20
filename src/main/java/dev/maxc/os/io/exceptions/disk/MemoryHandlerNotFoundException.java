package dev.maxc.os.io.exceptions.disk;

import dev.maxc.os.components.disk.DiskDrive;

/**
 * @author Max Carter
 * @since 20/05/2020
 */
public class MemoryHandlerNotFoundException extends Exception {
    public MemoryHandlerNotFoundException(DiskDrive drive, int processIdentifier) {
        super("Process Identifier [" + processIdentifier + "] could not be found in the storage drive [" + drive.toString() + "]");
    }
}
