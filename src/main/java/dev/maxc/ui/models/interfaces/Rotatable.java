package dev.maxc.ui.models.interfaces;

import javafx.scene.transform.Rotate;

/**
 * @author Max Carter
 * @since 03/04/2020
 */
public interface Rotatable {
    Rotate getRotation();

    void setRotation(Rotate rotation);
}
