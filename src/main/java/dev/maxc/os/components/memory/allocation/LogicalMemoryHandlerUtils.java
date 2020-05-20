package dev.maxc.os.components.memory.allocation;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public class LogicalMemoryHandlerUtils {
    private final int initialSize;
    private final int increase;

    public LogicalMemoryHandlerUtils(int power, int segmentPowerIncrease) {
        this.initialSize = (int) Math.pow(2, power);
        this.increase = (int) Math.pow(2, segmentPowerIncrease);
    }

    public int getInitialSize() {
        return initialSize;
    }

    public int getIncrease() {
        return increase;
    }
}
