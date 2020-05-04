package dev.maxc.os.components.scheduler.disciplines;

import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.structures.MutableQueue;
import dev.maxc.os.structures.Queue;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public abstract class SchedulingDiscipline {
    protected volatile Queue<ProcessControlBlock> jobQueue = new Queue<>();

    /**
     * Uses an algorithm to decide what order the pcbs
     * should be in when they enter the job queue.
     */
    public abstract void schedule(MutableQueue<ProcessControlBlock> pcbs);
}
