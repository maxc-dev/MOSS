package dev.maxc.os.bootup;

import java.util.ArrayList;
import java.util.List;

import dev.maxc.os.bootup.config.ConfigurationReader;
import dev.maxc.os.system.SystemAPI;
import dev.maxc.logs.Logger;

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
        ConfigurationReader configurationReader = new ConfigurationReader(systemAPI, componentLoader);
        configurationReader.configure();
        componentLoader.componentLoaded();

        //todo create components

        Logger.log("Configuration initialization successful.");
        for (LoadProgressUpdater loadProgressUpdater : loadProgressListeners) {
            loadProgressUpdater.onLoadComplete();
        }
    }
}
