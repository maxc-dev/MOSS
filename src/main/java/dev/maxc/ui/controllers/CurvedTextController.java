package dev.maxc.ui.controllers;

import dev.maxc.ui.util.UiUtils;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * @author Max Carter
 * @since 03/04/2020
 */
public class CurvedTextController {
    private Text[] chars;
    private final Pane pane;
    private final double radius;
    private final int startingAngle;
    private final Color fill;

    /**
     * RingLines with starting angle and degree
     *
     * @param startingAngle The initial angle of the rings
     */
    public CurvedTextController(String text, Pane pane, double radius, int startingAngle, Color fill) {
        this.pane = pane;
        this.radius = radius;
        this.startingAngle = startingAngle;
        this.fill = fill;
        updateText(text);
    }

    public void sendToFront() {
        for (Text text : chars) {
            text.toFront();
        }
    }

    public void updateText(String text) {
        text = text.toUpperCase();
        chars = new Text[text.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = new Text(text.charAt(i) + "");
            chars[i].setX(radius * Math.cos(Math.toRadians(startingAngle + (i * 7))));
            chars[i].setY(radius * Math.sin(Math.toRadians(startingAngle + (i * 7))));

            chars[i].setRotate(2);
            chars[i].setTextAlignment(TextAlignment.CENTER);
            chars[i].setFont(UiUtils.getFont(25));
            chars[i].setFill(fill);
            chars[i].setStroke(fill);
            chars[i].setStrokeWidth(1.7);
            DropShadow shadow = new DropShadow(8, fill);
            chars[i].setEffect(shadow);

            pane.getChildren().add(chars[i]);
            chars[i].toFront();
        }
    }
}
