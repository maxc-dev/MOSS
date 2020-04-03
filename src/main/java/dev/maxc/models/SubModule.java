package dev.maxc.models;

import dev.maxc.util.ColorUtils;
import javafx.scene.Cursor;

/**
 * @author Max Carter
 * @since 02/04/2020
 */
public class SubModule extends SemiRing implements Spinnable {
    private static final int LAYER_THICKNESS = 48;
    private static final int LAYER_OVERLAP = 16;

    private boolean clockwise;
    private boolean hovered;

    private String title;

    /**
     * Creates a submodule which is a menu option on the UI core menu
     */
    public SubModule(String title, int layer, int headRadius) {
        super(0, 0, headRadius + (layer * LAYER_THICKNESS) + LAYER_OVERLAP + LAYER_THICKNESS, headRadius + (layer * LAYER_THICKNESS), ColorUtils.LOOSE_SURFACE_COLOUR, ColorUtils.SURFACE_COLOUR);
        setCursor(Cursor.HAND);
        this.title = title;
        clockwise = layer % 2 != 0;

        /*
            TODO(write text on the label for `title`)
         */

        setOnMouseEntered(t -> hovered = true);
        setOnMouseExited(t -> hovered = false);
    }

    @Override
    public void spin() {
        if (!hovered) {
            getRotation().setAngle(getRotation().getAngle() + (clockwise ? 0.1 : -0.1));
        }
    }
}
