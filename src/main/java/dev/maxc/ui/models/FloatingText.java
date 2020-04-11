package dev.maxc.ui.models;

import dev.maxc.sim.logs.Logger;
import dev.maxc.ui.util.ColorUtils;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Max Carter
 * @since 11/04/2020
 */
public class FloatingText extends Pane {
    /**
     * Creates a new text box in a pane hat floats in an offset.
     */
    public FloatingText(String textContent, double xOffset, double yOffset) {
        Text text = new Text(textContent);
        text.setFill(ColorUtils.SURFACE_COLOUR);
        text.setFont(new Font(null, 24));
        DropShadow textShadow = new DropShadow(2, ColorUtils.SURFACE_COLOUR);
        text.setEffect(textShadow);
        getChildren().add(text);
        setLayoutX(xOffset);
        setLayoutY(yOffset);
    }
}
