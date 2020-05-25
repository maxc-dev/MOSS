package dev.maxc.os.components.memory.indexer;

import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.io.exceptions.memory.InvalidIndexSizeError;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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

    @Test
    public void testInvalidNegativeIndexAddressSlot() {
        RandomAccessMemory ram = getTestRam();
        assertThrows(InvalidIndexSizeError.class, () -> ram.indexMemory(-1));
    }

    @Test
    public void testInvalidZeroIndexAddressSlot() {
        RandomAccessMemory ram = getTestRam();
        assertThrows(InvalidIndexSizeError.class, () -> ram.indexMemory(0));
    }

    @Test
    public void testInvalidExceedIndexAddressSlot() {
        RandomAccessMemory ram = getTestRam();
        assertThrows(InvalidIndexSizeError.class, () -> ram.indexMemory(99999999));
    }
}