package dev.maxc.models;

import dev.maxc.util.Utils;

/**
 * @author Max Carter
 * @since 03/04/2020
 */
public class SparkUtils {
    public static final int DIRECTION_NORTH_EAST = 0;
    public static final int DIRECTION_EAST = 1;
    public static final int DIRECTION_SOUTH_EAST = 2;
    public static final int DIRECTION_SOUTH_WEST = 3;
    public static final int DIRECTION_WEST = 4;
    public static final int DIRECTION_NORTH_WEST = 5;

    public static final double INCREMENT = 2;
    public static final int MIN_LINE_LENGTH = 200;
    public static final int MAX_LINE_LENGTH = 300;
    public static final int LINE_LENGTH_DIAGONAL = 100;

    public static int getNewDirection(int currentDirection) {
        int newDirection;
        switch (currentDirection) {
            case DIRECTION_NORTH_EAST:
            case DIRECTION_SOUTH_EAST:
                newDirection = DIRECTION_EAST;
                break;
            case DIRECTION_NORTH_WEST:
            case DIRECTION_SOUTH_WEST:
                newDirection = DIRECTION_WEST;
                break;
            default:
                newDirection = currentDirection + (Utils.chance(50) ? 1 : -1);
        }
        return newDirection;
    }

    public static boolean isDiagonal(int direction) {
        switch (direction) {
            case DIRECTION_NORTH_EAST:
            case DIRECTION_NORTH_WEST:
            case DIRECTION_SOUTH_EAST:
            case DIRECTION_SOUTH_WEST:
                return true;
            default:
                return false;
        }
    }

    public static SparkPoint[] sparkDirectionMap = new SparkPoint[]{
            new SparkPoint(INCREMENT, -INCREMENT),
            new SparkPoint(INCREMENT, 0),
            new SparkPoint(INCREMENT, INCREMENT),

            new SparkPoint(-INCREMENT, INCREMENT),
            new SparkPoint(-INCREMENT, 0),
            new SparkPoint(-INCREMENT, -INCREMENT)
    };

    protected static final class SparkPoint {
        private double xGrowth, yGrowth;

        public SparkPoint(double xGrowth, double yGrowth) {
            this.xGrowth = xGrowth;
            this.yGrowth = yGrowth;
        }

        public double getGrowthX() {
            return xGrowth;
        }

        public double getGrowthY() {
            return yGrowth;
        }
    }

}
