package dev.maxc.ui.controllers;

import dev.maxc.sim.bootup.system.SystemAPI;
import dev.maxc.ui.models.CoreRing;
import dev.maxc.ui.models.FloatingText;
import dev.maxc.ui.models.RingLines;
import dev.maxc.ui.util.ColorUtils;
import dev.maxc.ui.util.UiUtils;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * @author Max Carter
 * @since 01/04/2020
 */
public class HeadController {
    private final AnchorPane anchorPane;
    private final Pane pane;
    private final Pane subPane;
    private CoreRing coreRing;
    private RingLines ringLines, coreRingLines;

    private ModuleController moduleController;
    private SparkLineController sparkLineController;

    private volatile State state = State.CLOSED;

    /**
     * Creates a new head controller
     *
     * @param anchorPane the pane to add the controller to
     */
    public HeadController(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        pane = new Pane();
        subPane = new Pane();
        pane.getChildren().add(subPane);
    }

    /**
     * Initiates the UI
     */
    public void init() {
        int headRadius = (int) UiUtils.HEIGHT / 4;

        //creates base ring (hollow)
        Circle baseRing = new Circle();
        baseRing.setFill(Color.TRANSPARENT);
        baseRing.setStrokeWidth(4);
        baseRing.setStroke(ColorUtils.SURFACE_COLOUR);
        baseRing.setRadius(headRadius);

        //glow effect to ring
        DropShadow shadow = new DropShadow(10, ColorUtils.SURFACE_COLOUR);
        baseRing.setEffect(shadow);

        //inits the sub modules
        moduleController = new ModuleController();
        moduleController.init(headRadius);
        subPane.getChildren().addAll(moduleController.getRotatablePanes());

        //inits spark lines
        sparkLineController = new SparkLineController(pane, 6);

        //creates the core ring
        coreRing = new CoreRing();
        coreRingLines = new RingLines(coreRing.getRadius() - 45, coreRing.getRadius(), 0, 360, ColorUtils.CORE_FILL, 10, 16);

        //creates the ring lines
        ringLines = new RingLines(headRadius - 60, headRadius, ColorUtils.SURFACE_COLOUR, 12, 40);
        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(ringLines.layoutXProperty());
        rotation.pivotYProperty().bind(ringLines.layoutYProperty());
        ringLines.getTransforms().add(rotation);
        ringLines.setRotation(rotation);

        FloatingText floatingTextTitle = new FloatingText(SystemAPI.SYSTEM_NAME, -42, -3);
        FloatingText floatingTextAuthor = new FloatingText(SystemAPI.SYSTEM_AUTHOR, -56, 20);

        //structures pane
        pane.setLayoutX(UiUtils.WIDTH / 2);
        pane.setLayoutY(UiUtils.HEIGHT / 2);
        pane.getChildren().add(baseRing);
        pane.getChildren().add(ringLines);
        pane.getChildren().add(coreRing);
        pane.getChildren().add(coreRingLines);
        pane.getChildren().add(floatingTextTitle);
        pane.getChildren().add(floatingTextAuthor);
        anchorPane.getChildren().add(pane);
        open();
    }

    /**
     * Opens the controller UI up
     */
    public void open() {
        if (state != State.CLOSED) {
            return;
        }
        state = State.OPENING;

        //fades in the opening of the controller
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(4000), pane);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();

        //calls module update event (animations)
        Timeline subRotateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.01), evt -> onModuleUpdateEvent()));
        subRotateTimeline.setCycleCount(Animation.INDEFINITE);
        subRotateTimeline.play();

        //once the controller opens up it will display the sub modules
        fadeTransition.setOnFinished(actionEvent -> {
            Timeline subTimeline = new Timeline(new KeyFrame(Duration.seconds(0.01), evt -> moduleController.open()));
            subTimeline.setCycleCount(100);
            subTimeline.play();
        });
    }

    /**
     * When an update event for the module occurs
     */
    private void onModuleUpdateEvent() {
        moduleController.spin();
        ringLines.spin();
        coreRing.glow();
        coreRingLines.translate();
        sparkLineController.translate();
    }

    private enum State {
        CLOSED,
        OPENING,
        OPEN,
        CLOSING
    }
}
