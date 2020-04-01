package dev.maxc;

import java.net.URL;
import java.util.ResourceBundle;

import dev.maxc.ui.DecorController;
import dev.maxc.ui.HeadController;
import dev.maxc.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class BackgroundController implements Initializable {
    @FXML
    public AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anchorPane.setMaxWidth(Utils.WIDTH);
        anchorPane.setMaxHeight(Utils.HEIGHT);
        anchorPane.toBack();

        DecorController decorController = new DecorController(anchorPane);
        decorController.run();

        HeadController headController = new HeadController(Utils.TITLE, anchorPane);
        headController.run();

 /*       Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.01), evt -> updateLoadingPercent()));
        timeline.setCycleCount(200);
        timeline.play();*/
    }

}
