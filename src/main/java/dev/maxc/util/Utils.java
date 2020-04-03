package dev.maxc.util;

import java.util.Random;

/**
 * @author Max Carter
 * @since 01/04/2020
 */
public class Utils {
    public static final int UNKNOWN_VALUE = -1;

    //TODO(auto generate res)
    public static final int HEIGHT = 864;
    public static final int WIDTH = 4608; //1536 <-- small systems
    public static final int RESOLUTION = WIDTH * HEIGHT;

    public static final String TITLE = "Max OS";

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
}
