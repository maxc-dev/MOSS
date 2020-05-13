package dev.maxc;

import dev.maxc.os.system.api.SystemAPI;
import dev.maxc.os.system.api.SystemUtils;
import dev.maxc.os.io.log.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

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

        scene = new Scene(loadFXML("primary"));
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

    static void setRoot(String name) throws IOException {
        scene.setRoot(loadFXML(name));
    }

    public static Parent loadFXML(String name) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(name + ".fxml"));
        return fxmlLoader.load();
    }

    public static boolean isValidProcessFile(String name) {
        URL file = App.class.getResource(name + ".moss");
        return file != null;
    }

    public static void main(String[] args) {
        Logger.log("Main", "App started.");
        launch();
    }
}