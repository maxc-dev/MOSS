package dev.maxc.os.components.scheduler;

import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.ProcessState;
import dev.maxc.os.structures.MutableQueue;
import dev.maxc.os.system.sync.ClockTick;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public class AdmissionScheduler implements ClockTick {
    private volatile MutableQueue<ProcessControlBlock> waitingQueue = new MutableQueue<>();
    private final CPUScheduler cpuScheduler;

    public AdmissionScheduler(CPUScheduler cpuScheduler) {
        this.cpuScheduler = cpuScheduler;
    }

    public synchronized void schedulePCB(ProcessControlBlock pcb) {
        pcb.setProcessState(ProcessState.WAITING);
        waitingQueue.add(pcb);
    }

    @Override
    public synchronized void onSystemClockTick() {
        cpuScheduler.addToJobQueue(waitingQueue);
    }
}
