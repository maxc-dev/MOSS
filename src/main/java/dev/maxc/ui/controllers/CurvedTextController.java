package dev.maxc.ui.controllers;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * @author Max Carter
 * @since 03/04/2020
 */
public class CurvedTextController {
    private Text[] chars;

    /**
     * RingLines with starting angle and degree
     *
     * @param startingAngle The initial angle of the rings
     */
    public CurvedTextController(String text, Pane pane, double radius, int startingAngle, Color fill) {
        text = text.toUpperCase();
        chars = new Text[text.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = new Text(text.charAt(i) + "");
            chars[i].setX(radius * Math.cos(Math.toRadians(startingAngle + (i * 7))));
            chars[i].setY(radius * Math.sin(Math.toRadians(startingAngle + (i * 7))));

            chars[i].setRotate(2);
            chars[i].setTextAlignment(TextAlignment.CENTER);
            chars[i].setFont(new Font(null, 25));
            chars[i].setFill(fill);
            chars[i].setStroke(fill);
            chars[i].setStrokeWidth(1.7);
            DropShadow shadow = new DropShadow(8, fill);
            chars[i].setEffect(shadow);

            pane.getChildren().add(chars[i]);
            chars[i].toFront();
        }
    }

    public void sendToFront() {
        for (Text text : chars) {
            text.toFront();
        }
    }

    /**
     * Gets the tangent at t
     *
     * K, R, V all constants
     * t is the index of the char in the string
     *
     * formula:
     * x = K * cos(R + (t * V))
     * y = K * sin(R + (t * V))
     *
     * dx/dt = -sin(R + (t * V)) * t
     * dy/dt = cos(R + (t * V)) * t
     *
     * result = t*cos(R + (t * V)) / -t*sin(R + (t * V)) = -cot(R + (t * V))
     */
    @Deprecated
    private double getTangent(double R, double t, double V) {
        return Math.cos(R + (t * V))/-Math.sin(R + (t * V));
    }
}
