package dev.maxc.models;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

/**
 * @author Max Carter
 * @since 03/04/2020
 */
public class RingLines extends Pane implements Rotatable, Spinnable {
    private static final double SPIN_DEGREE = 0.02;

    private Rotate rotation;

    /**
     * Creates a list of lines in a ring formation
     *
     * @param innerRadius gap to leave in the middle
     * @param outerRadius max length of the lines
     * @param fill        colour fill of the lines
     * @param padding     padding between the top and bottom of the lines
     */
    public RingLines(double innerRadius, double outerRadius, Color fill, int padding) {
        Line[] lines = new Line[40];
        innerRadius += padding;
        outerRadius -= padding;

        for (int i = 0; i < lines.length; i++) { //cos 180 -> 360
            lines[i] = new Line(
                    innerRadius * cos(i, lines.length), //startx
                    innerRadius * sin(i, lines.length), //starty
                    outerRadius * cos(i, lines.length), //endx
                    outerRadius * sin(i, lines.length) //endy
            );
            //customise line and add to super-pane
            lines[i].setFill(fill);
            lines[i].setStroke(fill);
            lines[i].setStrokeWidth(3);
            getChildren().add(lines[i]);
        }
    }

    /**
     * Performs a cosine function for the X position of the line
     */
    private double cos(int i, int length) {
        return Math.cos(Math.toRadians(180 + (i * ((double) 360 / length))));
    }

    /**
     * Performs a sine function for the Y position of the line
     */
    private double sin(int i, int length) {
        return Math.sin(Math.toRadians(180 + (i * ((double) 360 / length))));
    }

    /**
     * The rotation object of the pane
     */
    @Override
    public Rotate getRotation() {
        return rotation;
    }

    /**
     * Sets the ring rotation object
     * @param rotation
     */
    @Override
    public void setRotation(Rotate rotation) {
        this.rotation = rotation;
    }

    /**
     * Spins the object
     */
    @Override
    public void spin() {
        double angle = getRotation().getAngle();
        if (angle > 360) {
            getRotation().setAngle(0);
        }
        angle += SPIN_DEGREE;
        getRotation().setAngle(angle);
    }
}
