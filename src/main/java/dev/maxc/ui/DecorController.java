package dev.maxc.ui;

import dev.maxc.models.DecorCircle;
import dev.maxc.util.Utils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class DecorController {
    public static final double UPDATE_DECOR = 0.01;
    public static final int CIRCLE_COUNT = Utils.RESOLUTION/35000;

    private static DecorCircle[] decorCircles;

    private AnchorPane anchorPane;

    public DecorController(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public void run() {
        decorCircles = new DecorCircle[CIRCLE_COUNT];
        for (int i = 0; i < decorCircles.length; i++) {
            DecorCircle before = (i == 0 ? DecorCircle.NULL_CIRCLE : decorCircles[i-1]);
            DecorCircle after = (i == decorCircles.length-1 ? decorCircles[0] : decorCircles[i+1]);
            decorCircles[i] = new DecorCircle((double) (i*360)/decorCircles.length, before, after);
            decorCircles[i].toFront();
        }
        decorCircles[0].setPrevious(decorCircles[decorCircles.length-1]);
        anchorPane.getChildren().addAll(decorCircles);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(UPDATE_DECOR), evt -> updateDecor()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateDecor() {
        for (DecorCircle decorCircle : decorCircles) {
            decorCircle.transform();
        }
    }
}
