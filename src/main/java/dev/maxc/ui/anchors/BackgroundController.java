package dev.maxc.ui.anchors;

import java.net.URL;
import java.util.ResourceBundle;

import dev.maxc.ui.controllers.DecorController;
import dev.maxc.ui.controllers.HeadController;
import dev.maxc.ui.controllers.SplashController;
import dev.maxc.ui.util.UiUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class BackgroundController implements Initializable, SplashController.OnSplashComplete {
    @FXML
    public AnchorPane anchorPane;

    private HeadController headController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anchorPane.setMaxWidth(UiUtils.WIDTH);
        anchorPane.setMaxHeight(UiUtils.HEIGHT);
        anchorPane.toBack();

        DecorController decorController = new DecorController(anchorPane);
        decorController.run();

        headController = new HeadController(this, anchorPane);
        headController.loadSplash();
    }

    @Override
    public void onSplashComplete() {
        Platform.runLater(headController::loadMainUI);
    }
}
