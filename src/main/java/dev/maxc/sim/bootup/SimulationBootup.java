package dev.maxc.sim.bootup;

import java.util.ArrayList;
import java.util.List;

import dev.maxc.sim.bootup.config.ConfigurationReader;
import dev.maxc.sim.system.SystemAPI;
import dev.maxc.sim.logs.Logger;

/**
 * @author Max Carter
 * @since 10/04/2020
 */
public class SimulationBootup {
    private final List<LoadProgressUpdater> loadProgressListeners;

    public SimulationBootup() {
        loadProgressListeners = new ArrayList<>();
    }

    public void addProgressUpdaterListener(LoadProgressUpdater loadProgressUpdater) {
        loadProgressListeners.add(loadProgressUpdater);
    }

    /**
     * Loads all the OS simulator components
     */
    public void bootup() {
        Logger.log("Attempting to boot simulation...");
        Logger.log("Initializing configuration...");

        ComponentLoader componentLoader = new ComponentLoader(loadProgressListeners);

        //creating system api
        componentLoader.componentLoaded();
        SystemAPI systemAPI = new SystemAPI();
        componentLoader.componentLoaded();

        //initializing system api
        componentLoader.componentLoaded();
        ConfigurationReader configurationReader = new ConfigurationReader(systemAPI);
        configurationReader.configure();
        componentLoader.componentLoaded();

        //todo create components

        Logger.log("Configuration initialization successful.");
        for (LoadProgressUpdater loadProgressUpdater : loadProgressListeners) {
            loadProgressUpdater.onLoadComplete();
        }
    }
}
