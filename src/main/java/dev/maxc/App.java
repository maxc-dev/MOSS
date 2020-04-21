package dev.maxc;

import dev.maxc.os.system.api.SystemAPI;
import dev.maxc.os.system.api.SystemUtils;
import dev.maxc.logs.Logger;
import dev.maxc.ui.util.Display;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * @author Max Carter
 * @since 01/04/2020
 */
public class App extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        SystemAPI.uiAPI.setStage(stage);
        SystemAPI.uiAPI.setTitle("Booting up...");

        scene = new Scene(loadFXML(Display.PRIMARY));
        scene.getStylesheets().add(getClass().getResource("fontstyle.css").toExternalForm());

        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Pres [ESC] to quit the simulation.");
        stage.fullScreenProperty().addListener((observableValue, aBoolean, t1) -> SystemUtils.shutdown());
        stage.setOnCloseRequest(t -> SystemUtils.shutdown());
        stage.setX(0);
        stage.setY(0);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    static void setRoot(Display display) throws IOException {
        scene.setRoot(loadFXML(display));
    }

    private static Parent loadFXML(Display display) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(display + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        Logger.log("App started.");
        launch();
    }
}