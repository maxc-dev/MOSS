package dev.maxc.os.system.sync;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 08/05/2020
 */
public class HardwareClockTickEmitter {
    private final ArrayList<HardwareClockTick> hardwareClockTickListeners = new ArrayList<>();
    private final Thread clockTickThread;
    private boolean emit = true;

    public HardwareClockTickEmitter(int frequency) {
        clockTickThread = new Thread(() -> {
            while (emit) {
                emit();
                try {
                    Thread.sleep(1000/frequency);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        clockTickThread.start();
    }

    public void addClockTickListener(HardwareClockTick listener) {
        hardwareClockTickListeners.add(listener);
    }

    private void emit() {
        for (HardwareClockTick listener : hardwareClockTickListeners) {
            listener.onClockTick();
        }
    }

    public void toggleEmit(boolean emit) {
        this.emit = emit;
    }
}
