package dev.maxc.os.components.scheduler;

import dev.maxc.os.components.scheduler.disciplines.FirstInFirstOut;
import dev.maxc.os.components.scheduler.disciplines.SchedulingDiscipline;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.ProcessState;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;
import dev.maxc.os.structures.MutableQueue;
import dev.maxc.os.system.sync.ClockTick;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public class AdmissionScheduler implements ClockTick {
    private final MutableQueue<ProcessControlBlock> waitingQueue = new MutableQueue<>();
    private final CPUScheduler cpuScheduler;
    private SchedulingDiscipline scheduler;

    public <T extends SchedulingDiscipline> AdmissionScheduler(CPUScheduler cpuScheduler, Class<T> schedulerClass) {
        this.cpuScheduler = cpuScheduler;
        try {
            this.scheduler = schedulerClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            Logger.log(Status.ERROR, this, "Failed to initialise a new scheduling discipline, defaulting to FIFO.");
            this.scheduler = new FirstInFirstOut();
            ex.printStackTrace();
        }
    }

    public synchronized void schedulePCB(ProcessControlBlock pcb) {
        pcb.setProcessState(ProcessState.WAITING);
        waitingQueue.add(pcb);
    }

    @Override
    public void onSystemClockTick() {
        //send the PCB to the STS
    }
}
