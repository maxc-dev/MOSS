package dev.maxc.sim.bootup;

/**
 * @author Max Carter
 * @since 11/04/2020
 */
public interface LoadProgressUpdater {
    void onUpdateProgression(String message, double percent);

    void onLoadComplete();
}
