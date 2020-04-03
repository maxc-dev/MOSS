package dev.maxc.ui;

import dev.maxc.models.*;
import dev.maxc.util.ColorUtils;
import dev.maxc.util.Utils;
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
    private static final int SPARK_LINE_EXTENSION = 90;

    private AnchorPane anchorPane;
    private Pane pane, subPane;
    private SubModule[] subModules;
    private CoreRing coreRing;
    private RingLines ringLines;
    private SparkLine[] sparkLines;

    private volatile State state = State.CLOSED;

    /**
     * Creates a new head controller
     * @param anchorPane the pane to add the controller to
     */
    public HeadController(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
        pane = new Pane();
        subPane = new Pane();
        pane.getChildren().add(subPane);
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
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(3500), pane);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();

        //calls module update event (animations)
        Timeline subRotateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.01), evt -> onModuleUpdateEvent()));
        subRotateTimeline.setCycleCount(Animation.INDEFINITE);
        subRotateTimeline.play();

        //once the controller opens up it will display the sub modules
        fadeTransition.setOnFinished(actionEvent -> {
            Timeline subTimeline = new Timeline(new KeyFrame(Duration.seconds(0.01), evt -> openSubModules()));
            subTimeline.setCycleCount(100);
            subTimeline.play();
        });
    }

    /**
     * Opens the sub modules up
     */
    private void openSubModules() {
        for (SubModule module : subModules) {
            module.setOpacity(module.getOpacity() + 0.01);
            module.setScaleX(module.getScaleX() + 0.01);
            module.setScaleY(module.getScaleY() + 0.01);
        }
    }

    /**
     * When an update event for the module occurs
     */
    private void onModuleUpdateEvent() {
        for (SubModule module : subModules) {
            module.spin();
        }
        ringLines.spin();
        coreRing.glow();

        //TODO(add a bias to the spark lines)

/*        for (int i = 0; i < sparkLines.length; i++) {
            if (pane.getChildren().contains(sparkLines[i])) {
                sparkLines[i].translate();
            } else {
                sparkLines[i] = new SparkLine(pane, SparkUtils.DIRECTION_NORTH_EAST, SPARK_LINE_EXTENSION, -SPARK_LINE_EXTENSION);
            }
        }*/
    }

    /**
     * Initiates the UI
     */
    public void init() {
        int headRadius = Utils.HEIGHT/4;

        //creates base ring (hollow)
        Circle baseRing = new Circle();
        baseRing.setFill(Color.TRANSPARENT);
        baseRing.setStrokeWidth(4);
        baseRing.setStroke(ColorUtils.SURFACE_COLOUR);
        baseRing.setRadius(headRadius);


        //glow effect to ring
        DropShadow shadow = new DropShadow(30, (Color) baseRing.getFill());
        baseRing.setEffect(shadow);

        //inits the sub modules
        subModules = new SubModule[4];
        for (int i = 0; i < subModules.length; i++) {
            subModules[i] = new SubModule("Test" + i, i, headRadius);

            Rotate rotation = new Rotate();
            rotation.pivotXProperty().bind(subModules[i].layoutXProperty());
            rotation.pivotYProperty().bind(subModules[i].layoutYProperty());
            rotation.setAngle(i * ((double) 360 / subModules.length) + Utils.randomInt(0, 360));
            subModules[i].getTransforms().add(rotation);
            subModules[i].setRotation(rotation);

            subModules[i].setScaleX(0);
            subModules[i].setScaleY(0);
            subModules[i].setOpacity(0);
            subPane.getChildren().add(subModules[i]);
        }

/*        sparkLines = new SparkLine[6];
        for (int i = 0; i < sparkLines.length; i++) {
            sparkLines[i] = new SparkLine(pane, i, 0, 0);
        }*/

        //creates the core ring
        coreRing = new CoreRing();

        //creates the ring lines
        ringLines = new RingLines(headRadius - 60, headRadius, ColorUtils.SURFACE_COLOUR, 12);
        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(ringLines.layoutXProperty());
        rotation.pivotYProperty().bind(ringLines.layoutYProperty());
        ringLines.getTransforms().add(rotation);
        ringLines.setRotation(rotation);

        //structures pane
        pane.setLayoutX((double) Utils.WIDTH / 2);
        pane.setLayoutY((double) Utils.HEIGHT / 2);
        pane.getChildren().add(baseRing);
        pane.getChildren().add(ringLines);
        pane.getChildren().add(coreRing);
        anchorPane.getChildren().add(pane);
        open();
    }

    private enum State {
        CLOSED,
        OPENING,
        OPEN,
        CLOSING;
    }

}
