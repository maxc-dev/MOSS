package dev.maxc.ui.controllers;

import dev.maxc.sim.system.SystemUtils;
import dev.maxc.ui.models.RingLines;
import dev.maxc.ui.models.panes.RotatablePane;
import dev.maxc.ui.models.panes.SubModule;
import dev.maxc.ui.util.ColorUtils;
import javafx.scene.transform.Rotate;

/**
 * @author Max Carter
 * @since  11/04/2020
 */
public class ModuleController {
    public static final String[] MODULE_TITLES = new String[]{ "Shell", "Config", "Files" };

    private RotatablePane[] rotatablePanes;

    public void init(int headRadius) {
        SubModule[] subModules = new SubModule[MODULE_TITLES.length];
        rotatablePanes = new RotatablePane[subModules.length];
        for (int i = 0; i < subModules.length; i++) {
            rotatablePanes[i] = new RotatablePane(i);
            subModules[i] = new SubModule(rotatablePanes[i], i, headRadius);
            CurvedTextController text = new CurvedTextController(MODULE_TITLES[i], rotatablePanes[i], subModules[i].getInnerRadius() + 10, 275, ColorUtils.SURFACE_COLOUR);

            Rotate rotation = new Rotate();
            rotation.pivotXProperty().bind(rotatablePanes[i].layoutXProperty());
            rotation.pivotYProperty().bind(rotatablePanes[i].layoutYProperty());
            rotation.setAngle(i * ((double) 360 / subModules.length) + SystemUtils.randomInt(0, 360));
            rotatablePanes[i].getTransforms().add(rotation);
            rotatablePanes[i].setRotation(rotation);

            rotatablePanes[i].getChildren().add(subModules[i]);
            RingLines lines = new RingLines(subModules[i].getInnerRadius(), subModules[i].getOuterRadius(), 0, 30, ColorUtils.SURFACE_COLOUR, 15, 14);
            RingLines lines2 = new RingLines(subModules[i].getInnerRadius(), subModules[i].getOuterRadius(), 180, 60, ColorUtils.SURFACE_COLOUR, 15, 28);
            rotatablePanes[i].getChildren().add(lines);
            rotatablePanes[i].getChildren().add(lines2);

            rotatablePanes[i].setScaleX(0);
            rotatablePanes[i].setScaleY(0);
            rotatablePanes[i].setOpacity(0);
            text.sendToFront();
        }
    }

    public void open() {
        for (RotatablePane rotatablePane : rotatablePanes) {
            rotatablePane.setOpacity(rotatablePane.getOpacity() + 0.01);
            rotatablePane.setScaleX(rotatablePane.getScaleX() + 0.01);
            rotatablePane.setScaleY(rotatablePane.getScaleY() + 0.01);
        }
    }

    public void spin() {
        for (RotatablePane modulesRotatable : rotatablePanes) {
            modulesRotatable.spin();
        }
    }

    public RotatablePane[] getRotatablePanes() {
        return rotatablePanes;
    }
}
