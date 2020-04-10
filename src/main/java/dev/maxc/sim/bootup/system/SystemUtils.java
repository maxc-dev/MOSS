package dev.maxc.sim.bootup.system;

import java.util.Random;

/**
 * @author Max Carter
 * @since 10/04/2020
 */
public class SystemUtils {
    /*
        This class provides utility resources to application programs
        and programs within the kernel.
     */

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
