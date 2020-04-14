package dev.maxc.os.components.virtual;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Process {
    private final ArrayList<Thread> threads = new ArrayList<>();
    private final int processIdentifier;

    public Process(int processIdentifier) {
        this.processIdentifier = processIdentifier;
    }

    public ArrayList<Thread> getThreads() {
        return threads;
    }

    public int getProcessIdentifier() {
        return processIdentifier;
    }
}
