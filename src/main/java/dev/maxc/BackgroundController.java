package dev.maxc;

import java.net.URL;
import java.util.ResourceBundle;

import dev.maxc.ui.controllers.DecorController;
import dev.maxc.ui.controllers.HeadController;
import dev.maxc.ui.util.UiUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class BackgroundController implements Initializable {
    @FXML
    public AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anchorPane.setMaxWidth(UiUtils.WIDTH);
        anchorPane.setMaxHeight(UiUtils.HEIGHT);
        anchorPane.toBack();

        DecorController decorController = new DecorController(anchorPane);
        decorController.run();

        HeadController headController = new HeadController(anchorPane);
        headController.init();
    }

}
