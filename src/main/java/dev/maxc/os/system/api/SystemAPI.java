/*
    This class provides an API between the Operating System Kernel
    and any application programs that want to access system config
    options.
 */

package dev.maxc.os.system.api;

import dev.maxc.logs.Logger;
import dev.maxc.os.bootup.LoadProgressUpdater;
import dev.maxc.os.bootup.config.Configurable;
import dev.maxc.os.components.memory.MemoryManagementUnit;
import dev.maxc.os.components.memory.RandomAccessMemory;
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

    @Configurable(docs = "Memory is allocated to a process when it needs it during execution.")
    public boolean DYNAMIC_MEMORY_ALLOCATION;

    @Configurable(value = "malloc_algorithm_use_segmentation", docs = "If the MMU uses segmentation to allocate memory.")
    public boolean USE_SEGMENTATION;

    @Configurable(value = "malloc_algorithm_use_paging", docs = "If the MMU uses paging to allocate memory.")
    public boolean USE_PAGING;

    //paging config

    @Configurable(docs = "Base of the paging system.")
    public int PAGE_SIZE_BASE;

    @Configurable(docs = "The power of each page.")
    public int PAGE_SIZE_POWER;

    //segmentation config

    @Configurable(docs = "Base of the paging system.")
    public int SEGMENTATION_SIZE_BASE;

    @Configurable(docs = "The power of each page.")
    public int SEGMENTATION_SIZE_POWER;

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
        //TODO update ram and mmu with new paging/segmentation algorithm arch
        //memoryAPI = new MemoryManagementUnit(new RandomAccessMemory(MAIN_MEMORY_SIZE, PAGE_SIZE, DYNAMIC_MAX_PAGE_SIZE, MAX_MEMORY_PER_PAGE, VIRTUAL_MEMORY), DYNAMIC_MEMORY_ALLOCATION, PAGE_INCREASE_INCREMENT);
        threadAPI = new ThreadAPI();
        processAPI = new ProcessAPI(threadAPI);
        Logger.log("System", "Created memory, thread and process APIs.");
    }
}
