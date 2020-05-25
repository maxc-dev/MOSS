package dev.maxc.os.components.cpu;

import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.ProcessState;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;
import dev.maxc.os.structures.Queue;
import dev.maxc.os.system.sync.HardwareClockTick;
import dev.maxc.ui.anchors.TaskManagerController;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ControlUnit implements HardwareClockTick {
    private final ProcessorCore[] cores;
    private int lastCoreIndexed = 0;
    private final Queue<ProcessControlBlock> readyQueue;

    public ControlUnit(int coreCount, Queue<ProcessControlBlock> readyQueue, MemoryManagementUnit mmu) {
        this.readyQueue = readyQueue;
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
    public synchronized boolean writeCoreSocket(ProcessControlBlock pcb) {
        for (int i = 0; i < cores.length; i++) {
            if (lastCoreIndexed >= cores.length) {
                lastCoreIndexed = 0;
            }
            if (cores[lastCoreIndexed].isAvailable()) {
                if (cores[lastCoreIndexed].writePCBSocket(pcb)) {
                    lastCoreIndexed++;
                    return true;
                }
                return false;
            }
            lastCoreIndexed++;
        }
        return false;
    }

    @Override
    public synchronized void onClockTick() {
        //if the ready queue is not empty, it will find a core to execute the next job.
        while (readyQueue.hasNext()) {
            ProcessControlBlock pcb = readyQueue.get();
            if (!writeCoreSocket(pcb)) {
                /*
                    When the control unit is unable to write to the socket, it is usually due to
                    an issue of concurrency. What normally happens is the core socket might be empty,
                    and just as the control unit goes to write the PCB to the socket, the core is written
                    to in another thread. Since it cannot be overwritten, the PCB is rejected and has
                    to be appended back into the queue. However due to concurrency and the structure of
                    a queue, it cannot be added back to the start, it must be written to from the back
                    which means the PCB has to be reprocessed. Whilst this is inconvenient, the delay
                    is insignificant and the affect it has on the rest of the system is negligible.
                 */
                Logger.log(Status.WARN, this, "Unable to write process to core socket due to concurrency, to prevent the process from being lost it has been appended to the back of the job queue.");
                pcb.setProcessState(ProcessState.WAITING);
                readyQueue.add(pcb);
            }
        }
    }

    public ProcessorCore[] getCores() {
        return cores;
    }

    public void setTaskManagerOutput(TaskManagerController taskManagerOutput) {
        for (ProcessorCore core : cores) {
            core.setTaskManager(taskManagerOutput);
        }
    }
}
