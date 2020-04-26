package dev.maxc.os.io.log;

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
     * Prints the log to the console.
     */
    private static void printLog(String message) {
        System.out.println(String.format(LOG_FORMAT, dateFormat.format(Calendar.getInstance().getTimeInMillis()), message));
    }

    /**
     * Requests a log with a a status, origin and message
     */
    public static void log(Status status, Object origin, String message) {
        printLog("[" + status.name() + "]\t" + "[" + getNameOfOrigin(origin) + "] " + message);
    }

    /**
     * Requests a log with a default status (INFO), an origin and a message
     */
    public static void log(Object origin, String message) {
        printLog("[" + Status.INFO.name() + "]\t" + "[" + getNameOfOrigin(origin) + "] " + message);
    }

    private static String getNameOfOrigin(Object origin) {
        String name = origin.getClass().getSimpleName();
        if (name.equals("String")) {
            name = origin.toString();
        }
        return name;
    }

}
