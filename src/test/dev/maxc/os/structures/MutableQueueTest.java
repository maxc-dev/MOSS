package dev.maxc.os.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MutableQueueTest {
    @Test
    public void testQueue() {
        MutableQueue<Integer> queue = new MutableQueue<>();
        for (int i = 0; i < 10; i++) {
            queue.add(i);
        }
        assertEquals(3, queue.get(3));
        assertEquals(4, queue.get(3));
    }

}