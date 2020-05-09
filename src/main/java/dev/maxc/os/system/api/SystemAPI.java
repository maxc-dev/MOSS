/*
    This class provides an API between the Operating System Kernel
    and any application programs that want to access system config
    options.
 */

package dev.maxc.os.system.api;

import dev.maxc.os.components.cpu.ControlUnit;
import dev.maxc.os.components.interpreter.Interpreter;
import dev.maxc.os.components.memory.allocation.LogicalMemoryHandlerUtils;
import dev.maxc.os.components.process.ProcessControlBlock;
import dev.maxc.os.components.scheduler.AdmissionScheduler;
import dev.maxc.os.components.scheduler.CPUScheduler;
import dev.maxc.os.components.scheduler.disciplines.FirstInFirstOut;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.bootup.LoadProgressUpdater;
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
public class SystemAPI implements LoadProgressUpdater {
    //system constants
    public static final String SYSTEM_NAME = "MOSS";
    public static final String SYSTEM_AUTHOR = "Max Carter";

    //system config

    @Configurable(docs = "The amount of time between each CPU execution in ms.")
    public int CLOCK_TICK_FREQUENCY;

    //cpu config

    @Configurable(docs = "The amount of cores in the CPU.")
    public int CPU_CORES;

    @Configurable(value = "cpu_core_frequency", docs = "The maximum amount of processes a CPU core can handle per second.")
    public int CORE_FREQUENCY;

    @Configurable(docs = "If set to true, the CPU cores will run in async which means they will handle instructions independently from other cores. When set to false, the CPU cores will run in sync with one another and share resources and instructions.")
    public boolean CPU_CORES_ASYNC;

    //memory config

    @Configurable(docs = "The amount of memory locations available.")
    public int MAIN_MEMORY_BASE;

    @Configurable(docs = "The amount of memory locations available.")
    public int MAIN_MEMORY_POWER;

    @Configurable(value = "malloc_algorithm_use_segmentation", docs = "If the MMU uses segmentation to allocate memory.")
    public boolean USE_SEGMENTATION;

    @Configurable(value = "malloc_algorithm_use_paging", docs = "If the MMU uses paging to allocate memory.")
    public boolean USE_PAGING;

    //paging config

    @Configurable(value="malloc_size_base", docs = "The base of the allocation system.")
    public int ALLOCATION_BASE;

    @Configurable(value="malloc_size_power", docs = "The power of allocation system.")
    public int ALLOCATION_POWER;

    //segmentation config

    @Configurable(docs = "The power by which the segments will increase by (Base^x).")
    public int SEGMENT_INCREASE_POWER;

    //memory allocation config

    @Configurable(value = "malloc_best_fit", docs = "The memory given to a new allocation is the smallest possible, but just big enough.")
    public boolean ALLOCATION_BEST_FIT;

    @Configurable(value = "malloc_first_fit", docs = "The memory given to a new allocation is the first occurrence available which is big enough.")
    public boolean ALLOCATION_FIRST_FIT;

    @Configurable(value = "malloc_worst_fit", docs = "The memory given to a new allocation is the largest space available.")
    public boolean ALLOCATION_WORST_FIT;

    @Configurable(value = "cache_size_level_1", docs = "The amount of memory that the cache can store.")
    public int CACHE_SIZE;

    //virtual memory config

    @Configurable(docs = "When set to true, virtual memory will be enabled so memory is stored in pages in the main storage.")
    public boolean VIRTUAL_MEMORY;

    //process config

    @Configurable(docs = "When set to true, threads will be scheduled along with their parent Process. When set to false, threads wil be executed independently from their parent Process.")
    public boolean PROCESS_FIRST_SCHEDULING;

    public static UserInterfaceAPI uiAPI = new UserInterfaceAPI();

    private volatile Queue<ProcessControlBlock> jobQueue = new Queue<>();
    private volatile CPUScheduler shortTermScheduler = new CPUScheduler(FirstInFirstOut.class, jobQueue);
    public volatile AdmissionScheduler longTermScheduler;

    public MemoryManagementUnit memoryAPI;
    public ThreadAPI threadAPI;
    public ProcessAPI processAPI;
    public ClockTickEmitter clockTickEmitter;

    @Override
    public void onUpdateProgression(String message, double percent) {
    }

    @Override
    public void onLoadComplete() {
        RandomAccessMemory ram = new RandomAccessMemory(MAIN_MEMORY_BASE, MAIN_MEMORY_POWER, FirstFit.class, VIRTUAL_MEMORY);
        final LogicalMemoryHandlerUtils handlerUtils = new LogicalMemoryHandlerUtils(ALLOCATION_BASE, ALLOCATION_POWER, SEGMENT_INCREASE_POWER);

        if (USE_SEGMENTATION && USE_PAGING) {
            USE_PAGING = false;
        } else if (!USE_SEGMENTATION && !USE_PAGING) {
            USE_SEGMENTATION = true;
        }
        memoryAPI = new MemoryManagementUnit(ram, USE_SEGMENTATION, handlerUtils, CACHE_SIZE);

        final ControlUnit controlUnit = new ControlUnit(CPU_CORES, jobQueue, memoryAPI);
        controlUnit.initProcessorCoreThreads(CLOCK_TICK_FREQUENCY);
        longTermScheduler = new AdmissionScheduler(shortTermScheduler);
        clockTickEmitter = new ClockTickEmitter(CLOCK_TICK_FREQUENCY);
        clockTickEmitter.addClockTickListener(longTermScheduler);
        clockTickEmitter.addClockTickListener(controlUnit);

        threadAPI = new ThreadAPI();
        processAPI = new ProcessAPI(threadAPI, memoryAPI);
        Logger.log(this, "Created memory, thread and process APIs.");
        new Thread(() -> {
            do {
                Interpreter interpreter = new Interpreter(longTermScheduler, memoryAPI, processAPI);
                interpreter.interpret("pf1");
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }).start();
    }
}
