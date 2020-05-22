package dev.maxc.os.components.memory.virtual;

import dev.maxc.os.components.disk.DiskDrive;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.memory.allocation.Paging;
import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.io.exceptions.disk.MemoryHandlerNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

class VirtualMemoryTest {
    public LogicalMemoryHandlerUtils getTestUtils() {
        return new LogicalMemoryHandlerUtils(4, 4);
    }

    public RandomAccessMemory getTestRAM() {
        return new RandomAccessMemory(12, FirstFit.class);
    }

    public MemoryManagementUnit getTestMMU(RandomAccessMemory ram, LogicalMemoryHandlerUtils utils, boolean useSegmentation) {
        return new MemoryManagementUnit(ram, useSegmentation, utils, 5);
    }

    @Test
    public void testPushToDisk() {
        DiskDrive diskDrive = new DiskDrive('T');
        VirtualMemoryInterface vmi = new VirtualMemoryInterface(null, diskDrive);

        VirtualMemoryDiskNode diskNode1 = new VirtualMemoryDiskNode(new Paging(null, null, 0, -1));
        VirtualMemoryDiskNode diskNode2 = new VirtualMemoryDiskNode(new Paging(null, null, 1, -1));
        vmi.pushToDisk(diskNode1);
        vmi.pushToDisk(diskNode2);

        assertEquals(diskNode1.getHandler(), diskDrive.get(0).getHandler());
        assertEquals(diskNode1.getInstructions(), diskDrive.get(0).getInstructions());
        assertEquals(diskNode2.getHandler(), diskDrive.get(1).getHandler());
        assertEquals(diskNode2.getInstructions(), diskDrive.get(1).getInstructions());
    }

    @Test
    public void testPullFromDisk() {
        RandomAccessMemory ram = getTestRAM();
        LogicalMemoryHandlerUtils utils = getTestUtils();
        MemoryManagementUnit mmu = getTestMMU(ram, utils, false);
        DiskDrive diskDrive = new DiskDrive('T');
        VirtualMemoryInterface vmi = new VirtualMemoryInterface(mmu, diskDrive);
        ram.initMemoryManagementUnit(mmu);
        ram.initVirtualMemoryInterface(vmi);
        mmu.initVirtualMemoryInterface(vmi);

        Paging page1 = new Paging(ram, utils, 0, -1);
        page1.allocate(new AddressPointerSet(0, 15));
        Paging page2 = new Paging(ram, utils, 1, -1);
        page2.allocate(new AddressPointerSet(16, 31));

        VirtualMemoryDiskNode diskNode1 = new VirtualMemoryDiskNode(page1);
        VirtualMemoryDiskNode diskNode2 = new VirtualMemoryDiskNode(page2);
        vmi.pushToDisk(diskNode1);
        vmi.pushToDisk(diskNode2);

        assertEquals(0, ram.getAllocatedMemory());

        try {
            vmi.pullFromDisk(0);
        } catch (MemoryHandlerNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(utils.getInitialSize(), ram.getAllocatedMemory());

        try {
            vmi.pullFromDisk(1);
        } catch (MemoryHandlerNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(utils.getInitialSize() * 2, ram.getAllocatedMemory());
    }

}