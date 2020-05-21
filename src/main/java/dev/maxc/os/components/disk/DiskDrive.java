package dev.maxc.os.components.disk;

import dev.maxc.os.components.memory.virtual.VirtualMemoryDiskNode;

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

    public void clean() {
/*        for (VirtualMemoryDiskNode node : this) {
            if (node.getHandler().getId())
        }*/
    }

    @Override
    public String toString() {
        return "(" + driveSymbol + ":)";
    }
}
