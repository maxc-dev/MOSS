package dev.maxc.sim.bootup;

import java.util.ArrayList;
import java.util.List;

import dev.maxc.sim.bootup.config.ConfigurationReader;
import dev.maxc.sim.system.SystemAPI;
import dev.maxc.sim.logs.Logger;
import dev.maxc.sim.system.SystemUtils;

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

        SystemAPI systemAPI = new SystemAPI();
        ConfigurationReader configurationReader = new ConfigurationReader(systemAPI);
        configurationReader.configure();

        //todo create components

        //demo simulates the loading screen
        for (int i = 0; i < 100; i++) {
            try {
                //random delay which would be replaced with component loading
                Thread.sleep(SystemUtils.randomInt(20, 200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (LoadProgressUpdater loadProgressUpdater : loadProgressListeners) {
                loadProgressUpdater.onUpdateProgression("", i, (1000 - i * 10) + "ms");
            }
        }

        Logger.log("Configuration initialization successful...");
        for (LoadProgressUpdater loadProgressUpdater : loadProgressListeners) {
            loadProgressUpdater.onLoadComplete();
        }
    }
}
