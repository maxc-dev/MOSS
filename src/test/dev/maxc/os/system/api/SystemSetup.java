package dev.maxc.os.system.api;

import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;

/**
 * @author Max Carter
 * @since 23/05/2020
 */
public class SystemSetup {
    public int mainMemoryPower = 12;
    public int cacheSize = 12;
    public int cpuCores = 4;
    public int coreFrequency = 1000;
    public int clockTickFrequency = 1000;

    public boolean useSegmentation = true;
    public boolean useVirtualMemory = true;
    public boolean useFIFO = true;
    public boolean clearProcesses = true;

    public LogicalMemoryHandlerUtils utils;

    public SystemSetup(LogicalMemoryHandlerUtils utils) {
        this.utils = utils;
    }
}
