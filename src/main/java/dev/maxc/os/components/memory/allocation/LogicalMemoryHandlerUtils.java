package dev.maxc.os.components.memory.allocation;

/**
 * @author Max Carter
 * @since 25/04/2020
 */
public class LogicalMemoryHandlerUtils {
    private final int initialSize;
    private final int increase;

    public LogicalMemoryHandlerUtils(int base, int power, int segmentPowerIncrease) {
        this.initialSize = (int) Math.pow(base, power);
        this.increase = (int) Math.pow(base, segmentPowerIncrease);
    }

    public int getInitialSize() {
        return initialSize;
    }

    public int getIncrease() {
        return increase;
    }
}
