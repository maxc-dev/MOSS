package dev.maxc.os.system;

import java.util.concurrent.atomic.AtomicInteger;

import dev.maxc.os.components.virtual.Process;
import dev.maxc.os.components.virtual.ProcessControlBlock;
import dev.maxc.os.components.virtual.ProcessState;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ProcessAPI {
    public static final int NO_PARENT_PROCESS = -1;

    private final AtomicInteger processCount = new AtomicInteger();
    private final ThreadAPI threadAPI;

    public ProcessAPI(ThreadAPI threadAPI) {
        this.threadAPI = threadAPI;
    }

    /**
     * Creates a new Process with an associated Process Control Block.
     * Also creates a new main thread to add with the process.
     */
    public Process getNewProcess(int parentProcessIdentifier) {
        Process process = new Process(new ProcessControlBlock(processCount.addAndGet(1), parentProcessIdentifier));
        threadAPI.addNewThreadToProcess(process);
        return process;
    }

    /**
     * Exits the process permanently.
     */
    public void exitProcess(Process process) {
        process.getProcessControlBlock().setProcessState(ProcessState.TERMINATED);
    }
}
