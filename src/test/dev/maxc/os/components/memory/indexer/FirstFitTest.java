package dev.maxc.os.components.memory.indexer;

import org.junit.jupiter.api.Test;

import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.model.GroupedMemoryAddress;

import static org.junit.Assert.*;

public class FirstFitTest {
    public RandomAccessMemory getTestRam() {
        return new RandomAccessMemory(2, 10, FirstFit.class, false);
    }

    @Test
    public void testIndexAddressSlot() {
        RandomAccessMemory ram = getTestRam();
        GroupedMemoryAddress address = ram.allocateMemory(40);
        assertEquals(0, address.getStartPointer());
        assertEquals(39, address.getEndPointer());

        assertTrue(ram.get(39).getMemoryUnit().isActive());

        GroupedMemoryAddress address1 = ram.allocateMemory(80);
        assertEquals(40, address1.getStartPointer());
        assertEquals(119, address1.getEndPointer());

        GroupedMemoryAddress address2 = ram.allocateMemory(800);
        assertEquals(120, address2.getStartPointer());
        assertEquals(919, address2.getEndPointer());

        GroupedMemoryAddress address3 = ram.allocateMemory(103);
        assertEquals(920, address3.getStartPointer());
        assertEquals(1022, address3.getEndPointer());
    }

    @Test
    public void testIndexAddressSlot2() {
        RandomAccessMemory ram = getTestRam();
        GroupedMemoryAddress address = ram.allocateMemory(1023);
        assertEquals(0, address.getStartPointer());
        assertEquals(1022, address.getEndPointer());

        ram.allocateMemory(1);
        assertTrue(ram.isFull());
        assertEquals(1024, ram.getUsedMemory());
    }

    @Test
    public void testIndexAddressSlotMemoryBreach() {
        RandomAccessMemory ram = getTestRam();
        GroupedMemoryAddress address3 = ram.allocateMemory(1024);
        assertEquals(0, address3.getStartPointer());
        assertEquals(1023, address3.getEndPointer());

        //should init an out of memory error
        assertThrows(OutOfMemoryError.class, () -> ram.allocateMemory(1));
    }

}