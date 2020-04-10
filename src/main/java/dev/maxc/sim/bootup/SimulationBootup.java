package dev.maxc.sim.bootup;

import dev.maxc.sim.bootup.config.ConfigurationReader;
import dev.maxc.sim.bootup.system.SystemAPI;
import dev.maxc.sim.logs.Logger;

/**
 * @author Max Carter
 * @since  10/04/2020
 */
public class SimulationBootup {
    public void bootup() {
        Logger.log("Attempting to boot simulation...");
        Logger.log("Initializing configuration...");

        SystemAPI systemAPI = new SystemAPI();
        ConfigurationReader configurationReader = new ConfigurationReader(systemAPI);
        configurationReader.configure();

        Logger.log("Configuration initialization successful...");

        //todo create components
    }
}
