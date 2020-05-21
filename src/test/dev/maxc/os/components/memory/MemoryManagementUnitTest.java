package dev.maxc.os.components.memory;

import dev.maxc.os.components.disk.DiskDrive;
import dev.maxc.os.components.instruction.Instruction;
import dev.maxc.os.components.instruction.Opcode;
import dev.maxc.os.components.instruction.Operand;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.memory.model.MemoryUnit;
import dev.maxc.os.components.memory.virtual.VirtualMemoryInterface;
import dev.maxc.os.io.exceptions.deadlock.AccessingLockedUnitException;
import dev.maxc.os.io.exceptions.deadlock.MutatingLockedUnitException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryManagementUnitTest {
    public LogicalMemoryHandlerUtils getTestUtils() {
        return new LogicalMemoryHandlerUtils(4, 2);
    }

    public RandomAccessMemory getTestRAM() {
        return new RandomAccessMemory(16, FirstFit.class);
    }

    public MemoryManagementUnit getTestMMU(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, boolean useSegmentation) {
        return new MemoryManagementUnit(ram, useSegmentation, utils, 5);
    }

    @Test
    public void testPaging() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, false);
        DiskDrive diskDrive = new DiskDrive('T');
        VirtualMemoryInterface vmi = new VirtualMemoryInterface(mmu, diskDrive);
        ram.initMemoryManagementUnit(mmu);
        ram.initVirtualMemoryInterface(vmi);
        mmu.initVirtualMemoryInterface(vmi);

        assertTrue(mmu.allocateMemory(0));
        assertEquals(utils.getInitialSize(), ram.getAllocatedMemory());
        assertTrue(mmu.allocateAdditionalMemory(0));
        assertEquals(utils.getInitialSize() * 2, ram.getAllocatedMemory());

        //allocating to a new process
        assertTrue(mmu.allocateMemory(1));
        assertEquals(utils.getInitialSize() * 3, ram.getAllocatedMemory());
        assertTrue(mmu.allocateAdditionalMemory(1));
        assertEquals(utils.getInitialSize() * 4, ram.getAllocatedMemory());

        assertEquals(64, ram.getAllocatedMemory());

        mmu.clearProcessMemory(0);
        assertEquals(utils.getInitialSize() * 2, ram.getAllocatedMemory());

        mmu.clearProcessMemory(1);
        assertEquals(0, ram.getAllocatedMemory());
    }

    @Test
    public void testSegmentation() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, true);
        DiskDrive diskDrive = new DiskDrive('T');
        VirtualMemoryInterface vmi = new VirtualMemoryInterface(mmu, diskDrive);
        ram.initMemoryManagementUnit(mmu);
        ram.initVirtualMemoryInterface(vmi);
        mmu.initVirtualMemoryInterface(vmi);

        //allocating to a new process
        assertTrue(mmu.allocateMemory(0));
        assertEquals(utils.getInitialSize(), ram.getAllocatedMemory());
        assertTrue(mmu.allocateAdditionalMemory(0));
        assertEquals(utils.getInitialSize() + utils.getIncrease(), ram.getAllocatedMemory());

        //allocating to a new process
        assertTrue(mmu.allocateMemory(1));
        assertEquals(utils.getInitialSize() * 2 + utils.getIncrease(), ram.getAllocatedMemory());
        assertTrue(mmu.allocateAdditionalMemory(1));
        assertEquals(utils.getInitialSize() * 2 + utils.getIncrease() * 2, ram.getAllocatedMemory());

        mmu.clearProcessMemory(0);
        assertEquals(utils.getInitialSize() + utils.getIncrease(), ram.getAllocatedMemory());

        mmu.clearProcessMemory(1);
        assertEquals(0, ram.getAllocatedMemory());
    }

    @Test
    public void testGetMemoryUnitContentPaging() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, false);
        DiskDrive diskDrive = new DiskDrive('T');
        VirtualMemoryInterface vmi = new VirtualMemoryInterface(mmu, diskDrive);
        ram.initMemoryManagementUnit(mmu);
        ram.initVirtualMemoryInterface(vmi);
        mmu.initVirtualMemoryInterface(vmi);

        mmu.allocateMemory(0);
        MemoryUnit unit = mmu.getMemoryUnit(0, 0);
        assertFalse(unit.isLocked());
        assertFalse(unit.isLockedToProcess(0));
        unit.lock(0);
        assertTrue(unit.isLocked());
        assertTrue(unit.isLockedToProcess(0));

        Instruction instruction = new Instruction(Opcode.ADD, new Operand(false, 3), new Operand(false, 2));

        try {
            unit.mutate(0, instruction);
        } catch (MutatingLockedUnitException e) {
            e.printStackTrace();
        }


        try {
            assertEquals(instruction, unit.access(0));
        } catch (AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        unit.unlock();
        assertFalse(unit.isLocked());

        assertThrows(AccessingLockedUnitException.class, () -> unit.access(0));
        assertThrows(MutatingLockedUnitException.class, () -> unit.mutate(0, instruction));
    }

    @Test
    public void testGetMemoryUnitContentSegmentation() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, true);
        DiskDrive diskDrive = new DiskDrive('T');
        VirtualMemoryInterface vmi = new VirtualMemoryInterface(mmu, diskDrive);
        ram.initMemoryManagementUnit(mmu);
        ram.initVirtualMemoryInterface(vmi);
        mmu.initVirtualMemoryInterface(vmi);

        mmu.allocateMemory(0);
        MemoryUnit unit = mmu.getMemoryUnit(0, 0);
        assertFalse(unit.isLocked());
        assertFalse(unit.isLockedToProcess(0));
        unit.lock(0);
        assertTrue(unit.isLocked());
        assertTrue(unit.isLockedToProcess(0));

        Instruction instruction = new Instruction(Opcode.ADD, new Operand(false, 1), new Operand(false, 2));

        try {
            unit.mutate(0, instruction);
        } catch (MutatingLockedUnitException e) {
            e.printStackTrace();
        }

        try {
            assertEquals(instruction, unit.access(0));
        } catch (AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        unit.unlock();
        assertFalse(unit.isLocked());

        assertThrows(AccessingLockedUnitException.class, () -> unit.access(0));
        assertThrows(MutatingLockedUnitException.class, () -> unit.mutate(0, instruction));
    }

    @Test
    public void testCache() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, true);
        DiskDrive diskDrive = new DiskDrive('T');
        VirtualMemoryInterface vmi = new VirtualMemoryInterface(mmu, diskDrive);
        ram.initMemoryManagementUnit(mmu);
        ram.initVirtualMemoryInterface(vmi);
        mmu.initVirtualMemoryInterface(vmi);

        mmu.allocateMemory(0);
        MemoryUnit unit1 = mmu.getMemoryUnit(0, 0);
        unit1.lock(0);
        Instruction instruction1 = new Instruction(Opcode.ADD, new Operand(false, 1), new Operand(false, 2));

        try {
            unit1.mutate(0, instruction1);
            assertEquals(instruction1, unit1.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        MemoryUnit unit2 = mmu.getMemoryUnit(0, 1);
        unit2.lock(0);
        Instruction instruction2 = new Instruction(Opcode.ADD, new Operand(false, 65), new Operand(false, 6));
        try {
            unit2.mutate(0, instruction2);
            assertEquals(instruction2, unit2.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        MemoryUnit unit3 = mmu.getMemoryUnit(0, 2);
        unit3.lock(0);
        Instruction instruction3 = new Instruction(Opcode.SUB, new Operand(false, 56), new Operand(false, 24));
        try {
            unit3.mutate(0, instruction3);
            assertEquals(instruction3, unit3.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        MemoryUnit unit4 = mmu.getMemoryUnit(0, 3);
        unit4.lock(0);
        Instruction instruction4 = new Instruction(Opcode.MUL, new Operand(false, 2), new Operand(false, 6));
        try {
            unit4.mutate(0, instruction4);
            assertEquals(instruction4, unit4.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }

        MemoryUnit unit5 = mmu.getMemoryUnit(0, 4);
        unit5.lock(0);
        Instruction instruction5 = new Instruction(Opcode.STR, new Operand(false, 4), new Operand(false, 2));
        try {
            unit5.mutate(0, instruction5);
            assertEquals(instruction5, unit5.access(0));
        } catch (MutatingLockedUnitException | AccessingLockedUnitException e) {
            e.printStackTrace();
        }
        unit5.unlock();

        unit5 = mmu.getMemoryUnit(0, 4);
        unit5.lock(0);
        Instruction instruction6 = new Instruction(Opcode.SUB, new Operand(false, 2), new Operand(false, 3));
        try {
            unit5.mutate(0, instruction6);
            assertEquals(instruction6, unit5.access(0));
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