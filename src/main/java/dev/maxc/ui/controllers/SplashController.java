package dev.maxc.ui.controllers;

import dev.maxc.os.bootup.LoadProgressUpdater;
import dev.maxc.os.system.api.SystemAPI;
import dev.maxc.os.system.api.SystemUtils;
import dev.maxc.ui.models.FloatingText;
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
    private static final int SPLASH_RADIUS = 240;
    private static final int SPLASH_PADDING = 8;

    private volatile boolean incrementing = false;
    private volatile double progressArcSpeed = 0.005;
    private double maxLength = 0;
    private final Arc progressArc;
    private final FloatingText progressDisplay;

    private final OnSplashComplete loadingCompleteListener;

    /**
     * Creates a controller for the Splash screen.
     *
     * @param loadingCompleteListener Listens to the when the splash is unloaded
     */
    public SplashController(OnSplashComplete loadingCompleteListener) {
        this.loadingCompleteListener = loadingCompleteListener;

        //creates the main circle
        Circle mainCircle = new Circle(0, 0, SPLASH_RADIUS - SPLASH_PADDING, ColorUtils.BACKGROUND_COLOUR);
        mainCircle.setEffect(new DropShadow((double) SPLASH_PADDING * 0.8, ColorUtils.BACKGROUND_COLOUR));

        //creates an arc which is the loading bar
        int innerPadding = 4;
        progressArc = new Arc(0, 0, SPLASH_RADIUS - SPLASH_PADDING, SPLASH_RADIUS - SPLASH_PADDING, 90, 0);
        progressArc.setFill(ColorUtils.SURFACE_COLOUR);

        //creates the inner circle that overlaps the arc
        Circle innerCircle = new Circle(0, 0, SPLASH_RADIUS - SPLASH_PADDING - innerPadding, ColorUtils.BACKGROUND_COLOUR);

        //creates the text displays
        FloatingText floatingTextTitle = new FloatingText(SystemAPI.SYSTEM_NAME, -20, 100);
        floatingTextTitle.centerText();
        progressDisplay = new FloatingText("Loading...", 10, 14);
        progressDisplay.setOffsetX(-(double) SPLASH_RADIUS * 0.75);

        //adds to pane
        getChildren().add(mainCircle);
        getChildren().add(progressArc);
        getChildren().add(innerCircle);
        getChildren().add(floatingTextTitle);
        getChildren().add(progressDisplay);
    }

    public void incrementProgressBar() {
        if (progressArc.getLength() < maxLength) {
            progressArc.setLength((progressArc.getLength() - maxLength) * 0.5);
            incrementing = true;
            new Timeline(new KeyFrame(Duration.seconds(progressArcSpeed), evt -> incrementProgressBar())).play();
        } else {
            incrementing = false;
        }
    }

    @Override
    public synchronized void onUpdateProgression(String message, double percent) {
        SystemAPI.uiAPI.setTitle(SystemUtils.getRoundedPercent(percent) + "% Loaded");
        progressDisplay.setText(message + " (" + SystemUtils.getRoundedPercent(percent) + "%)");
        maxLength = percent * 360;
        if (!incrementing) {
            incrementProgressBar();
        }
    }

    @Override
    public synchronized void onLoadComplete() {
        SystemAPI.uiAPI.setTitle("Home");
        if (incrementing) {
            maxLength = 360;
            progressArcSpeed = 0.001;
        }

        //animate fade out of the splash
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();

        //when faded out, start loading main ui
        fadeTransition.setOnFinished(actionEvent -> loadingCompleteListener.onSplashComplete());
    }

    public interface OnSplashComplete {
        void onSplashComplete();
    }
}
