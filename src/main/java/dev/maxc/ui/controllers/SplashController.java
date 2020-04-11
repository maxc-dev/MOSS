package dev.maxc.ui.controllers;

import dev.maxc.sim.bootup.LoadProgressUpdater;
import dev.maxc.sim.logs.Logger;
import dev.maxc.ui.util.ColorUtils;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * @author Max Carter
 * @since 11/04/2020
 */
public class SplashController extends Pane implements LoadProgressUpdater {
    private static final int SPLASH_RADIUS = 200;
    private static final int SPLASH_PADDING = 10;

    private int percentIndex = 0;
    private final Arc progressArc;

    private final OnSplashComplete loadingCompleteListener;
    private final HeadController headController;

    /**
     * Creates a controller for the Splash screen.
     *
     * @param loadingCompleteListener Listens to the when the splash is unloaded
     * @param headController          Head controller to pass to the complete listener
     */
    public SplashController(OnSplashComplete loadingCompleteListener, HeadController headController) {
        this.loadingCompleteListener = loadingCompleteListener;
        this.headController = headController;

        //creates the main circle
        Circle mainCircle = new Circle(SPLASH_RADIUS, SPLASH_RADIUS, SPLASH_RADIUS - SPLASH_PADDING, ColorUtils.BACKGROUND_COLOUR);
        mainCircle.setEffect(new DropShadow((double) SPLASH_PADDING * 0.8, ColorUtils.BACKGROUND_COLOUR));

        //creates an arc which is the loading bar
        int innerPadding = 4;
        progressArc = new Arc(SPLASH_RADIUS, SPLASH_RADIUS, SPLASH_RADIUS - SPLASH_PADDING, SPLASH_RADIUS - SPLASH_PADDING, 90, 90);
        progressArc.setFill(ColorUtils.SURFACE_COLOUR);

        //creates the inner circle that overlaps the arc
        Circle innerCircle = new Circle(SPLASH_RADIUS, SPLASH_RADIUS, SPLASH_RADIUS - SPLASH_PADDING - innerPadding, ColorUtils.BACKGROUND_COLOUR);

        //adds to pane
        getChildren().add(mainCircle);
        getChildren().add(progressArc);
        getChildren().add(innerCircle);
    }

    @Override
    public synchronized void onUpdateProgression(String message, double percent, String timeLeft) {
        Logger.log("Updating splash screen for: " + message + " \t(" + percent + "%)");
        //animates a change from the previous percent to the new one
        Timeline subRotateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.01), evt -> move(percent)));
        subRotateTimeline.setCycleCount(20);
        subRotateTimeline.play();
    }

    /**
     * Increases the loading bar
     *
     * @param increment How much to increase it by
     */
    private synchronized void move(double increment) {
        if (percentIndex < 20) {
            percentIndex++;
            progressArc.setLength((increment * 3.60) + (double) percentIndex / 20);
        } else {
            percentIndex = 0;
        }
    }

    @Override
    public synchronized void onLoadComplete() {
        //animate fade out of the splash
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();

        //when faded out, start loading main ui
        fadeTransition.setOnFinished(actionEvent -> loadingCompleteListener.onSplashComplete(headController));
    }

    public interface OnSplashComplete {
        void onSplashComplete(HeadController headController);
    }
}
