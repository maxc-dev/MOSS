package dev.maxc.os.bootup;

import java.util.ArrayList;
import java.util.List;

import dev.maxc.os.bootup.config.ConfigurationReader;
import dev.maxc.os.system.api.SystemAPI;
import dev.maxc.os.io.log.Logger;
/**
 * @author Max Carter
 * @since 10/04/2020
 */
public class SimulationBootup {
    private final List<LoadProgressUpdater> loadProgressListeners = new ArrayList<>();
    private SystemAPI systemAPI;

    public void addProgressUpdaterListener(LoadProgressUpdater loadProgressUpdater) {
        loadProgressListeners.add(loadProgressUpdater);
    }

    /**
     * Loads all the OS simulator components
     */
    public void bootup() {
        Logger.log(this, "Attempting to boot simulation...");

        DynamicComponentLoader componentLoader = new DynamicComponentLoader(loadProgressListeners);

        //creating system api
        componentLoader.componentLoaded("Creating System API...");
        systemAPI = new SystemAPI(componentLoader);
        componentLoader.componentLoaded("System API created.");

        //initializing system api
        componentLoader.componentLoaded("Configuring System API...");
        ConfigurationReader configurationReader = new ConfigurationReader(systemAPI, componentLoader);
        configurationReader.configure();
        componentLoader.componentLoaded("System API successfully configured.");

        Logger.log(this, "Configuration initialization successful.");
        systemAPI.onLoadingReady();
    }

    public SystemAPI getSystemAPI() {
        return systemAPI;
    }
}
