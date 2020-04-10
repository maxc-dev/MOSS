package dev.maxc.ui.models;

import dev.maxc.ui.models.interfaces.Rotatable;
import dev.maxc.ui.models.interfaces.Spinnable;
import dev.maxc.ui.util.ColorUtils;
import javafx.scene.effect.DropShadow;
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

    private double translationCounter = 0;

    private double innerRadius, outerRadius;
    private Line[] lines;
    private int startingAngle = 180, degree = 360;
    private Rotate rotation;

    /**
     * Creates a list of lines in a ring formation
     *
     * @param innerRadius gap to leave in the middle
     * @param outerRadius max length of the lines
     * @param fill        colour fill of the lines
     * @param padding     padding between the top and bottom of the lines
     * @param density     The amount of lines
     */
    public RingLines(double innerRadius, double outerRadius, Color fill, int padding, int density) {
        init(innerRadius, outerRadius, fill, padding, density);
    }

    /**
     * RingLines with starting angle and degree
     *
     * @param degrees       The degree of the rings, 0 < x < 360
     * @param startingAngle The initial angle of the rings
     */
    public RingLines(double innerRadius, double outerRadius, int startingAngle, int degrees, Color fill, int padding, int density) {
        this.startingAngle = startingAngle;
        this.degree = degrees;
        init(innerRadius, outerRadius, fill, padding, density);
    }

    private void init(double innerRadius, double outerRadius, Color fill, int padding, int density) {
        lines = new Line[density];
        innerRadius += padding;
        outerRadius -= padding;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;

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
            DropShadow shadow = new DropShadow(6, ColorUtils.SURFACE_COLOUR);
            setEffect(shadow);

            getChildren().add(lines[i]);
        }
    }

    public void translate() {
        translationCounter += 0.5;
        if (translationCounter >= 360) {
            translationCounter = 0;
        }
        changeSize(innerRadius + (10 * (Math.sin(Math.toRadians(translationCounter)))),
                outerRadius + (5 * (Math.sin(Math.toRadians(translationCounter)))));
    }

    private void changeSize(double innerRadius, double outerRadius) {
        for (int i = 0; i < lines.length; i++) {
            lines[i].setStartX(innerRadius * cos(i, lines.length)); //startx
            lines[i].setStartY(innerRadius * sin(i, lines.length)); //starty
            lines[i].setEndX(outerRadius * cos(i, lines.length)); //endx
            lines[i].setEndY(outerRadius * sin(i, lines.length)); //endy
        }
    }

    /**
     * Performs a cosine function for the X position of the line
     */
    private double cos(int i, int length) {
        return Math.cos(Math.toRadians(startingAngle + (i * ((double) degree / length))));
    }

    /**
     * Performs a sine function for the Y position of the line
     */
    private double sin(int i, int length) {
        return Math.sin(Math.toRadians(startingAngle + (i * ((double) degree / length))));
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
