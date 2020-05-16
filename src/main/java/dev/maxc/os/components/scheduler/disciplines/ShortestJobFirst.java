package dev.maxc.os.components.scheduler.disciplines;

import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.ProcessState;
import dev.maxc.os.structures.MutableQueue;
import dev.maxc.os.structures.Queue;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public class ShortestJobFirst extends SchedulingDiscipline {
    public ShortestJobFirst(Queue<ProcessControlBlock> jobQueue) {
        super(jobQueue);
    }

    @Override
    public synchronized void schedule(MutableQueue<ProcessControlBlock> pcbs) {
        ArrayList<ProcessControlBlock> processes = new ArrayList<>();
        while (pcbs.hasNext()) {
            processes.add(pcbs.get());
        }
        while (processes.size() > 0) {
            ProcessControlBlock shortestJob = null;
            for (ProcessControlBlock pcb : processes) {
                if (shortestJob == null) {
                    shortestJob = pcb;
                } else if (shortestJob.getCPUBursts() > pcb.getCPUBursts()) {
                    shortestJob = pcb;
                }
            }
            processes.remove(shortestJob);
            if (shortestJob != null) {
                shortestJob.setProcessState(ProcessState.READY);
                readyQueue.add(shortestJob);
            }
        }
    }
}
