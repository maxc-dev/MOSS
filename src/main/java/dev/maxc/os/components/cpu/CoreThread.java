package dev.maxc.os.components.cpu;

/**
 * @author Max Carter
 * @since 07/05/2020
 */
public class CoreThread extends Thread {
    private final ProcessorCore core;
    private final int delay;

    public CoreThread(ProcessorCore core, int frequency) {
        this.core = core;
        this.delay = 1000/frequency;
    }

    @Override
    public void run() {
        core.requestInstruction();
        try {
            sleep(delay);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
