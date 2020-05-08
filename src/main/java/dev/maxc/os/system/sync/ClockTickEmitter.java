package dev.maxc.os.system.sync;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 08/05/2020
 */
public class ClockTickEmitter {
    private final ArrayList<ClockTick> clockTickListeners = new ArrayList<>();
    private final Thread clockTickThread;
    private boolean emit = true;

    public ClockTickEmitter(int msDelay) {
        clockTickThread = new Thread(() -> {
            while (emit) {
                emit();
                try {
                    Thread.sleep(msDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        clockTickThread.start();
    }

    public void addClockTickListener(ClockTick listener) {
        clockTickListeners.add(listener);
    }

    private void emit() {
        for (ClockTick listener : clockTickListeners) {
            listener.onSystemClockTick();
        }
    }

    public void toggleEmit(boolean emit) {
        this.emit = emit;
    }
}
