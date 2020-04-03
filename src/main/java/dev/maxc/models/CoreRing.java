package dev.maxc.models;

import dev.maxc.util.ColorUtils;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Circle;

/**
 * @author Max Carter
 * @since 03/04/2020
 */
public class CoreRing extends Circle {
    private static final int SHADOW_MIN = 15;
    private static final int SHADOW_EXPANSION = 20;
    private static final int RADIUS_MIN = 120;
    private static final int RADIUS_EXPANSION = 4;

    private double counter = 0;
    private DropShadow shadow = new DropShadow();

    /**
     * Creates the glowing core of the UI
     */
    public CoreRing() {
        super(RADIUS_MIN);
        setFill(ColorUtils.CORE_FILL);
        shadow.setColor(ColorUtils.CORE_FILL);
        shadow.setRadius(SHADOW_MIN);
        setEffect(shadow);
    }

    /**
     * Glows the core
     */
    public void glow() {
        counter+=0.5;
        if (counter == 360) {
            counter = 0;
        }
        setRadius(RADIUS_MIN + RADIUS_EXPANSION*(1+Math.sin(Math.toRadians(counter))));
        shadow.setRadius(SHADOW_MIN + SHADOW_EXPANSION*(1+Math.sin(Math.toRadians(counter))));
    }
}
