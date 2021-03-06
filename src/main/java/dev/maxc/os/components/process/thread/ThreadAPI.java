package dev.maxc.os.components.process.thread;

import dev.maxc.os.components.process.Process;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class ThreadAPI {
    private final AtomicInteger threadCount = new AtomicInteger();

    /**
     * Creates a new Thread
     */
    public Thread getNewThread(Process process) {
        return new Thread(threadCount.addAndGet(1), process.getProcessControlBlock().getProcessID());
    }

    /**
     * Adds a new Thread to a Process
     */
    public void addNewThreadToProcess(Process process) {
        process.getThreads().add(getNewThread(process));
    }

    @Override
    public String toString() {
        return "Thread-" + threadCount;
    }
}
