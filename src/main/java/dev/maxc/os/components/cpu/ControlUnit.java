package dev.maxc.os.components.cpu;

import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.ProcessState;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;
import dev.maxc.os.structures.Queue;
import dev.maxc.os.system.sync.ClockTick;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ControlUnit implements ClockTick {
    private final ProcessorCore[] cores;

    private volatile Queue<ProcessControlBlock> jobQueue;

    public ControlUnit(int coreCount, Queue<ProcessControlBlock> jobQueue, MemoryManagementUnit mmu) {
        this.jobQueue = jobQueue;
        cores = new ProcessorCore[coreCount];
        for (int i = 0; i < coreCount; i++) {
            cores[i] = new ProcessorCore(i, mmu);
        }
    }

    /**
     * Initialises the processor core threads at a given frequency.
     */
    public void initProcessorCoreThreads(int frequency) {
        for (ProcessorCore core : cores) {
            core.initCoreThread(frequency);
        }
    }

    /**
     * Writes a PCB to an available core's PCB socket.
     */
    public boolean writeCoreSocket(ProcessControlBlock pcb) {
        for (ProcessorCore core : cores) {
            if (core.isAvailable()) {
                if (core.writePCBSocket(pcb)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onSystemClockTick() {
        while (jobQueue.hasNext()) {
            ProcessControlBlock pcb = jobQueue.get();
            if (!writeCoreSocket(pcb)) {
                Logger.log(Status.CRIT, this, "Unable to write process to core socket, to prevent the process from being lost it has been appended back to the job queue.");
                pcb.setProcessState(ProcessState.WAITING);
                jobQueue.add(pcb);
            }
        }
    }
}
