package dev.maxc.os.io.log;

import dev.maxc.ui.anchors.TaskManagerController;
import javafx.application.Platform;

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

    private static volatile String latestLog = "";
    private static volatile int logRepeatCount = 0;
    private static TaskManagerController taskManagerController;

    public static void setTaskManagerController(TaskManagerController taskManagerController) {
        Logger.taskManagerController = taskManagerController;
    }

    /**
     * Prints the log to the console.
     */
    private static synchronized void printLog(String message) {
        if (message.equals(latestLog)) {
            logRepeatCount++;
        } else {
            if (logRepeatCount > 0) {
                print(String.format(LOG_FORMAT, dateFormat.format(Calendar.getInstance().getTimeInMillis()), "\t\t\t^ (Repeated line x" + logRepeatCount + ")"));
                logRepeatCount = 0;
            }
            print(String.format(LOG_FORMAT, dateFormat.format(Calendar.getInstance().getTimeInMillis()), message));
            latestLog = message;
        }
    }

    private static void print(String message) {
        if (taskManagerController != null) {
            Platform.runLater(() -> taskManagerController.console(message));
        }
        System.out.println(message);
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
