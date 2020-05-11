package dev.maxc.os.components.cpu;

import dev.maxc.os.io.log.Logger;

/**
 * @author Max Carter
 * @since 07/05/2020
 */
public class CoreThread extends Thread {
    private volatile ProcessorCore core;
    private final int delay;
    protected volatile boolean busy = false;

    public CoreThread(ProcessorCore core, int frequency) {
        Logger.log(this, "Processor Core Thread initialised, operating @" + frequency + "hz, awaiting instructions.");
        this.core = core;
        this.delay = 1000/frequency;
    }

    @Override
    public synchronized void run() {
        while (true) {
            if (!busy) {
                core.readPCBSocket();
            }
            try {
                sleep(delay);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected synchronized void setBusy(boolean busy) {
        this.busy = busy;
    }
}
