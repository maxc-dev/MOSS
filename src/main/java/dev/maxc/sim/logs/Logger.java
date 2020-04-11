package dev.maxc.sim.logs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Max Carter
 * @since  10/04/2020
 */
public class Logger {
    private static final String LOG_FORMAT = "[%s] %s";
    private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    /**
     * Outputs a log message.
     */
    public static void log(String message) {
        System.out.println(String.format(LOG_FORMAT, dateFormat.format(Calendar.getInstance().getTimeInMillis()), message));
    }
}
