module dev.maxc {
    requires javafx.controls;
    requires javafx.fxml;

    opens dev.maxc to javafx.fxml;
    exports dev.maxc;
}