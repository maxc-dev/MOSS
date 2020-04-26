package dev.maxc.os.bootup;

import java.util.List;

/**
 * @author Max Carter
 * @since 24/04/2020
 */
public class DynamicComponentLoader {
    private final List<LoadProgressUpdater> loadProgressListeners;
    private int size = 1;
    private int pointer;

    public DynamicComponentLoader(List<LoadProgressUpdater> loadProgressListeners) {
        this.loadProgressListeners = loadProgressListeners;
    }

    public void addToSize(int size) {
        this.size += size;
    }

    /**
     * Loads a component which allows the user to see the loading progress.
     */
    public void componentLoaded(String message) {
        for (LoadProgressUpdater loadProgressUpdater : loadProgressListeners) {
            loadProgressUpdater.onUpdateProgression(message, (double) (pointer +1)/size);
        }
        pointer++;
        try {
            //ensures that the splash screen is enabled for at least 2 seconds
            Thread.sleep(2000/size);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
