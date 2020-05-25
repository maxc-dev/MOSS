package dev.maxc.os.system.api;

import dev.maxc.os.components.compiler.CompilerAPI;
import dev.maxc.os.components.cpu.ControlUnit;
import dev.maxc.os.components.disk.DiskDrive;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.memory.virtual.VirtualMemoryInterface;
import dev.maxc.os.components.process.ProcessAPI;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.process.thread.ThreadAPI;
import dev.maxc.os.components.scheduler.AdmissionScheduler;
import dev.maxc.os.components.scheduler.CPUScheduler;
import dev.maxc.os.components.scheduler.disciplines.FirstInFirstOut;
import dev.maxc.os.components.scheduler.disciplines.ShortestJobFirst;
import dev.maxc.os.structures.Queue;
import dev.maxc.os.system.sync.HardwareClockTickEmitter;

public class SystemAPITest {
    private final Queue<ProcessControlBlock> readyQueue = new Queue<>();
    public volatile AdmissionScheduler longTermScheduler;
    public RandomAccessMemory ram;
    public MemoryManagementUnit memoryAPI;
    public ThreadAPI threadAPI;
    public ProcessAPI processAPI;
    public HardwareClockTickEmitter hardwareClockTickEmitter;
    public CompilerAPI compilerAPI;
    public DiskDrive diskDrive;
    public ControlUnit controlUnit;
    public CPUScheduler shortTermScheduler;
    public VirtualMemoryInterface virtualMemoryAPI;

    public SystemAPITest(SystemSetup systemSetup) {
        diskDrive = new DiskDrive('T');

        ram = new RandomAccessMemory(systemSetup.mainMemoryPower, FirstFit.class);
        memoryAPI = new MemoryManagementUnit(ram, systemSetup.useSegmentation, systemSetup.utils, systemSetup.cacheSize);

        if (systemSetup.useVirtualMemory) {
            virtualMemoryAPI = new VirtualMemoryInterface(memoryAPI, diskDrive);
            memoryAPI.initVirtualMemoryInterface(virtualMemoryAPI);
            ram.initVirtualMemoryInterface(virtualMemoryAPI);
            ram.initMemoryManagementUnit(memoryAPI);
        }

        controlUnit = new ControlUnit(systemSetup.cpuCores, readyQueue, memoryAPI);
        controlUnit.initProcessorCoreThreads(systemSetup.coreFrequency);
        if (systemSetup.useFIFO) {
            shortTermScheduler = new CPUScheduler(FirstInFirstOut.class, readyQueue);
        } else {
            shortTermScheduler = new CPUScheduler(ShortestJobFirst.class, readyQueue);
        }
        longTermScheduler = new AdmissionScheduler(shortTermScheduler);

        hardwareClockTickEmitter = new HardwareClockTickEmitter(systemSetup.clockTickFrequency);
        hardwareClockTickEmitter.addClockTickListener(longTermScheduler);
        hardwareClockTickEmitter.addClockTickListener(controlUnit);

        threadAPI = new ThreadAPI();
        processAPI = new ProcessAPI(threadAPI, memoryAPI, systemSetup.clearProcesses);

        compilerAPI = new CompilerAPI(longTermScheduler, memoryAPI, processAPI);
    }

}