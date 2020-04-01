package dev.maxc.models;

import dev.maxc.util.ColorUtils;
import dev.maxc.util.Utils;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * @author Max Carter
 * @since 01/04/2020
 */
public class DecorCircle extends Circle {
    public static final DecorCircle NULL_CIRCLE = null;
    public static final double OFFSET_Y_BASE = 0.3;
    public static final int OFFSET_Y = 4;
    public static final double OFFSET_X_BASE = 0.6;
    public static final int OFFSET_X = 12;

    public static final int RADIUS_MIN = 1;
    public static final int RADIUS_MAX = 2;

    private double counter, xOffset, yOffset;
    private DecorCircle before, after;

    public DecorCircle(double counter, DecorCircle before, DecorCircle after) {
        super(Utils.randomInt(RADIUS_MIN, RADIUS_MAX));

        xOffset = OFFSET_X_BASE + (double) Utils.randomInt(0, OFFSET_X)/10;
        yOffset = OFFSET_Y_BASE + (double) Utils.randomInt(0, OFFSET_Y)/10;

        this.counter = counter;
        this.before = before;
        this.after = after;

        init();
    }

    public void init() {
        setFill(ColorUtils.PRIMARY_ACCENT);
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(5.0);
        shadow.setOffsetX(5.0);
        shadow.setColor((Color) getFill());
        setEffect(shadow);
    }

    public void transform() {
        counter+=0.01;
        if (counter == 361) {
            counter = 0;
        }
        setLayoutX(xOffset*Math.sin(Math.toRadians(counter))*(Utils.WIDTH/3) + Utils.WIDTH/2);
        setLayoutY(yOffset*Math.cos(Math.toRadians(counter))*(Utils.HEIGHT) + Utils.HEIGHT/2);
    }

    public DecorCircle getPrevious() {
        return before;
    }

    public void setPrevious(DecorCircle before) {
        this.before = before;
    }

    public DecorCircle getNext() {
        return after;
    }

    public void setNext(DecorCircle after) {
        this.after = after;
    }
}
