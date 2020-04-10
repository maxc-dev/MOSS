package dev.maxc.ui.controllers;

import dev.maxc.sim.bootup.system.SystemUtils;
import dev.maxc.ui.models.CoreRing;
import dev.maxc.ui.models.RingLines;
import dev.maxc.ui.models.panes.RotatablePane;
import dev.maxc.ui.models.spark.SparkLine;
import dev.maxc.ui.models.spark.SparkUtils;
import dev.maxc.ui.models.panes.SubModule;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
    private RotatablePane[] rotatableModules;
    private CoreRing coreRing;
    private RingLines ringLines, coreRingLines;
    private SparkLine[] sparkLines;

    public String[] titles = new String[]{ "Terminal", "Settings", "Files", "Exit" };

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
        for (RotatablePane rotatablePane : rotatableModules) {
            rotatablePane.setOpacity(rotatablePane.getOpacity() + 0.01);
            rotatablePane.setScaleX(rotatablePane.getScaleX() + 0.01);
            rotatablePane.setScaleY(rotatablePane.getScaleY() + 0.01);
        }
    }

    /**
     * When an update event for the module occurs
     */
    private void onModuleUpdateEvent() {
        for (RotatablePane modulesRotatable : rotatableModules) {
            modulesRotatable.spin();
        }
        ringLines.spin();
        coreRing.glow();
        coreRingLines.translate();

        for (int i = 0; i < sparkLines.length; i++) {
            if (pane.getChildren().contains(sparkLines[i])) {
                sparkLines[i].translate();
            } else if (sparkLines[i].getChainLength() <= 1) {
                sparkLines[i] = new SparkLine(pane, i, (int) SparkUtils.sparkStartingMap[i].getX(), (int) SparkUtils.sparkStartingMap[i].getY());
            }
        }
    }

    /**
     * Initiates the UI
     */
    public void init() {
        int headRadius = UiUtils.HEIGHT / 4;

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
        SubModule[] subModules = new SubModule[titles.length];
        rotatableModules = new RotatablePane[subModules.length];
        for (int i = 0; i < subModules.length; i++) {
            rotatableModules[i] = new RotatablePane(i);
            subModules[i] = new SubModule(rotatableModules[i], i, headRadius);
            CurvedText text = new CurvedText(titles[i], rotatableModules[i], subModules[i].getInnerRadius() + 10, 275, ColorUtils.SURFACE_COLOUR);

            Rotate rotation = new Rotate();
            rotation.pivotXProperty().bind(rotatableModules[i].layoutXProperty());
            rotation.pivotYProperty().bind(rotatableModules[i].layoutYProperty());
            rotation.setAngle(i * ((double) 360 / subModules.length) + SystemUtils.randomInt(0, 360));
            rotatableModules[i].getTransforms().add(rotation);
            rotatableModules[i].setRotation(rotation);

            rotatableModules[i].getChildren().add(subModules[i]);
            RingLines lines = new RingLines(subModules[i].getInnerRadius(), subModules[i].getOuterRadius(), 0, 30, ColorUtils.SURFACE_COLOUR, 15, 14);
            RingLines lines2 = new RingLines(subModules[i].getInnerRadius(), subModules[i].getOuterRadius(), 180, 60, ColorUtils.SURFACE_COLOUR, 15, 28);
            rotatableModules[i].getChildren().add(lines);
            rotatableModules[i].getChildren().add(lines2);

            rotatableModules[i].setScaleX(0);
            rotatableModules[i].setScaleY(0);
            rotatableModules[i].setOpacity(0);
            text.sendToFront();
            subPane.getChildren().add(rotatableModules[i]);
        }

        sparkLines = new SparkLine[6];
        for (int i = 0; i < sparkLines.length; i++) {
            sparkLines[i] = new SparkLine(pane, i, (int) SparkUtils.sparkStartingMap[i].getX(), (int) SparkUtils.sparkStartingMap[i].getY());
        }

        //creates the core ring
        coreRing = new CoreRing();
        coreRingLines = new RingLines(coreRing.getRadius() - 45, coreRing.getRadius(), 0, 360, ColorUtils.CORE_FILL, 10, 16);

        Text title = new Text("J.A.R.V.I.S");
        title.setFill(ColorUtils.SURFACE_COLOUR);
        title.setFont(new Font(null, 30));
        DropShadow textShadow = new DropShadow(4, ColorUtils.SURFACE_COLOUR);
        title.setEffect(textShadow);
        title.setX(-62);
        title.setY(-2);

        Text author = new Text("Max Carter");
        author.setFill(ColorUtils.SURFACE_COLOUR);
        author.setFont(new Font(null, 20));
        textShadow = new DropShadow(2, ColorUtils.SURFACE_COLOUR);
        author.setEffect(textShadow);
        author.setX(-48);
        author.setY(18);

        //creates the ring lines
        ringLines = new RingLines(headRadius - 60, headRadius, ColorUtils.SURFACE_COLOUR, 12, 40);
        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(ringLines.layoutXProperty());
        rotation.pivotYProperty().bind(ringLines.layoutYProperty());
        ringLines.getTransforms().add(rotation);
        ringLines.setRotation(rotation);

        //structures pane
        pane.setLayoutX((double) UiUtils.WIDTH / 2);
        pane.setLayoutY((double) UiUtils.HEIGHT / 2);
        pane.getChildren().add(baseRing);
        pane.getChildren().add(ringLines);
        pane.getChildren().add(coreRing);
        pane.getChildren().add(coreRingLines);
        pane.getChildren().add(title);
        pane.getChildren().add(author);
        anchorPane.getChildren().add(pane);
        open();
    }

    private enum State {
        CLOSED,
        OPENING,
        OPEN,
        CLOSING
    }

}
