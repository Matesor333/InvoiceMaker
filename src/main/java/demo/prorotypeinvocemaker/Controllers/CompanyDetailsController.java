package demo.prorotypeinvocemaker.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;


import java.io.*;
import java.util.Properties;

public class CompanyDetailsController {

    @FXML private TextField companyNameField;
    @FXML private TextField companyIdField;
    @FXML private TextField addressLine1Field;
    @FXML private TextField addressLine2Field;
    @FXML private TextField cityField;
    @FXML private TextField postcodeField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField taxNumberField;
    @FXML private Label statusLabel;
    @FXML private TextField saveLocationField;
    @FXML private TextField supabaseUrlField;
    @FXML private TextField supabaseKeyField;
    //NEW BANK FIELDS
    @FXML private TextField bankNameField;
    @FXML private TextField accountNameField;
    @FXML private TextField ibanField;
    @FXML private TextField swiftField;

    private static final String CONFIG_FILE = "company-details.properties";

    @FXML
    public void initialize() {
        loadCompanyDetails();
    }

    @FXML
    private void handleSave() {
        Properties properties = new Properties();
        // Save all fields
        properties.setProperty("companyName", companyNameField.getText());
        properties.setProperty("companyId", companyIdField.getText());
        properties.setProperty("addressLine1", addressLine1Field.getText());
        properties.setProperty("addressLine2", addressLine2Field.getText());
        properties.setProperty("city", cityField.getText());
        properties.setProperty("postcode", postcodeField.getText());
        properties.setProperty("phone", phoneField.getText());
        properties.setProperty("email", emailField.getText());
        properties.setProperty("taxNumber", taxNumberField.getText());
        properties.setProperty("supabaseUrl", supabaseUrlField.getText());
        properties.setProperty("supabaseKey", supabaseKeyField.getText());
        //BANK SAVE DETAILS
        properties.setProperty("saveLocation", saveLocationField.getText());
        properties.setProperty("bankName", bankNameField.getText());
        properties.setProperty("accountName", accountNameField.getText());
        properties.setProperty("iban", ibanField.getText());
        properties.setProperty("swift", swiftField.getText());

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            properties.store(out, "Company Details");
            statusLabel.setText("✓ Saved successfully");
            statusLabel.setStyle("-fx-text-fill: green;");
        } catch (IOException e) {
            statusLabel.setText("✗ Save failed: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReset() {
        loadCompanyDetails();
        statusLabel.setText("Reset to saved values");
        statusLabel.setStyle("-fx-text-fill: blue;");
    }

    private void loadCompanyDetails() {
        Properties properties = new Properties();
        File configFile = new File(CONFIG_FILE);


        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                properties.load(in);
                // Load all fields
                saveLocationField.setText(properties.getProperty("saveLocation", ""));
                companyNameField.setText(properties.getProperty("companyName", ""));
                companyIdField.setText(properties.getProperty("companyId", "")); //
                addressLine1Field.setText(properties.getProperty("addressLine1", ""));
                addressLine2Field.setText(properties.getProperty("addressLine2", ""));
                cityField.setText(properties.getProperty("city", ""));
                postcodeField.setText(properties.getProperty("postcode", ""));
                phoneField.setText(properties.getProperty("phone", ""));
                emailField.setText(properties.getProperty("email", ""));
                taxNumberField.setText(properties.getProperty("taxNumber", ""));
                supabaseUrlField.setText(properties.getProperty("supabaseUrl", ""));
                supabaseKeyField.setText(properties.getProperty("supabaseKey", ""));

                // Load New Bank Details
                bankNameField.setText(properties.getProperty("bankName", ""));
                accountNameField.setText(properties.getProperty("accountName", ""));
                ibanField.setText(properties.getProperty("iban", ""));
                swiftField.setText(properties.getProperty("swift", ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void handleBrowseLocation(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Invoice Save Location");

        // Open dialog using the current window as owner
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            saveLocationField.setText(selectedDirectory.getAbsolutePath());
        }
    }

}
