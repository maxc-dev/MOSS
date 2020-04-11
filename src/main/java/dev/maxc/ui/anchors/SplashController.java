package dev.maxc.ui.anchors;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 * @author Max Carter
 * @since 11/04/2020
 */
public class SplashController implements Initializable {
    @FXML
    private AnchorPane splashAnchor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
            Splash has to show the progress of the loading
            needs:
                loading bar
                system name
                detail of what is being loaded
                percentage
                estimated time left
         */
    }
}
