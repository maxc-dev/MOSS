package dev.maxc.os.components.virtual.thread;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Thread {
    private final int parentProcessIdentifier;
    private final int threadIdentifier;

    public Thread(int threadIdentifier, int parentProcessIdentifier) {
        this.threadIdentifier = threadIdentifier;
        this.parentProcessIdentifier = parentProcessIdentifier;
    }

    public int getParentProcessID() {
        return parentProcessIdentifier;
    }

    public int getThreadIdentifier() {
        return threadIdentifier;
    }

    @Override
    public String toString() {
        return "Thread-" + threadIdentifier;
    }
}
