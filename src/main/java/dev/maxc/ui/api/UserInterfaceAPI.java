package dev.maxc.ui.api;

import dev.maxc.logs.Logger;
import dev.maxc.os.system.api.SystemAPI;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * @author Max Carter
 * @since 01/04/2020
 */
public class UserInterfaceAPI {
    private Stage stage = null;

    /**
     * Sets the main title of the program.
     */
    public void setTitle(String title) {
        if (stage != null) {
            String completeTitle = title + " | " + SystemAPI.SYSTEM_NAME + " by " + SystemAPI.SYSTEM_AUTHOR;
            Platform.runLater(() -> {
                stage.setTitle(completeTitle);
            });
            Logger.log("Title changed to [" + completeTitle + "]");
        } else {
            Logger.log("UiAPI", "The `stage` has not been set, hence the title cannot be changed.");
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
