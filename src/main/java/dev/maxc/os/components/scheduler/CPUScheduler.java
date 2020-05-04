package dev.maxc.os.components.scheduler;

import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.scheduler.disciplines.FirstInFirstOut;
import dev.maxc.os.components.scheduler.disciplines.SchedulingDiscipline;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;
import dev.maxc.os.structures.MutableQueue;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public class CPUScheduler {
    private SchedulingDiscipline scheduler;

    public <T extends SchedulingDiscipline> CPUScheduler(Class<T> schedulerClass) {
        try {
            this.scheduler = schedulerClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            Logger.log(Status.ERROR, this, "Failed to initialise a new scheduling discipline, defaulting to FIFO.");
            this.scheduler = new FirstInFirstOut();
            ex.printStackTrace();
        }
    }

    public void addToJobQueue(MutableQueue<ProcessControlBlock> pcbs) {
        scheduler.schedule(pcbs);
    }
}
