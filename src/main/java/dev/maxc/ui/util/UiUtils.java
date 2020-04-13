package dev.maxc.ui.util;

import javafx.scene.text.Font;
import javafx.stage.Screen;

/**
 * @author Max Carter
 * @since 01/04/2020
 */
public class UiUtils {
    public static final double HEIGHT = Screen.getPrimary().getBounds().getHeight(); //864
    public static final double WIDTH = Screen.getPrimary().getBounds().getWidth(); //1536 <-- small systems, 4608 3 monitors
    public static final double RESOLUTION = WIDTH * HEIGHT;

    public static final String DEFAULT_FONT = "Roboto Thin";

    public static Font getFont(int size) {
        return new Font(DEFAULT_FONT, size);
    }

    public static Font getFont() {
        return getFont(20);
    }
}
