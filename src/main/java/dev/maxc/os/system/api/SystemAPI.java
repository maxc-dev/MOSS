/*
    This class provides an API between the Operating System Kernel
    and any application programs that want to access system config
    options.
 */

package dev.maxc.os.system.api;

import dev.maxc.os.io.log.Logger;
import dev.maxc.os.bootup.LoadProgressUpdater;
import dev.maxc.os.bootup.config.Configurable;
import dev.maxc.os.components.memory.*;
import dev.maxc.os.components.memory.indexer.FirstFit;
import dev.maxc.os.components.virtual.process.ProcessAPI;
import dev.maxc.os.components.virtual.thread.ThreadAPI;
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
    public int SEGMENTATION_INCREASE_POWER;

    //virtual memory config

    @Configurable(docs = "When set to true, virtual memory will be enabled so memory is stored in pages in the main storage.")
    public boolean VIRTUAL_MEMORY;

    //process config

    @Configurable(docs = "When set to true, threads will be scheduled along with their parent Process. When set to false, threads wil be executed independently from their parent Process.")
    public boolean PROCESS_FIRST_SCHEDULING;

    public static UserInterfaceAPI uiAPI = new UserInterfaceAPI();

    public MemoryManagementUnit memoryAPI;
    public ThreadAPI threadAPI;
    public ProcessAPI processAPI;

    @Override
    public void onUpdateProgression(String message, double percent) {
    }

    @Override
    public void onLoadComplete() {
        RandomAccessMemory ram = new RandomAccessMemory(MAIN_MEMORY_BASE, ALLOCATION_POWER, new FirstFit(), VIRTUAL_MEMORY);
        LogicalMemoryHandlerUtils handlerUtils = new LogicalMemoryHandlerUtils(ALLOCATION_BASE, ALLOCATION_POWER, SEGMENTATION_INCREASE_POWER);
        if (USE_SEGMENTATION) {
            USE_PAGING = false;
            memoryAPI = new MemoryManagementUnit<Segment>(ram, Segment.class, handlerUtils);
        } else {
            USE_PAGING = true;
            memoryAPI = new MemoryManagementUnit<Page>(ram, Page.class, handlerUtils);
        }

        threadAPI = new ThreadAPI();
        processAPI = new ProcessAPI(threadAPI);
        Logger.log(this, "Created memory, thread and process APIs.");
    }
}
