package dev.maxc.sim.bootup;

import java.util.List;

import dev.maxc.sim.logs.Logger;

/**
 * @author Max Carter
 * @since 12/04/2020
 */
public class ComponentLoader {
    public static final String[] loadingComponents = new String[]{
            "Creating System API...",
            "System API created.",
            "Configuring System API...",
            "Parsing process first scheduling config option...",
            "Parsed process first scheduling config option.",
            "Parsing clock tick frequency config option...",
            "Parsed clock tick frequency config option.",
            "Parsing CPU cores using async config option...",
            "Parsed CPU cores using async config option.",
            "Parsing virtual memory config option...",
            "Parsed virtual memory config option.",
            "Parsing main memory size config option...",
            "Parsed main memory size config option.",
            "Parsing CPU cores config option...",
            "Parsed CPU cores config option.",
            "System API successfully configured.",
            "Loading drivers...",
    };

    private final List<LoadProgressUpdater> loadProgressListeners;
    private int pointer = 0;

    /**
     * Adds a component listener for when a component is loaded.
     */
    public ComponentLoader(List<LoadProgressUpdater> loadProgressListeners) {
        this.loadProgressListeners = loadProgressListeners;
    }

    /**
     * Loaded a component at a specific pointer, all automatic
     * component updates after this will resume from this pointer.
     */
    public void componentLoaded(int pointer) {
        this.pointer = pointer;
        componentLoaded();
    }

    /**
     * Increments the pointer and notifies the listeners that a new
     * components has loaded.
     */
    public void componentLoaded() {
        if (pointer > loadingComponents.length) {
            Logger.log("Bootup", "Prevented loading component overflow failure.");
            return;
        }
        for (LoadProgressUpdater loadProgressUpdater : loadProgressListeners) {
            loadProgressUpdater.onUpdateProgression(loadingComponents[pointer], (double) (pointer +1)/loadingComponents.length);
        }
        Logger.log("Bootup", "Loaded component [" + loadingComponents[pointer] + "]" + " (" + (int) (((double) (pointer +1)/loadingComponents.length)*100) + "%)");
        pointer++;
        try {
            //ensures that the splash screen is enabled for at least 2 seconds
            Thread.sleep(2000/loadingComponents.length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
