/*
    This class provides an API between the Operating System Kernel
    and any application programs that want to access system config
    options.
 */

package dev.maxc.sim.bootup.system;

import dev.maxc.sim.bootup.config.Configurable;

/**
 * @author Max Carter
 * @since 10/04/2020
 */
public class SystemAPI {

    //cpu config

    @Configurable
    public static int CPU_CORES;

    @Configurable
    public static boolean CPU_CORES_ASYNC;

}
