package dev.maxc.ui.controllers;

import dev.maxc.ui.models.spark.SparkLine;
import dev.maxc.ui.models.spark.SparkUtils;
import javafx.scene.layout.Pane;

/**
 * @author Max Carter
 * @since 11/04/2020
 */
public class SparkLineController {
    private final SparkLine[] sparkLines;
    private final Pane pane;

    /**
     * Creates a new SparkLineController which controls the spark lines on the main display.
     *
     * @param pane The pane to add the spark lines to.
     * @param size The amount of spark lines to add.
     */
    public SparkLineController(Pane pane, int size) {
        sparkLines = new SparkLine[size];
        this.pane = pane;
        for (int i = 0; i < size; i++) {
            sparkLines[i] = new SparkLine(pane, i, (int) SparkUtils.sparkStartingMap[i].getX(), (int) SparkUtils.sparkStartingMap[i].getY());
        }
    }

    public void translate() {
        for (int i = 0; i < sparkLines.length; i++) {
            if (pane.getChildren().contains(sparkLines[i])) {
                sparkLines[i].translate();
            } else if (sparkLines[i].getChainLength() <= 1) {
                sparkLines[i] = new SparkLine(pane, i, (int) SparkUtils.sparkStartingMap[i].getX(), (int) SparkUtils.sparkStartingMap[i].getY());
            }
        }
    }
}
