package dev.maxc.ui.models;

import dev.maxc.ui.util.ColorUtils;
import dev.maxc.ui.util.UiUtils;
import javafx.geometry.Bounds;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * @author Max Carter
 * @since 11/04/2020
 */
public class FloatingText extends Pane {
    private final Text text;

    /**
     * Creates a new text box in a pane hat floats in an offset.
     */
    public FloatingText(String textContent, double yOffset, int fontSize) {
        text = new Text(textContent);
        text.setFill(ColorUtils.SURFACE_COLOUR);
        text.setFont(UiUtils.getFont(fontSize));
        DropShadow textShadow = new DropShadow(3, ColorUtils.SURFACE_COLOUR);
        text.setEffect(textShadow);
        getChildren().add(text);
        setLayoutY(yOffset);
    }

    public void setOffsetX(double offset) {
        text.setLayoutX(offset);
    }

    private Bounds getBounds() {
        Bounds tb = text.getBoundsInLocal();
        Rectangle stencil = new Rectangle(
                tb.getMinX(), tb.getMinY(), tb.getWidth(), tb.getHeight()
        );
        Shape intersection = Shape.intersect(text, stencil);
        return intersection.getBoundsInLocal();
    }

    public double getTextWidth() {
        return getBounds().getWidth();
    }

    public void centerText() {
        text.setLayoutX(-getTextWidth() / 2);
    }

    public void setText(String textContent) {
        text.setText(textContent);
    }
}
