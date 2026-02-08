package demo.prorotypeinvocemaker.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class SupabaseConfigController {

    @FXML private TextField urlField;
    @FXML private PasswordField keyField;
    @FXML private TextField saveFolderField;
    @FXML private Label errorLabel;

    private static final String CONFIG_FILE = "company-details.properties";

    @FXML
    private void handleSave() {
        String url = urlField.getText().trim();
        String key = keyField.getText().trim();
        String saveFolder = saveFolderField.getText().trim();

        if (url.isEmpty() || key.isEmpty() || saveFolder.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (!url.startsWith("http")) {
            errorLabel.setText("Invalid URL format.");
            return;
        }

        saveToProperties(url, key, saveFolder);

        // Close the popup
        Stage stage = (Stage) urlField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleBrowse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Invoice Save Location");
        Stage stage = (Stage) urlField.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            saveFolderField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void saveToProperties(String url, String key, String saveFolder) {
        Properties properties = new Properties();
        File configFile = new File(CONFIG_FILE);

        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                properties.load(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        properties.setProperty("supabaseUrl", url);
        properties.setProperty("supabaseKey", key);
        properties.setProperty("saveLocation", saveFolder);

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            properties.store(out, "Supabase Configuration");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to save configuration.");
        }
    }
}
