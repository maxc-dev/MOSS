package dev.maxc.os.components.process;

import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.process.thread.ThreadAPI;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ProcessAPI {
    public static final int NO_PARENT_PROCESS = -1;

    private final AtomicInteger processCount = new AtomicInteger();
    private final ThreadAPI threadAPI;
    private final MemoryManagementUnit mmu;

    public ProcessAPI(ThreadAPI threadAPI, MemoryManagementUnit mmu) {
        this.threadAPI = threadAPI;
        this.mmu = mmu;
    }

    /**
     * Creates a new Process with an associated Process Control Block.
     * Also creates a new main thread to add with the process.
     */
    public Process getNewProcess(int parentProcessIdentifier) {
        Process process = new Process(new ProcessControlBlock(processCount.addAndGet(1), parentProcessIdentifier, this), this);
        threadAPI.addNewThreadToProcess(process);
        mmu.allocateMemory(process.getProcessControlBlock().getProcessID());
        return process;
    }

    /**
     * Exits the process permanently.
     */
    public void exitProcess(int processIdentifier) {
        mmu.clearProcessMemory(processIdentifier);
    }
}
