package dev.maxc.os.system.sync;

import dev.maxc.ui.anchors.TaskManagerController;

import java.util.ArrayList;

/**
 * @author Max Carter
 * @since 08/05/2020
 */
public class SystemClockPulse {
    private final ArrayList<SystemClock> clockTickListeners = new ArrayList<>();
    private final Thread systemTickThread;
    private boolean emit = true;
    private TaskManagerController taskManagerController;

    public SystemClockPulse(TaskManagerController taskManagerController) {
        this.taskManagerController = taskManagerController;
        systemTickThread = new Thread(() -> {
            while (emit) {
                emit();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        systemTickThread.start();
    }

    public void addSystemPulseListener(SystemClock listener) {
        clockTickListeners.add(listener);
    }

    private void emit() {
        for (SystemClock listener : clockTickListeners) {
            listener.onSecondTick(taskManagerController);
        }
    }

    public void toggleEmit(boolean emit) {
        this.emit = emit;
    }
}
