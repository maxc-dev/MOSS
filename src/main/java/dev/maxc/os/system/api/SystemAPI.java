/*
    This class provides an API between the Operating System Kernel
    and any application programs that want to access system config
    options.
 */

package dev.maxc.os.system.api;

import dev.maxc.os.bootup.DynamicComponentLoader;
import dev.maxc.os.bootup.config.Configurable;
import dev.maxc.os.components.compiler.CompilerAPI;
import dev.maxc.os.components.cpu.ControlUnit;
import dev.maxc.os.components.cpu.ProcessorCore;
import dev.maxc.os.components.disk.DiskDrive;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
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
import dev.maxc.os.system.sync.SystemClockPulse;
import dev.maxc.ui.anchors.TaskManagerController;
import dev.maxc.ui.api.UserInterfaceAPI;

/**
 * @author Max Carter
 * @since 10/04/2020
 */
public class SystemAPI {
    //system constants
    public static final String SYSTEM_NAME = "MOSS";
    public static final String SYSTEM_AUTHOR = "Max Carter";

    private final DynamicComponentLoader componentLoader;

    public SystemAPI(DynamicComponentLoader componentLoader) {
        this.componentLoader = componentLoader;
    }

    //system config
    @Configurable(docs = "The amount of time between each CPU execution in ms.", min = 1, max = 1000, recommended = 1000)
    public int CLOCK_TICK_FREQUENCY;

    //cpu config
    @Configurable(docs = "The amount of cores in the CPU.", min = 1, max = 256, recommended = 4)
    public int CPU_CORES;
    @Configurable(value = "cpu_core_frequency", docs = "The maximum amount of processes a CPU core can handle per second.", min = 1, max = 1000, recommended = 1000)
    public int CORE_FREQUENCY;

    //scheduling config
    @Configurable(value = "use_first_in_first_out", docs = "The scheduler will schedule processes on a first come first serve basis.")
    public boolean USE_FIFO;
    @Configurable(value = "use_shortest_job_first", docs = "The scheduler will schedule processes first if they have the shortest CPU time.")
    public boolean USE_SJF;

    //memory config
    @Configurable(docs = "The amount of memory locations available.", min = 2, max = 24, recommended = 12)
    public int MAIN_MEMORY_POWER;
    @Configurable(value = "malloc_algorithm_use_segmentation", docs = "If the MMU uses segmentation to allocate memory.")
    public boolean USE_SEGMENTATION;
    @Configurable(value = "malloc_algorithm_use_paging", docs = "If the MMU uses paging to allocate memory.")
    public boolean USE_PAGING;

    //virtual memory config
    @Configurable(docs = "Allows memory to be swapped into the disk drive temporarily to give space to other processes in main memory.")
    public boolean VIRTUAL_MEMORY;
    @Configurable(docs = "Processes which are terminated will have their memory automatically cleared. Recommend as true although if virtual memory & disk cleaner is enabled it is safe as false.")
    public boolean CLEAR_TERMINATED_PROCESS_MEMORY;

    //paging config
    @Configurable(value = "malloc_size_power", docs = "The power of allocation system.", min = 2, max = 12, recommended = 4)
    public int ALLOCATION_POWER;

    //segmentation config
    @Configurable(docs = "The power by which the segments will increase by (Base^x).", min = 1, max = 8, recommended = 4)
    public int SEGMENT_INCREASE_POWER;

    //cache config
    @Configurable(value = "cache_size_level_1", docs = "The amount of memory that the cache can store.", min = 0, max = 1024, recommended = 24)
    public int CACHE_SIZE;

    public static UserInterfaceAPI uiAPI = new UserInterfaceAPI();
    private final Queue<ProcessControlBlock> readyQueue = new Queue<>();
    public volatile AdmissionScheduler longTermScheduler;
    public MemoryManagementUnit memoryAPI;
    public ThreadAPI threadAPI;
    public ProcessAPI processAPI;
    public HardwareClockTickEmitter hardwareClockTickEmitter;
    public CompilerAPI compilerAPI;
    public DiskDrive diskDrive;
    private ControlUnit controlUnit;

    public void onLoadingReady() {
        componentLoader.componentLoaded("Initialising I/O subsystem...");
        diskDrive = new DiskDrive('C');
        componentLoader.componentLoaded("Successfully initialised the I/O subsystem.");

        componentLoader.componentLoaded("Initialising memory subsystem...");
        //NOTE: mmu and vmi are not yet initialised although they are not used until after they're initialised
        RandomAccessMemory ram = new RandomAccessMemory(MAIN_MEMORY_POWER, FirstFit.class);
        componentLoader.componentLoaded("Initialised Random Access memory.");
        final LogicalMemoryHandlerUtils handlerUtils = new LogicalMemoryHandlerUtils(ALLOCATION_POWER, SEGMENT_INCREASE_POWER);
        componentLoader.componentLoaded("Initialised logical memory handler interface utils.");

        if (USE_SEGMENTATION && USE_PAGING) {
            USE_PAGING = false;
        } else if (!USE_SEGMENTATION && !USE_PAGING) {
            USE_SEGMENTATION = true;
        }
        memoryAPI = new MemoryManagementUnit(ram, USE_SEGMENTATION, handlerUtils, CACHE_SIZE);
        componentLoader.componentLoaded("Initialised the Memory Management Unit.");

        if (VIRTUAL_MEMORY) {
            VirtualMemoryInterface virtualMemoryAPI = new VirtualMemoryInterface(memoryAPI, diskDrive);
            memoryAPI.initVirtualMemoryInterface(virtualMemoryAPI);
            ram.initVirtualMemoryInterface(virtualMemoryAPI);
            ram.initMemoryManagementUnit(memoryAPI);
        }
        componentLoader.componentLoaded("Initialised the Virtual Memory API.");
        componentLoader.componentLoaded("Successfully initialised the memory subsystem.");

        componentLoader.componentLoaded("Initialising the CPU architecture...");
        controlUnit = new ControlUnit(CPU_CORES, readyQueue, memoryAPI);
        componentLoader.componentLoaded("Initialised the CPU Control Unit.");
        controlUnit.initProcessorCoreThreads(CORE_FREQUENCY);
        componentLoader.componentLoaded("Initialised processor core threads.");
        CPUScheduler shortTermScheduler;
        if (USE_FIFO) {
            USE_SJF = false;
            shortTermScheduler = new CPUScheduler(FirstInFirstOut.class, readyQueue);
        } else {
            USE_SJF = true;
            shortTermScheduler = new CPUScheduler(ShortestJobFirst.class, readyQueue);
        }
        componentLoader.componentLoaded("Initialised the short term scheduler.");
        longTermScheduler = new AdmissionScheduler(shortTermScheduler);
        componentLoader.componentLoaded("Initialised the long term scheduler.");

        hardwareClockTickEmitter = new HardwareClockTickEmitter(CLOCK_TICK_FREQUENCY);
        componentLoader.componentLoaded("Initialised the system clock.");
        hardwareClockTickEmitter.addClockTickListener(longTermScheduler);
        hardwareClockTickEmitter.addClockTickListener(controlUnit);
        componentLoader.componentLoaded("Successfully initialised the processing & scheduling subsystem.");

        threadAPI = new ThreadAPI();
        processAPI = new ProcessAPI(threadAPI, memoryAPI, CLEAR_TERMINATED_PROCESS_MEMORY);
        componentLoader.componentLoaded("Initialised the process & thread APIs.");

        compilerAPI = new CompilerAPI(longTermScheduler, memoryAPI, processAPI);
        componentLoader.componentLoaded("Initialised the Compiler API.");
        componentLoader.complete();

/*        Thread compile1 = new Thread(() -> {
            compilerAPI.compile("sample");
        });
        Thread compile2 = new Thread(() -> {
            compilerAPI.compile("one_to_ten");
        });
        Thread compile3 = new Thread(() -> {
            compilerAPI.compile("advanced");
        });
        Thread compile4 = new Thread(() -> {
            compilerAPI.compile("sample");
        });
        compile1.start();
        compile2.start();
        compile3.start();
        compile4.start();*/
    }

    public void setTaskManagerController(TaskManagerController taskManagerController) {
        controlUnit.setTaskManagerOutput(taskManagerController);
    }

    public void initSystemClockPulse(TaskManagerController taskManagerController) {
        taskManagerController.initCoreUsageChart(CPU_CORES);
        new Thread(() -> {
            SystemClockPulse systemClockPulse = new SystemClockPulse(taskManagerController);
            systemClockPulse.addSystemPulseListener(memoryAPI);
            for (ProcessorCore core : controlUnit.getCores()) {
                systemClockPulse.addSystemPulseListener(core);
            }
        }).start();
    }
}
