package dev.maxc.ui.models.spark;

import dev.maxc.sim.system.SystemUtils;
import dev.maxc.ui.util.ColorUtils;
import dev.maxc.ui.util.UiUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import static dev.maxc.ui.models.spark.SparkUtils.*;

/**
 * @author Max Carter
 * @since 03/04/2020
 */
public class SparkLine extends Line {
    private static final int SHADOW_RADIUS = 12;
    private static final int CHANCE_SPLIT = 8;

    public int maxChainLength;

    private final Pane pane;
    private final int growthDirection;
    private final int biasDirection;
    private SparkLine previous, next = null, next2 = null;
    private Timeline timeline;

    /**
     * Creates a base spark line
     */
    public SparkLine(Pane pane, int growthDirection, int startX, int startY) {
        super();
        this.pane = pane;
        this.previous = null;
        this.growthDirection = growthDirection;
        this.biasDirection = growthDirection;
        this.maxChainLength = SystemUtils.randomInt(10, 20);

        setStartX(startX);
        setStartY(startY);
        setEndX(startX);
        setEndY(startY);

        init();
    }

    /**
     * Creates a linked spark line node
     */
    public SparkLine(Pane pane, SparkLine previous, int growthDirection) {
        super();
        this.pane = pane;
        this.previous = previous;
        this.growthDirection = growthDirection;
        this.biasDirection = previous.biasDirection;
        this.maxChainLength = previous.maxChainLength;

        setStartX(previous.getEndX());
        setStartY(previous.getEndY());
        setEndX(previous.getEndX());
        setEndY(previous.getEndY());

        init();
    }

    /**
     * Initiates all the variables
     */
    private void init() {
        setStroke(ColorUtils.CORE_FILL_SLIGHT);
        setStrokeWidth(0.4);
        DropShadow shadow = new DropShadow(SHADOW_RADIUS, ColorUtils.CORE_FILL);
        shadow.setSpread(0.7);
        setEffect(shadow);

        pane.getChildren().add(this);
        toBack();
    }

    /**
     * The length of the spark line using pythagoras
     */
    private double getLineLength() {
        double x = Math.abs(getStartX() - getEndX());
        double y = Math.abs(getStartY() - getEndY());
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public int getChainLength() {
        int leftNodes = 0;
        int rightNodes = 0;

        SparkLine leftSpark = this;
        SparkLine rightSpark = next;

        while (leftSpark != null) {
            if (!pane.getChildren().contains(leftSpark)) {
                leftSpark = null;
            } else {
                leftNodes++;
                leftSpark = leftSpark.previous;
            }
        }

        while (rightSpark != null) {
            if (!pane.getChildren().contains(rightSpark)) {
                rightSpark = null;
            } else {
                rightNodes++;
                rightSpark = rightSpark.previous;
            }
        }

        return leftNodes + rightNodes;
    }

    /**
     * Creates a new line node
     */
    private void grow() {
        int newDirection = getNewDirection(growthDirection, biasDirection);
        if (SystemUtils.chance(CHANCE_SPLIT) && !isDiagonal(growthDirection)) {
            next2 = new SparkLine(this.pane, this, getAdjacentDirection(newDirection));
        }
        next = new SparkLine(this.pane, this, newDirection);
    }

    /**
     * Translates the node to a new size
     */
    public void translate() {
        if (next != null) {
            next.translate();
            if (next2 != null) {
                next2.translate();
            }
            return;
        }

        double length = getLineLength();
        if ((length >= LINE_LENGTH_DIAGONAL - SystemUtils.randomInt(0, 50) && SparkUtils.isDiagonal(growthDirection)) || length >= MAX_LINE_LENGTH || (length >= MIN_LINE_LENGTH && SystemUtils.chance(86))) {
            grow();
            return;
        }

        //out of bounds
        if (Math.abs(getEndX()) >= (double) UiUtils.WIDTH / 2 || Math.abs(getEndY()) >= (double) UiUtils.HEIGHT / 2 || getChainLength() >= maxChainLength) {
            if (previous != null) {
                //tells all the other nodes to retreat
                previous.traverse();
            }
        }
        setEndX(getEndX() + SparkUtils.sparkDirectionMap[growthDirection].getX());
        setEndY(getEndY() + SparkUtils.sparkDirectionMap[growthDirection].getY());
        toBack();
    }

    /**
     * Calls the previous node to retreat, if it is the last one it will self delete
     */
    private void traverse() {
        if (previous != null) {
            previous.traverse();
        } else {
            delete();
        }
    }

    /**
     * Deletes the node
     */
    private void delete() {
        if (previous != null) {
            previous = null;
        }
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.005), evt -> shorten()));
        timeline.setCycleCount((int) getLineLength());
        timeline.play();
    }

    /**
     * Gradually shortens the node
     */
    private void shorten() {
        setStartX(getStartX() + SparkUtils.sparkDirectionMap[growthDirection].getX());
        setStartY(getStartY() + SparkUtils.sparkDirectionMap[growthDirection].getY());
        if (getLineLength() < 1) {
            timeline.stop();
            pane.getChildren().remove(this);
            if (next != null) {
                next.delete();
                if (next2 != null) {
                    next2.delete();
                }
            }
        }
    }
}
