/*
    This class provides an API between the Operating System Kernel
    and any application programs that want to access system config
    options.
 */

package dev.maxc.os.system.api;

import dev.maxc.logs.Logger;
import dev.maxc.os.bootup.LoadProgressUpdater;
import dev.maxc.os.bootup.config.Configurable;
import dev.maxc.os.components.memory.RandomAccessMemory;
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

    //ram config

    @Configurable(docs = "The amount of memory locations available.")
    public int MAIN_MEMORY_SIZE;

    @Configurable(docs = "The amount of memory locations that are available for each page.")
    public int MEMORY_PAGE_SIZE;

    @Configurable(docs = "When set to true, virtual memory will be enabled so memory is stored in pages in the main storage.")
    public boolean VIRTUAL_MEMORY_ENABLED;

    @Configurable(docs = "Memory is allocated to a process when it needs it during execution. This does not bypass the maximum amount of memory per process.")
    public boolean DYNAMIC_MEMORY_ALLOCATION;

    @Configurable(docs = "A set amount of memory is allocated when the process is created.")
    public boolean STATIC_MEMORY_ALLOCATION;

    @Configurable(docs = "The maximum amount of memory a process can be allocated.")
    public int MAX_MEMORY_PER_PROCESS;

    //process config

    @Configurable(docs = "When set to true, threads will be scheduled along with their parent Process. When set to false, threads wil be executed independently from their parent Process.")
    public boolean PROCESS_FIRST_SCHEDULING;

    public static UserInterfaceAPI uiAPI = new UserInterfaceAPI();

    public MemoryAPI memoryAPI;
    public ThreadAPI threadAPI;
    public ProcessAPI processAPI;

    @Override
    public void onUpdateProgression(String message, double percent) {
    }

    @Override
    public void onLoadComplete() {
        memoryAPI = new MemoryAPI(new RandomAccessMemory(MAIN_MEMORY_SIZE), MEMORY_PAGE_SIZE);
        threadAPI = new ThreadAPI();
        processAPI = new ProcessAPI(threadAPI);
        Logger.log("System", "Created memory, thread and process APIs.");
    }
}
