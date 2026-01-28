module demo.prorotypeinvocemaker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.slf4j;
    requires layout;
    requires kernel;
    requires java.desktop;
    requires io;

    opens demo.prorotypeinvocemaker to javafx.fxml;
    exports demo.prorotypeinvocemaker;
    exports demo.prorotypeinvocemaker.Controllers;
    opens demo.prorotypeinvocemaker.Controllers to javafx.fxml;
    exports demo.prorotypeinvocemaker.managers;
    opens demo.prorotypeinvocemaker.managers to javafx.fxml;
    exports demo.prorotypeinvocemaker.helperClass;
    opens demo.prorotypeinvocemaker.helperClass to javafx.fxml;
}
