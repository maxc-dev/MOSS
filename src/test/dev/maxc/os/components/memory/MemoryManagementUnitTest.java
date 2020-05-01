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
        return new MemoryManagementUnit(ram, useSegmentation, utils, 5);
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
        MemoryUnit unit = mmu.getMemoryUnit(0, 0);
        assertFalse(unit.isLocked());
        assertFalse(unit.isLockedToProcess(0));
        unit.lock(0);
        assertTrue(unit.isLocked());
        assertTrue(unit.isLockedToProcess(0));

        try {
            unit.mutate(0, 5647);
        } catch (MutatingLockedUnitException e) {
            e.printStackTrace();
        }


        try {
            assertEquals(5647, unit.access(0));
        } catch (AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        unit.unlock();
        assertFalse(unit.isLocked());

        assertThrows(AccessingLockedUnitException.class, () -> unit.access(0));
        assertThrows(MutatingLockedUnitException.class, () -> unit.mutate(0, 45));
    }

    @Test
    public void testGetMemoryUnitContentSegmentation() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, true);

        mmu.allocateMemory(0);
        MemoryUnit unit = mmu.getMemoryUnit(0, 0);
        assertFalse(unit.isLocked());
        assertFalse(unit.isLockedToProcess(0));
        unit.lock(0);
        assertTrue(unit.isLocked());
        assertTrue(unit.isLockedToProcess(0));

        try {
            unit.mutate(0, 5647);
        } catch (MutatingLockedUnitException e) {
            e.printStackTrace();
        }

        try {
            assertEquals(5647, unit.access(0));
        } catch (AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        unit.unlock();
        assertFalse(unit.isLocked());

        assertThrows(AccessingLockedUnitException.class, () -> unit.access(0));
        assertThrows(MutatingLockedUnitException.class, () -> unit.mutate(0, 45));
    }

    @Test
    public void testCache() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, true);

        mmu.allocateMemory(0);
        MemoryUnit unit1 = mmu.getMemoryUnit(0, 0);
        unit1.lock(0);
        try {
            unit1.mutate(0, 78);
            assertEquals(78, unit1.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        MemoryUnit unit2 = mmu.getMemoryUnit(0, 1);
        unit2.lock(0);
        try {
            unit2.mutate(0, 32);
            assertEquals(32, unit2.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        MemoryUnit unit3 = mmu.getMemoryUnit(0, 2);
        unit3.lock(0);
        try {
            unit3.mutate(0, 799);
            assertEquals(799, unit3.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        MemoryUnit unit4 = mmu.getMemoryUnit(0, 3);
        unit4.lock(0);
        try {
            unit4.mutate(0, 453);
            assertEquals(453, unit4.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        MemoryUnit unit5 = mmu.getMemoryUnit(0, 4);
        unit5.lock(0);
        try {
            unit5.mutate(0, 56465);
            assertEquals(56465, unit5.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }
        unit5.unlock();

        unit5 = mmu.getMemoryUnit(0, 4);
        unit5.lock(0);
        try {
            unit5.mutate(0, 56465);
            assertEquals(56465, unit5.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        unit1.unlock();
        unit2.unlock();
        unit3.unlock();
        unit4.unlock();
        unit5.unlock();
    }
}