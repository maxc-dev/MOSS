package dev.maxc.sim.bootup.config;

import dev.maxc.sim.bootup.system.SystemAPI;

/**
 * @author Max Carter
 * @since 10/04/2020
 */
public class ConfigurationReader {
    private static final String CONFIG_FILE_NAME = "config.txt";

    private final SystemAPI system;

    /**
     * Creates a system reader
     */
    protected ConfigurationReader(SystemAPI system) {
        this.system = system;
    }

    /**
     * Reads the config file and updates the attributes in the config object
     */
    protected void configure() {

    }

    protected SystemAPI getSystemAPI() {
        return system;
    }
}
