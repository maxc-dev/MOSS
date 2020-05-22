package dev.maxc.os.components.memory.indexer;

import org.junit.jupiter.api.Test;

import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.model.AddressPointerSet;

import static org.junit.Assert.*;

public class FirstFitTest {
    public RandomAccessMemory getTestRam() {
        return new RandomAccessMemory(10, FirstFit.class);
    }

    @Test
    public void testIndexAddressSlot() {
        RandomAccessMemory ram = getTestRam();
        AddressPointerSet address = ram.indexMemory(40);
        assertEquals(0, address.getStartPointer());
        assertEquals(39, address.getEndPointer());
    }
}