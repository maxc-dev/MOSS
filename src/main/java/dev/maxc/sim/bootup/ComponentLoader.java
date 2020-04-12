package dev.maxc.sim.bootup;

import java.util.List;

import dev.maxc.sim.logs.Logger;

/**
 * @author Max Carter
 * @since 12/04/2020
 */
public class ComponentLoader {
    public static final String[] loadingComponents = new String[]{
            "Creating System API.",
            "System API created.",
            "Configuring System API.",
            "System API successfully configured."
    };

    private final List<LoadProgressUpdater> loadProgressListeners;
    private int index = 0;

    public ComponentLoader(List<LoadProgressUpdater> loadProgressListeners) {
        this.loadProgressListeners = loadProgressListeners;
    }

    public void componentLoaded() {
        if (index > loadingComponents.length) {
            Logger.log("Bootup", "Prevented loading component overflow failure.");
            return;
        }
        for (LoadProgressUpdater loadProgressUpdater : loadProgressListeners) {
            loadProgressUpdater.onUpdateProgression(loadingComponents[index], (double) (index+1)/loadingComponents.length);
        }
        Logger.log("Bootup", "Loaded component [" + loadingComponents[index] + "]" + " (" + (((double) (index+1)/loadingComponents.length)*100) + "%)");
        index++;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
