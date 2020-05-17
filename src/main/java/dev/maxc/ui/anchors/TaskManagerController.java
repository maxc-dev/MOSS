package dev.maxc.ui.anchors;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    @FXML
    private LineChart<Integer, Integer> memoryUsageChart;
    @FXML
    private NumberAxis xAxisMemory;
    @FXML
    private NumberAxis yAxisMemory;

    private XYChart.Series<Integer, Integer> writeRequests = new XYChart.Series<>();
    private XYChart.Series<Integer, Integer> readRequests = new XYChart.Series<>();
    private int counter = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pane.setOpacity(0);
        output("Output online");
        console("Console Online");

        outputListView.getItems().addListener((ListChangeListener<String>) change -> Platform.runLater(() -> outputListView.scrollTo(outputListView.getItems().size() - 1)));
        consoleListView.getItems().addListener((ListChangeListener<String>) change -> Platform.runLater(() -> consoleListView.scrollTo(consoleListView.getItems().size() - 1)));

        readRequests.setName("Memory Read Requests");
        writeRequests.setName("Memory Write Requests");
        memoryUsageChart.getData().add(readRequests);
        memoryUsageChart.getData().add(writeRequests);
    }

    public void addMemoryWrite(int read, int write) {
        counter++;
        writeRequests.getData().add(new XYChart.Data<>(counter, write));
        readRequests.getData().add(new XYChart.Data<>(counter, read));
        if (counter >= 30) {
            readRequests.getData().remove(0);
            writeRequests.getData().remove(0);
        }
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
