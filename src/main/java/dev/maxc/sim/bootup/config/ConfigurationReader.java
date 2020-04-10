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
    public ConfigurationReader(SystemAPI system) {
        this.system = system;
    }

    /**
     * Reads the config file and updates the attributes in the config object
     */
    public void configure() {

    }

    public SystemAPI getSystemAPI() {
        return system;
    }

    protected static final class ConfigurationFileReader {
        private static final char COMMENT_CHARACTER = '#';

        protected String[] getConfigFileStream() {
            /*
                loop through file line by line
                if line does not start with '#':
                    send to observable
             */
            return new String[]{};
        }
    }
}
