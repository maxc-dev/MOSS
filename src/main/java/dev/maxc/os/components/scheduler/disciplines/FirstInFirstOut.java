package dev.maxc.os.components.scheduler.disciplines;

import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.ProcessState;
import dev.maxc.os.structures.MutableQueue;
import dev.maxc.os.structures.Queue;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public class FirstInFirstOut extends SchedulingDiscipline {
    public FirstInFirstOut(Queue<ProcessControlBlock> readyQueue) {
        super(readyQueue);
    }

    @Override
    public synchronized void schedule(MutableQueue<ProcessControlBlock> pcbs) {
        //loop the queue and add each node to the job queue
        while (pcbs.hasNext()) {
            ProcessControlBlock pcb = pcbs.get();
            pcb.setProcessState(ProcessState.READY);
            readyQueue.add(pcb);
        }
    }
}
