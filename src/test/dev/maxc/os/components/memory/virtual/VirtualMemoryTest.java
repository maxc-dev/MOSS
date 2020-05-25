package dev.maxc.os.components.memory.virtual;

import dev.maxc.os.components.disk.DiskDrive;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.memory.allocation.Paging;
import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.memory.model.AddressPointerSet;
import dev.maxc.os.io.exceptions.disk.MemoryHandlerNotFoundException;
import dev.maxc.os.system.api.SystemAPITest;
import dev.maxc.os.system.api.SystemSetup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VirtualMemoryTest {

    /**
     * The following test shows how the OS will use the virtual
     * memory in a real-case scenario.
     */
    @Test
    public void testMemorySwapping() {
        SystemSetup setup = new SystemSetup(new LogicalMemoryHandlerUtils(5, 2));
        setup.clearProcesses = false;
        setup.mainMemoryPower = 5; //32 bit memory
        SystemAPITest api = new SystemAPITest(setup);

        /*
            Each sample process file will use 16bits of memory each
            so executing these will use up 16*2=32 bits of memory
            which is how much simulated memory there is in the test case.
         */
        api.compilerAPI.compile("sample");
        api.compilerAPI.compile("sample");

        //delay required since it takes time to get processed in the system
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //ram is now full
            assertEquals(32, api.ram.getAllocatedMemory());
            assertTrue(api.ram.isFull());
        }).start();

        /*
            The same process files will be executed again, this time
            the RAM is full so it has to swap a process into virtual
            memory to make room for a new process to execute in the RAM.
         */
        api.compilerAPI.compile("sample");
        api.compilerAPI.compile("sample");

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //ram is now full
            assertEquals(32, api.ram.getAllocatedMemory());
            assertTrue(api.ram.isFull());
            //signifies two processes being moved into the disk (vm)
            assertEquals(2, api.diskDrive.size());
        }).start();
    }

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

        /*
            creates dummy processes, and pushes them to the disk
         */
        VirtualMemoryDiskNode diskNode1 = new VirtualMemoryDiskNode(new Paging(null, null, 0, -1));
        VirtualMemoryDiskNode diskNode2 = new VirtualMemoryDiskNode(new Paging(null, null, 1, -1));
        vmi.pushToDisk(diskNode1);
        vmi.pushToDisk(diskNode2);

        /*
            test shows that the items are successfully pushed to the disk
            and the handler/instructions match up
         */
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

        /*
            creates dummy processes, and pushes them to the disk
         */
        Paging page1 = new Paging(ram, utils, 0, -1);
        page1.allocate(new AddressPointerSet(0, 15));
        Paging page2 = new Paging(ram, utils, 1, -1);
        page2.allocate(new AddressPointerSet(16, 31));

        VirtualMemoryDiskNode diskNode1 = new VirtualMemoryDiskNode(page1);
        VirtualMemoryDiskNode diskNode2 = new VirtualMemoryDiskNode(page2);
        vmi.pushToDisk(diskNode1);
        vmi.pushToDisk(diskNode2);

        //shows ram is empty
        assertEquals(0, ram.getAllocatedMemory());

        //pulls first process from VM which takes up the size of one process
        try {
            vmi.pullFromDisk(0);
        } catch (MemoryHandlerNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(utils.getInitialSize(), ram.getAllocatedMemory());

        //pulls second process from VM which takes up the size of two processes
        try {
            vmi.pullFromDisk(1);
        } catch (MemoryHandlerNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(utils.getInitialSize() * 2, ram.getAllocatedMemory());
    }
}