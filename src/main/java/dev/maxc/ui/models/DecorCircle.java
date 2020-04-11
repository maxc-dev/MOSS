package dev.maxc.ui.models;

import dev.maxc.sim.system.SystemUtils;
import dev.maxc.ui.util.ColorUtils;
import dev.maxc.ui.util.UiUtils;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * @author Max Carter
 * @since 01/04/2020
 */
public class DecorCircle extends Circle {
    public static final double OFFSET_Y_BASE = 0.3;
    public static final int OFFSET_Y = 4;
    public static final double OFFSET_X_BASE = 0.6;
    public static final int OFFSET_X = 12;

    public static final int RADIUS_MIN = 1;
    public static final int RADIUS_MAX = 2;

    private double counter;
    private final double xOffset;
    private final double yOffset;

    /**
     * Creates a glowing dot on the background
     */
    public DecorCircle(double counter) {
        super(SystemUtils.randomInt(RADIUS_MIN, RADIUS_MAX));
        this.counter = counter;

        xOffset = OFFSET_X_BASE + (double) SystemUtils.randomInt(0, OFFSET_X)/10;
        yOffset = OFFSET_Y_BASE + (double) SystemUtils.randomInt(0, OFFSET_Y)/10;

        setFill(ColorUtils.PRIMARY_ACCENT);
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor((Color) getFill());
        setEffect(shadow);
    }

    /**
     * translates the position of the node
     */
    public void translate() {
        counter+=0.01;
        if (counter >= 360) {
            counter = 0;
        }
        setLayoutX(xOffset*Math.sin(Math.toRadians(counter))*((double) UiUtils.WIDTH/3) + (double) UiUtils.WIDTH/2);
        setLayoutY(yOffset*Math.cos(Math.toRadians(counter))*(UiUtils.HEIGHT) + (double) UiUtils.HEIGHT/2);
    }
}
