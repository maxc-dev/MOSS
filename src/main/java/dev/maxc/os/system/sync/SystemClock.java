package dev.maxc.os.system.sync;

import dev.maxc.ui.anchors.TaskManagerController;

/**
 * @author Max Carter
 * @since 02/05/2020
 */
public interface SystemClock {
    void onSecondTick(TaskManagerController taskManagerController);
}
