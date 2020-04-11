module dev.maxc {
    requires javafx.controls;
    requires javafx.fxml;

    opens dev.maxc.ui.anchors to javafx.fxml;
    exports dev.maxc.ui.anchors;

    opens dev.maxc to javafx.graphics;
    exports dev.maxc;
}