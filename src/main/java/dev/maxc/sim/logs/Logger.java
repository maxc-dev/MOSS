package dev.maxc.sim.logs;

/**
 * @author Max Carter
 * @since 10/04/2020
 */
public class Logger {
    private static final String LOG_FORMAT = "[%s] %s";

    /**
     * Outputs a log message.
     */
    public static void log(String message) {
        System.out.println(String.format(LOG_FORMAT, "$time", message));
    }
}
