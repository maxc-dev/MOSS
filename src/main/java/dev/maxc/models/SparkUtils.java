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

    public static final double INCREMENT = 1;
    public static final int MIN_LINE_LENGTH = 150;
    public static final int MAX_LINE_LENGTH = 280;
    public static final int LINE_LENGTH_DIAGONAL = 100;
    public static final int SPARK_LINE_EXTENSION = 90;

    public static int getNewDirection(int currentDirection, int biasDirection) {
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
                newDirection = getBiasDirection(currentDirection, biasDirection);
        }
        return newDirection;
    }

    private static int getBiasDirection(int currentDirection, int biasDirection) {
        if (biasDirection == currentDirection) {
            return currentDirection + (Utils.chance(50) ? 1 : -1);
        } else {
            return biasDirection;
        }
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

    public static int getAdjacentDirection(int direction) {
        switch (direction) {
            case DIRECTION_NORTH_EAST:
                return DIRECTION_SOUTH_EAST;
            case DIRECTION_SOUTH_EAST:
                return DIRECTION_NORTH_EAST;
            case DIRECTION_NORTH_WEST:
                return DIRECTION_SOUTH_WEST;
            case DIRECTION_SOUTH_WEST:
                return DIRECTION_NORTH_WEST;
            default:
                return direction;
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

    public static SparkPoint[] sparkStartingMap = new SparkPoint[]{
            new SparkPoint(SPARK_LINE_EXTENSION, -SPARK_LINE_EXTENSION),
            new SparkPoint(SPARK_LINE_EXTENSION, 0),
            new SparkPoint(SPARK_LINE_EXTENSION, SPARK_LINE_EXTENSION),

            new SparkPoint(-SPARK_LINE_EXTENSION, SPARK_LINE_EXTENSION),
            new SparkPoint(-SPARK_LINE_EXTENSION, 0),
            new SparkPoint(-SPARK_LINE_EXTENSION, -SPARK_LINE_EXTENSION)
    };

    public static final class SparkPoint {
        private double x, y;

        public SparkPoint(double xGrowth, double yGrowth) {
            this.x = xGrowth;
            this.y = yGrowth;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

}
