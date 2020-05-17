package dev.maxc.os.components.memory.indexer;

import org.junit.jupiter.api.Test;

import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.model.AddressPointerSet;

import static org.junit.Assert.*;

public class FirstFitTest {
    public RandomAccessMemory getTestRam() {
        return new RandomAccessMemory(2, 10, FirstFit.class);
    }

    @Test
    public void testIndexAddressSlot() {
        RandomAccessMemory ram = getTestRam();
        AddressPointerSet address = ram.indexMemory(40);
        assertEquals(0, address.getStartPointer());
        assertEquals(39, address.getEndPointer());

        assertTrue(ram.get(39).getMemoryUnit().isAllocated());

        AddressPointerSet address1 = ram.indexMemory(80);
        assertEquals(40, address1.getStartPointer());
        assertEquals(119, address1.getEndPointer());

        AddressPointerSet address2 = ram.indexMemory(800);
        assertEquals(120, address2.getStartPointer());
        assertEquals(919, address2.getEndPointer());

        AddressPointerSet address3 = ram.indexMemory(103);
        assertEquals(920, address3.getStartPointer());
        assertEquals(1022, address3.getEndPointer());
    }

    @Test
    public void testIndexAddressSlot2() {
        RandomAccessMemory ram = getTestRam();
        AddressPointerSet address = ram.indexMemory(1023);
        assertEquals(0, address.getStartPointer());
        assertEquals(1022, address.getEndPointer());

        ram.indexMemory(1);
        assertTrue(ram.isFull());
        assertEquals(1024, ram.getAllocatedMemory());
    }

    @Test
    public void testIndexAddressSlotMemoryBreach() {
        RandomAccessMemory ram = getTestRam();
        AddressPointerSet address3 = ram.indexMemory(1024);
        assertEquals(0, address3.getStartPointer());
        assertEquals(1023, address3.getEndPointer());

        //should init an out of memory error
        assertThrows(OutOfMemoryError.class, () -> ram.indexMemory(1));
    }

}