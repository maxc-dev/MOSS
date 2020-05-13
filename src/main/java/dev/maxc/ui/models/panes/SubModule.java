package dev.maxc.ui.models.panes;

import dev.maxc.ui.models.SemiRing;
import dev.maxc.ui.util.ColorUtils;
import javafx.scene.Cursor;

/**
 * @author Max Carter
 * @since 02/04/2020
 */
public class SubModule extends SemiRing {
    private static final int LAYER_THICKNESS = 40;
    private static final int LAYER_OVERLAP = 0;

    private final double innerRadius;
    private final double outerRadius;

    /**
     * Creates a submodule which is a menu option on the UI core menu
     */
    public SubModule(RotatablePane pane, int layer, int headRadius) {
        super(0, 0, headRadius + (layer * LAYER_THICKNESS) + LAYER_OVERLAP + LAYER_THICKNESS, headRadius + (layer * LAYER_THICKNESS), ColorUtils.SURFACE_COLOUR);
        setCursor(Cursor.HAND);

        this.outerRadius = headRadius + (layer * LAYER_THICKNESS) + LAYER_OVERLAP + LAYER_THICKNESS;
        this.innerRadius = headRadius + (layer * LAYER_THICKNESS);

        super.setOnMouseEntered(t -> pane.hovered = true);
        super.setOnMouseExited(t -> pane.hovered = false);
        super.setOnMouseClicked(t -> System.out.println("MOUSE CLICK YEET"));
    }

    public double getOuterRadius() {
        return outerRadius;
    }

    public double getInnerRadius() {
        return innerRadius;
    }

}
