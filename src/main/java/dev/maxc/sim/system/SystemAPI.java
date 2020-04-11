/*
    This class provides an API between the Operating System Kernel
    and any application programs that want to access system config
    options.
 */

package dev.maxc.sim.system;

import dev.maxc.sim.bootup.config.Configurable;

/**
 * @author Max Carter
 * @since  10/04/2020
 */
public class SystemAPI {

    //system config

    @Configurable(docs = "The amount of time between each CPU execution in ms.")
    public int CLOCK_TICK_FREQUENCY;

    public static final String SYSTEM_NAME = "M.O.S.S";

    public static final String SYSTEM_AUTHOR = "Max Carter";

    //cpu config

    @Configurable(docs = "The amount of cores in the CPU.")
    public int CPU_CORES;

    @Configurable(docs = "If set to true, the CPU cores will run in async which means they will handle instructions independently from other cores. When set to false, the CPU cores will run in sync with one another and share resources and instructions.")
    public boolean CPU_CORES_ASYNC;

    //ram config

    @Configurable(docs = "The amount of memory locations available.")
    public int MAIN_MEMORY_SIZE;

    @Configurable(docs = "When set to true, virtual memory will be enabled so memory is stored in pages in the main storage.")
    public boolean VIRTUAL_MEMORY_ENABLED;

    //process config

    @Configurable("When set to true, threads will be scheduled along with their parent Process. When set to false, threads wil be executed independently from their parent Process.")
    public boolean PROCESS_FIRST_SCHEDULING;
}
