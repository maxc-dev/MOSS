package dev.maxc.os.components.disk;

import dev.maxc.os.components.memory.virtual.VirtualMemoryDiskNode;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 20/05/2020
 */
public class DiskDrive extends ArrayList<VirtualMemoryDiskNode> {
    private final char driveSymbol;
    private final int capacity;

    public DiskDrive(char driveSymbol, int capacity) {
        super(capacity);
        this.driveSymbol = driveSymbol;
        this.capacity = capacity;
    }

    public void clean() {

    }

    @Override
    public String toString() {
        return "(" + driveSymbol + "-Drive {" + capacity + "b})";
    }
}
