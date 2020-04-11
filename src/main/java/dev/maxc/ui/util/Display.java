package dev.maxc.ui.util;

/**
 * @author Max Carter
 * @since 11/04/2020
 */
public enum Display {
    SPLASH,
    PRIMARY;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
