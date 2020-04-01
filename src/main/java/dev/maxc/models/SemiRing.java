package dev.maxc.models;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * @author Max Carter
 * @since 01/04/2020
 */
public class SemiRing extends Path {
    public SemiRing(double centerX, double centerY, double radius, double innerRadius, Color bgColor, Color strkColor) {
        super();
        setFill(bgColor);
        setStroke(strkColor);
        setFillRule(FillRule.EVEN_ODD);

        MoveTo moveTo = new MoveTo();
        moveTo.setX(centerX + innerRadius);
        moveTo.setY(centerY);

        ArcTo arcToInner = new ArcTo();
        arcToInner.setX(centerX - innerRadius);
        arcToInner.setY(centerY);
        arcToInner.setRadiusX(innerRadius);
        arcToInner.setRadiusY(innerRadius);

        MoveTo moveTo2 = new MoveTo();
        moveTo2.setX(centerX + innerRadius);
        moveTo2.setY(centerY);

        HLineTo hLineToRightLeg = new HLineTo();
        hLineToRightLeg.setX(centerX + radius);

        ArcTo arcTo = new ArcTo();
        arcTo.setX(centerX - radius);
        arcTo.setY(centerY);
        arcTo.setRadiusX(radius);
        arcTo.setRadiusY(radius);

        HLineTo hLineToLeftLeg = new HLineTo();
        hLineToLeftLeg.setX(centerX - innerRadius);

        DropShadow shadow = new DropShadow(10, (Color) getFill());
        setEffect(shadow);

        getElements().add(moveTo);
        getElements().add(arcToInner);
        getElements().add(moveTo2);
        getElements().add(hLineToRightLeg);
        getElements().add(arcTo);
        getElements().add(hLineToLeftLeg);
    }

}
