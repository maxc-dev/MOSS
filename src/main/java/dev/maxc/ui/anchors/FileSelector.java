package dev.maxc.ui.anchors;

import dev.maxc.App;
import dev.maxc.os.components.compiler.CompilerAPI;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.io.log.Status;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Max Carter
 * @since 13/05/2020
 */
public class FileSelector implements Initializable {
    private static final String[] files = new String[]{"pf1", "ree"};

    private CompilerAPI compilerAPI = null;
    private int fileIndex = 0;

    @FXML
    private Button run;
    @FXML
    private Button next;
    @FXML
    private Label processLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        run.setOnAction(actionEvent -> {
            if (compilerAPI == null || processLabel == null) {
                Logger.log(Status.ERROR, this, "CompilerAPI has not been initialised properly and so cannot compile.");
            } else if (App.isValidProcessFile(processLabel.getText())) {
                compilerAPI.compile(processLabel.getText());
            } else {
                Logger.log(Status.ERROR, this, "The process file name [" + processLabel.getText() + "] is not recognised.");
            }
        });
        next.setOnAction(actionEvent -> {
            fileIndex++;
            if (fileIndex == files.length) {
                fileIndex = 0;
            }
            processLabel.setText(files[fileIndex]);
        });
    }

    public void initCompilerAPI(CompilerAPI compilerAPI) {
        this.compilerAPI = compilerAPI;
    }
}
