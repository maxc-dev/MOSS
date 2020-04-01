package dev.maxc.ui;

import dev.maxc.models.SemiRing;
import dev.maxc.util.ColorUtils;
import dev.maxc.util.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
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
    private AnchorPane anchorPane;
    private String title;

    private Circle base;
    private Pane pane;

    public HeadController(String title, AnchorPane anchorPane) {
        this.title = title;
        this.anchorPane = anchorPane;
        pane = new Pane();
    }

    public void run() {
        base = new Circle(Utils.HEIGHT/4);
        base.setFill(ColorUtils.SURFACE_COLOUR);

        SemiRing ring = new SemiRing(0, 0, 300, 150, ColorUtils.SURFACE_COLOUR, ColorUtils.SURFACE_COLOUR);
        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(ring.layoutXProperty());
        rotation.pivotYProperty().bind(ring.layoutYProperty());

        ring.getTransforms().add(rotation);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(rotation.angleProperty(), 360)));
        timeline.play();

        DropShadow shadow = new DropShadow(30, (Color) base.getFill());
        base.setEffect(shadow);


        pane.setLayoutX(Utils.WIDTH/2);
        pane.setLayoutY(Utils.HEIGHT/2);
        pane.getChildren().add(base);
        pane.getChildren().add(ring);
        anchorPane.getChildren().add(pane);
    }
}
