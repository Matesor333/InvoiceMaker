module demo.prorotypeinvocemaker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.slf4j;
    requires layout;
    requires kernel;

    opens demo.prorotypeinvocemaker to javafx.fxml;
    exports demo.prorotypeinvocemaker;
}
