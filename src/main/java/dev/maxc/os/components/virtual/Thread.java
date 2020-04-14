package dev.maxc.os.components.virtual;

/**
 * @author Max Carter
 * @since 14/04/2020
 */
public class Thread {
    private final int threadIdentifier;

    public Thread(int threadIdentifier) {
        this.threadIdentifier = threadIdentifier;
    }

    public int getThreadIdentifier() {
        return threadIdentifier;
    }
}
