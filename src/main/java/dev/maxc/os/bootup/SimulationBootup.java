package dev.maxc.os.bootup;

import java.util.ArrayList;
import java.util.List;

import dev.maxc.os.bootup.config.ConfigurationReader;
import dev.maxc.os.system.api.SystemAPI;
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
        Logger.log("Bootup", "Attempting to boot simulation...");
        Logger.log("Config", "Initializing configuration...");

        DynamicComponentLoader componentLoader = new DynamicComponentLoader(loadProgressListeners);
        componentLoader.addToSize(30);

        //creating system api
        componentLoader.componentLoaded("Creating System API...");
        SystemAPI systemAPI = new SystemAPI();
        addProgressUpdaterListener(systemAPI);
        componentLoader.componentLoaded("System API created.");

        //initializing system api
        componentLoader.componentLoaded("Configuring System API...");
        ConfigurationReader configurationReader = new ConfigurationReader(systemAPI, componentLoader);
        configurationReader.configure();
        componentLoader.componentLoaded("System API successfully configured.");

        //todo create components

        Logger.log("Config", "Configuration initialization successful.");
        for (LoadProgressUpdater loadProgressUpdater : loadProgressListeners) {
            loadProgressUpdater.onLoadComplete();
        }
    }
}
