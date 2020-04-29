package dev.maxc.os.components.memory;

import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.io.exceptions.deadlock.AccessingLockedUnitException;
import dev.maxc.os.io.exceptions.deadlock.MutatingLockedUnitException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryManagementUnitTest {
    public LogicalMemoryHandlerUtils getTestUtils() {
        return new LogicalMemoryHandlerUtils(2, 4, 2);
    }

    public RandomAccessMemory getTestRAM() {
        return new RandomAccessMemory(2, 16, FirstFit.class, true);
    }

    public MemoryManagementUnit getTestMMU(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, boolean useSegmentation) {
        return new MemoryManagementUnit(ram, useSegmentation, utils);
    }

    @Test
    public void testPaging() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, false);

        assertTrue(mmu.allocateMemory(0));
        assertEquals(utils.getInitialSize(), ram.getUsedMemory());
        assertTrue(mmu.allocateAdditionalMemory(0));
        assertEquals(utils.getInitialSize()*2, ram.getUsedMemory());

        //allocating to a new process
        assertTrue(mmu.allocateMemory(1));
        assertEquals(utils.getInitialSize()*3, ram.getUsedMemory());
        assertTrue(mmu.allocateAdditionalMemory(1));
        assertEquals(utils.getInitialSize()*4, ram.getUsedMemory());

        assertEquals(64, ram.getUsedMemory());

        mmu.clearProcessMemory(0);
        assertEquals(utils.getInitialSize()*2, ram.getUsedMemory());

        mmu.clearProcessMemory(1);
        assertEquals(0, ram.getUsedMemory());
    }

    @Test
    public void testSegmentation() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, true);

        //allocating to a new process
        assertTrue(mmu.allocateMemory(0));
        assertEquals(utils.getInitialSize(), ram.getUsedMemory());
        assertTrue(mmu.allocateAdditionalMemory(0));
        assertEquals(utils.getInitialSize() + utils.getIncrease(), ram.getUsedMemory());

        //allocating to a new process
        assertTrue(mmu.allocateMemory(1));
        assertEquals(utils.getInitialSize()*2 + utils.getIncrease(), ram.getUsedMemory());
        assertTrue(mmu.allocateAdditionalMemory(1));
        assertEquals(utils.getInitialSize()*2 + utils.getIncrease()*2, ram.getUsedMemory());

        mmu.clearProcessMemory(0);
        assertEquals(utils.getInitialSize() + utils.getIncrease(), ram.getUsedMemory());

        mmu.clearProcessMemory(1);
        assertEquals(0, ram.getUsedMemory());
    }

    @Test
    public void testGetMemoryUnitContentPaging() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, false);

        mmu.allocateMemory(0);
        MemoryUnit unit = mmu.getMemoryUnitFromProcess(0, 0);
        assertFalse(unit.isLocked());

        try {
            unit.mutate(5647);
        } catch (MutatingLockedUnitException e) {
            e.printStackTrace();
        }

        try {
            assertEquals(5647, unit.access());
        } catch (AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        unit.lock();
        assertTrue(unit.isLocked());

        assertThrows(AccessingLockedUnitException.class, unit::access);
        assertThrows(MutatingLockedUnitException.class, () -> unit.mutate(45));
    }

    @Test
    public void testGetMemoryUnitContentSegmentation() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, true);

        mmu.allocateMemory(0);
        MemoryUnit unit = mmu.getMemoryUnitFromProcess(0, 0);
        assertFalse(unit.isLocked());

        try {
            unit.mutate(5647);
        } catch (MutatingLockedUnitException e) {
            e.printStackTrace();
        }

        try {
            assertEquals(5647, unit.access());
        } catch (AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        unit.lock();
        assertTrue(unit.isLocked());

        assertThrows(AccessingLockedUnitException.class, unit::access);
        assertThrows(MutatingLockedUnitException.class, () -> unit.mutate(45));
    }
}