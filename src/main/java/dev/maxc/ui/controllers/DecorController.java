package dev.maxc.ui.controllers;

import dev.maxc.ui.models.DecorCircle;
import dev.maxc.ui.util.UiUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class DecorController {
    public static final double UPDATE_DECOR = 0.01;
    public static final int CIRCLE_COUNT = (int) UiUtils.RESOLUTION/35000;

    private static DecorCircle[] decorCircles;

    /**
     * Creates the controller for the decor circles
     * @param anchorPane background to add the decor to
     */
    public DecorController(AnchorPane anchorPane) {
        decorCircles = new DecorCircle[CIRCLE_COUNT];
        for (int i = 0; i < decorCircles.length; i++) {
            decorCircles[i] = new DecorCircle((double) (i*360)/decorCircles.length);
            decorCircles[i].toFront();
        }
        anchorPane.getChildren().addAll(decorCircles);
    }

    /**
     * Starts to display all the decor circles
     */
    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(UPDATE_DECOR), evt -> updateDecor()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * updates all the decor circle positions
     */
    private void updateDecor() {
        for (DecorCircle decorCircle : decorCircles) {
            decorCircle.translate();
        }
    }
}
