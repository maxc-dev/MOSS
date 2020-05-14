/*
    This class provides an API between the Operating System Kernel
    and any application programs that want to access system config
    options.
 */

package dev.maxc.os.system.api;

import dev.maxc.os.bootup.DynamicComponentLoader;
import dev.maxc.os.components.compiler.CompilerAPI;
import dev.maxc.os.components.cpu.ControlUnit;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.scheduler.AdmissionScheduler;
import dev.maxc.os.components.scheduler.CPUScheduler;
import dev.maxc.os.components.scheduler.disciplines.FirstInFirstOut;
import dev.maxc.os.bootup.config.Configurable;
import dev.maxc.os.components.memory.*;
import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.process.ProcessAPI;
import dev.maxc.os.components.process.thread.ThreadAPI;
import dev.maxc.os.structures.Queue;
import dev.maxc.os.system.sync.ClockTickEmitter;
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

    //memory config

    @Configurable(docs = "The amount of memory locations available.", min = 2, max = 3, recommended = 2)
    public int MAIN_MEMORY_BASE;

    @Configurable(docs = "The amount of memory locations available.", min = 2, max = 24, recommended = 12)
    public int MAIN_MEMORY_POWER;

    @Configurable(value = "malloc_algorithm_use_segmentation", docs = "If the MMU uses segmentation to allocate memory.")
    public boolean USE_SEGMENTATION;

    @Configurable(value = "malloc_algorithm_use_paging", docs = "If the MMU uses paging to allocate memory.")
    public boolean USE_PAGING;

    //paging config

    @Configurable(value = "malloc_size_base", docs = "The base of the allocation system.", min = 2, max = 3, recommended = 2)
    public int ALLOCATION_BASE;

    @Configurable(value = "malloc_size_power", docs = "The power of allocation system.", min = 2, max = 12, recommended = 4)
    public int ALLOCATION_POWER;

    //segmentation config

    @Configurable(docs = "The power by which the segments will increase by (Base^x).", min = 1, max = 8, recommended = 4)
    public int SEGMENT_INCREASE_POWER;

    //memory allocation config

    @Configurable(value = "malloc_best_fit", docs = "The memory given to a new allocation is the smallest possible, but just big enough.")
    public boolean ALLOCATION_BEST_FIT;

    @Configurable(value = "malloc_first_fit", docs = "The memory given to a new allocation is the first occurrence available which is big enough.")
    public boolean ALLOCATION_FIRST_FIT;

    @Configurable(value = "malloc_worst_fit", docs = "The memory given to a new allocation is the largest space available.")
    public boolean ALLOCATION_WORST_FIT;

    @Configurable(value = "cache_size_level_1", docs = "The amount of memory that the cache can store.", min = 0, max = 1024, recommended = 24)
    public int CACHE_SIZE;

    //virtual memory config

    @Configurable(docs = "When set to true, virtual memory will be enabled so memory is stored in pages in the main storage.")
    public boolean VIRTUAL_MEMORY;

    public static UserInterfaceAPI uiAPI = new UserInterfaceAPI();

    private volatile Queue<ProcessControlBlock> readyQueue = new Queue<>();
    private volatile CPUScheduler shortTermScheduler = new CPUScheduler(FirstInFirstOut.class, readyQueue);
    public volatile AdmissionScheduler longTermScheduler;

    public MemoryManagementUnit memoryAPI;
    public ThreadAPI threadAPI;
    public ProcessAPI processAPI;
    public ClockTickEmitter clockTickEmitter;
    public CompilerAPI compilerAPI;

    public void onLoadingReady() {
        componentLoader.componentLoaded("Initialising memory subsystem...");
        RandomAccessMemory ram = new RandomAccessMemory(MAIN_MEMORY_BASE, MAIN_MEMORY_POWER, FirstFit.class, VIRTUAL_MEMORY);
        componentLoader.componentLoaded("Initialised Random Access memory.");
        final LogicalMemoryHandlerUtils handlerUtils = new LogicalMemoryHandlerUtils(ALLOCATION_BASE, ALLOCATION_POWER, SEGMENT_INCREASE_POWER);
        componentLoader.componentLoaded("Initialised logical memory handler interface utils.");

        if (USE_SEGMENTATION && USE_PAGING) {
            USE_PAGING = false;
        } else if (!USE_SEGMENTATION && !USE_PAGING) {
            USE_SEGMENTATION = true;
        }
        memoryAPI = new MemoryManagementUnit(ram, USE_SEGMENTATION, handlerUtils, CACHE_SIZE);
        componentLoader.componentLoaded("Initialised the Memory Management Unit.");
        componentLoader.componentLoaded("Successfully initialised the memory subsystem.");

        componentLoader.componentLoaded("Initialising the CPU architecture...");
        final ControlUnit controlUnit = new ControlUnit(CPU_CORES, readyQueue, memoryAPI);
        componentLoader.componentLoaded("Initialised the CPU Control Unit.");
        controlUnit.initProcessorCoreThreads(CORE_FREQUENCY);
        componentLoader.componentLoaded("Initialised processor core threads.");
        longTermScheduler = new AdmissionScheduler(shortTermScheduler);
        componentLoader.componentLoaded("Initialised the long term scheduler.");

        clockTickEmitter = new ClockTickEmitter(CLOCK_TICK_FREQUENCY);
        componentLoader.componentLoaded("Initialised the system clock.");
        clockTickEmitter.addClockTickListener(longTermScheduler);
        clockTickEmitter.addClockTickListener(controlUnit);
        componentLoader.componentLoaded("Successfully initialised the processing & scheduling subsystem.");

        threadAPI = new ThreadAPI();
        processAPI = new ProcessAPI(threadAPI, memoryAPI);
        componentLoader.componentLoaded("Initialised the process & thread APIs.");

        compilerAPI = new CompilerAPI(longTermScheduler, memoryAPI, processAPI);
        componentLoader.componentLoaded("Initialised the Compiler API.");
        componentLoader.complete();

/*        Thread compile1 = new Thread(() -> {
            Compiler compiler = new Compiler(longTermScheduler, memoryAPI, processAPI);
            compiler.compile("pf1");
        });
        Thread compile2 = new Thread(() -> {
            Compiler compiler = new Compiler(longTermScheduler, memoryAPI, processAPI);
            compiler.compile("pf1");
        });
        Thread compile3 = new Thread(() -> {
            Compiler compiler = new Compiler(longTermScheduler, memoryAPI, processAPI);
            compiler.compile("pf1");
        });
        Thread compile4 = new Thread(() -> {
            Compiler compiler = new Compiler(longTermScheduler, memoryAPI, processAPI);
            compiler.compile("pf1");
        });
        compile1.start();
        compile2.start();
        compile3.start();
        compile4.start();*/
    }
}
