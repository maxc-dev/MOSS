package dev.maxc.os.components.cpu;

import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.structures.Queue;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ControlUnit {
    private final ProcessorCore[] cores;

    private volatile Queue<ProcessControlBlock> jobQueue;

    public ControlUnit(int coreCount, Queue<ProcessControlBlock> jobQueue, MemoryManagementUnit mmu) {
        this.jobQueue = jobQueue;
        cores = new ProcessorCore[coreCount];
        for (int i = 0; i < coreCount; i++) {
            cores[i] = new ProcessorCore(i, this, mmu, 20);
        }
    }

    public ProcessControlBlock getJobQueuePCB() throws JobQueueEmpty {
        if (jobQueue.hasNext()) {
            return jobQueue.get();
        } else {
            throw new JobQueueEmpty();
        }
    }
}
