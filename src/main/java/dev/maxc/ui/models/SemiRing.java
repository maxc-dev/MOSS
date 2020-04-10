package dev.maxc.ui.models;

import dev.maxc.ui.models.interfaces.Rotatable;
import dev.maxc.ui.util.ColorUtils;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

/**
 * @author Max Carter
 * @since 01/04/2020
 */
public class SemiRing extends Path implements Rotatable {
    private Rotate rotation;

    /**
     * Creates a semi ring object
     */
    public SemiRing(double centerX, double centerY, double outerRadius, double innerRadius, Color strokeColor) {
        super();
        setStroke(strokeColor);
        setStrokeWidth(3);

        MoveTo moveOuterArc = new MoveTo();
        moveOuterArc.setX(centerX + outerRadius);
        moveOuterArc.setY(centerY);

        ArcTo arcOuter = new ArcTo();
        arcOuter.setX(centerX - outerRadius);
        arcOuter.setY(centerY);
        arcOuter.setRadiusX(outerRadius);
        arcOuter.setRadiusY(outerRadius);

        HLineTo rightLeg = new HLineTo();
        rightLeg.setX(centerX - innerRadius);

        MoveTo moveLeftLeg = new MoveTo();
        moveLeftLeg.setX(centerX + outerRadius);
        moveLeftLeg.setY(centerY);

        HLineTo leftLeg = new HLineTo();
        leftLeg.setX(centerX + innerRadius);

        ArcTo arcInner = new ArcTo();
        arcInner.setX(centerX);
        arcInner.setY(centerY - innerRadius);
        arcInner.setRadiusX(innerRadius);
        arcInner.setRadiusY(innerRadius);

        LineTo middleLeg = new LineTo();
        middleLeg.setY(centerY - outerRadius);
        middleLeg.setX(centerX);

        DropShadow shadow = new DropShadow(12, ColorUtils.SURFACE_COLOUR);
        setEffect(shadow);

        getElements().add(moveOuterArc);
        getElements().add(arcOuter);
        getElements().add(rightLeg);
        getElements().add(moveLeftLeg);
        getElements().add(leftLeg);
        getElements().add(arcInner);
        getElements().add(middleLeg);
    }

    @Override
    public Rotate getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(Rotate rotation) {
        this.rotation = rotation;
    }
}