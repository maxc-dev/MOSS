package dev.maxc.os.components.scheduler.disciplines;

import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.structures.MutableQueue;
import dev.maxc.os.structures.Queue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FirstInFirstOutTest {

    @Test
    public void testFirstInFirstOut() {
        Queue<ProcessControlBlock> readyQueue = new Queue<>();
        FirstInFirstOut fifo = new FirstInFirstOut(readyQueue);

        MutableQueue<ProcessControlBlock> pcbs = new MutableQueue<>();
        pcbs.add(new ProcessControlBlock(0, -1, null));
        pcbs.add(new ProcessControlBlock(1, -1, null));
        pcbs.add(new ProcessControlBlock(2, -1, null));
        pcbs.add(new ProcessControlBlock(3, -1, null));
        fifo.schedule(pcbs);

        assertEquals(0, readyQueue.get().getProcessID());
        assertEquals(1, readyQueue.get().getProcessID());
        assertEquals(2, readyQueue.get().getProcessID());
        assertEquals(3, readyQueue.get().getProcessID());
    }

}