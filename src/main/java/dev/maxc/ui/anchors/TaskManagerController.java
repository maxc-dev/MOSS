package dev.maxc.ui.anchors;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Max Carter
 * @since 14/05/2020
 */
public class TaskManagerController implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private ListView<String> outputListView;
    @FXML
    private ListView<String> consoleListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pane.setOpacity(0);
        output("Output online");
        console("Console Online");

        outputListView.getItems().addListener((ListChangeListener<String>) change -> {
            Platform.runLater(() -> outputListView.scrollTo(outputListView.getItems().size() - 1));
        });
        consoleListView.getItems().addListener((ListChangeListener<String>) change -> {
            Platform.runLater(() -> consoleListView.scrollTo(consoleListView.getItems().size() - 1));
        });
    }

    public void output(String text) {
        outputListView.getItems().add(" > " + text);
    }

    public void console(String text) {
        consoleListView.getItems().add(text);
    }

    public void toggle() {
        pane.setOpacity(pane.getOpacity() == 0 ? 1 : 0);
    }
}
