package dev.maxc.sim.system;

/*
    This class provides utility resources to application programs
    and programs within the kernel.
 */

import java.util.Random;

import dev.maxc.sim.logs.Logger;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * @author Max Carter
 * @since  10/04/2020
 */
public class SystemUtils {

    /**
     * Returns a random number between two numbers.
     * @param min minimum
     * @param max maximum
     * @return random number
     */
    public static int randomInt(int min, int max) {
        return new Random().nextInt((max-min)+1)+min;
    }

    /**
     * Returns true on a chance
     * @param chance chance out of 100
     */
    public static boolean chance(int chance) {
        return randomInt(0, 100) < chance;
    }

    /**
     * Closes the simulation and the system.
     */
    public static void shutdown() {
        Logger.log("Shutting down...");
        Platform.exit();
        System.exit(0);
    }

    public static void setTitle(Stage stage, String title) {
        stage.setTitle(title + " | " + SystemAPI.SYSTEM_NAME + " by " + SystemAPI.SYSTEM_AUTHOR);
    }
}
