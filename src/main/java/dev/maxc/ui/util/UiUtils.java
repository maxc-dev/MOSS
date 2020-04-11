package dev.maxc.ui.util;

import javafx.stage.Screen;

/**
 * @author Max Carter
 * @since 01/04/2020
 */
public class UiUtils {
    public static final double HEIGHT = Screen.getPrimary().getBounds().getHeight(); //864
    public static final double WIDTH = Screen.getPrimary().getBounds().getWidth(); //1536 <-- small systems, 4608 3 monitors
    public static final double RESOLUTION = WIDTH * HEIGHT;
}
