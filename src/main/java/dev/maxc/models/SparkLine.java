package dev.maxc.models;

import dev.maxc.util.ColorUtils;
import dev.maxc.util.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import static dev.maxc.models.SparkUtils.*;

/**
 * @author Max Carter
 * @since 03/04/2020
 */
public class SparkLine extends Line {
    private static final int SHADOW_RADIUS = 12;

    private Pane pane;
    private int growthDirection;
    private SparkLine previous, next = null;
    private Timeline timeline;

    /**
     * Creates a base spark line
     */
    public SparkLine(Pane pane, int growthDirection, int startX, int startY) {
        super();
        this.pane = pane;
        this.previous = null;
        this.growthDirection = growthDirection;

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
        setStrokeWidth(1);
        DropShadow shadow = new DropShadow(SHADOW_RADIUS, ColorUtils.CORE_FILL);
        shadow.setSpread(0.7);
        setEffect(shadow);

        pane.getChildren().add(this);
        toBack();
    }

    /**
     * The length of the spark line using pythagoras
     */
    public double getLineLength() {
        double x = Math.abs(getStartX() - getEndX());
        double y = Math.abs(getStartY() - getEndY());
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /**
     * Creates a new line node
     */
    private void grow() {
        next = new SparkLine(this.pane, this, getNewDirection(growthDirection));
    }

    /**
     * Translates the node to a new size
     */
    public void translate() {
        if (next != null) {
            next.translate();
            return;
        }

        double length = getLineLength();
        if ((length >= LINE_LENGTH_DIAGONAL - Utils.randomInt(0, 30) && SparkUtils.isDiagonal(growthDirection)) || length >= MAX_LINE_LENGTH || (length >= MIN_LINE_LENGTH && Utils.chance(95))) {
            grow();
            return;
        }

        //out of bounds
        if (Math.abs(getEndX()) >= (double) Utils.WIDTH / 2 || Math.abs(getEndY()) >= (double) Utils.HEIGHT / 2) {
            if (previous != null) {
                //tells all the other nodes to retreat
                previous.retreat();
            }
        } else {
            setEndX(getEndX() + SparkUtils.sparkDirectionMap[growthDirection].getGrowthX());
            setEndY(getEndY() + SparkUtils.sparkDirectionMap[growthDirection].getGrowthY());
        }
    }

    /**
     * Calls the previous node to retreat, if it is the last one it will self delete
     */
    private void retreat() {
        if (previous != null) {
            previous.retreat();
        } else {
            delete();
        }
    }

    /**
     * Deletes the node
     */
    private void delete() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.01), evt -> shorten()));
        timeline.setCycleCount((int) getLineLength());
        timeline.play();
    }

    /**
     * Gradually shortens the node
     */
    private void shorten() {
        setStartX(getStartX() + SparkUtils.sparkDirectionMap[growthDirection].getGrowthX());
        setStartY(getStartY() + SparkUtils.sparkDirectionMap[growthDirection].getGrowthY());
        if (getLineLength() <= 1) {
            timeline.stop();
            pane.getChildren().remove(this);
            if (next != null) {
                next.delete();
            }
        }
    }
}
