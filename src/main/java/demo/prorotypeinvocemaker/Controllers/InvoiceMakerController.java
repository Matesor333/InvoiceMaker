package demo.prorotypeinvocemaker.Controllers;

import demo.prorotypeinvocemaker.managers.FileWatcher;
import demo.prorotypeinvocemaker.managers.RefreshManager;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class InvoiceMakerController {

    @FXML
    private TabPane mainTabPane;

    private FileWatcher customerWatcher;

    @FXML
    public void initialize() {
        // Main window initialization if needed
        System.out.println("Invoice Maker initialized");

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && "Create Invoice".equals(newTab.getText())) {
                RefreshManager.triggerRefresh();
            }
        });

        // Watch for changes in customers.dat
        customerWatcher = new FileWatcher();
        customerWatcher.start("customers.dat", RefreshManager::triggerRefresh);
    }
}
