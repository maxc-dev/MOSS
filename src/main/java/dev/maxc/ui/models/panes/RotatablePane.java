package dev.maxc.ui.models.panes;

import dev.maxc.ui.models.interfaces.Rotatable;
import dev.maxc.ui.models.interfaces.Spinnable;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

/**
 * @author Max Carter
 * @since 04/04/2020
 */
public class RotatablePane extends Pane implements Rotatable, Spinnable {
    private Rotate rotation;

    protected boolean hovered;
    private final double increment;

    public RotatablePane(int layer) {
        double incrementAbs = 0.1 + (layer * 0.02);
        increment = (layer % 2 != 0 ? incrementAbs : -incrementAbs);
    }

    @Override
    public Rotate getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(Rotate rotation) {
        this.rotation = rotation;
    }

    @Override
    public void spin() {
        if (!hovered) {
            getRotation().setAngle(getRotation().getAngle() + increment * (1.4 + Math.sin(225 + Math.toRadians(getRotation().getAngle()))));
        }
    }
}
