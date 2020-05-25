package dev.maxc.os.components.disk;

import dev.maxc.os.components.memory.virtual.VirtualMemoryDiskNode;
import dev.maxc.os.io.log.Logger;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 20/05/2020
 */
public class DiskDrive extends ArrayList<VirtualMemoryDiskNode> {
    private final char driveSymbol;

    public DiskDrive(char driveSymbol) {
        this.driveSymbol = driveSymbol;
    }

    /**
     * Cleans terminated process from the disk
     */
    public void clean(int processIdentifier) {
        for (int i = 0; i < size(); i++) {
            if (get(i).getHandler().getId() == processIdentifier) {
                remove(i);
                Logger.log(this, "Cleared process [P-" + processIdentifier + "] from the disk drive because it is terminated.");
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "(" + driveSymbol + ":)";
    }
}
