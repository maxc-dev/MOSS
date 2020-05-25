package dev.maxc.os.components.scheduler.disciplines;

import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.structures.MutableQueue;
import dev.maxc.os.structures.Queue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShortestJobFirstTest {

    @Test
    public void testShortestJobFirst() {
        Queue<ProcessControlBlock> readyQueue = new Queue<>();
        ShortestJobFirst sjf = new ShortestJobFirst(readyQueue);

        MutableQueue<ProcessControlBlock> pcbs = new MutableQueue<>();
        pcbs.add(getPCBWithPCSize(0, 1000));
        pcbs.add(getPCBWithPCSize(1, 10));
        pcbs.add(getPCBWithPCSize(2, 100));
        pcbs.add(getPCBWithPCSize(3, 1));
        sjf.schedule(pcbs);

        assertEquals(3, readyQueue.get().getProcessID());
        assertEquals(1, readyQueue.get().getProcessID());
        assertEquals(2, readyQueue.get().getProcessID());
        assertEquals(0, readyQueue.get().getProcessID());
    }

    public ProcessControlBlock getPCBWithPCSize(int index, int size) {
        ProcessControlBlock pcb = new ProcessControlBlock(index, -1, null);
        for (int i = 0; i < size; i++) {
            pcb.getProgramCounter().add(i);
        }
        return pcb;
    }

}