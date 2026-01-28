package demo.prorotypeinvocemaker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;

public class CreateInvoiceController {

    @FXML private ChoiceBox<String> customerTypeBox;
    @FXML private GridPane customerDetailsPane;

    // Labels that change based on selection
    @FXML private Label nameLabel;
    @FXML private Label idLabel;
    @FXML private Label vatLabel;

    // Fields
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField idField;
    @FXML private TextField vatField;



    @FXML private TextField cityField;
    @FXML private TextField postcodeField;
    @FXML private TextField countryField;




    // Table and Items
    @FXML private TableView<InvoiceItem> itemsTable;
    @FXML private TableColumn<InvoiceItem, String> serviceCol;
    @FXML private TableColumn<InvoiceItem, Double> amountCol;

    @FXML private TextField newServiceField;
    @FXML private TextField newAmountField;

    @FXML private Label totalAmountLabel;
    @FXML private DatePicker dueDatePicker;

    private final ObservableList<InvoiceItem> invoiceItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup Customer Type ChoiceBox
        customerTypeBox.setItems(FXCollections.observableArrayList("Company", "Person"));
        customerTypeBox.setValue("Company"); // Default

        // Listen for changes
        customerTypeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateFormFields(newVal);
        });

        // Setup Table Columns
        serviceCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        itemsTable.setItems(invoiceItems);

        // Default due date to 14 days from now
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
    }

    private void updateFormFields(String type) {
        if ("Person".equals(type)) {
            // PERSON VIEW
            nameLabel.setText("Full Name:");
            nameField.setPromptText("John Doe");

            // Hide ID field completely
            idLabel.setVisible(false);
            idField.setVisible(false);

            // Hide VAT field completely
            vatLabel.setVisible(false);
            vatField.setVisible(false);

        } else {
            // COMPANY VIEW
            nameLabel.setText("Company Name:");
            nameField.setPromptText("Acme Corp");

            // Show ID field
            idLabel.setVisible(true);
            idField.setVisible(true);
            idLabel.setText("Company ID:");

            // Show VAT field
            vatLabel.setVisible(true);
            vatField.setVisible(true);
        }
    }


    @FXML
    private void handleAddItem() {
        String desc = newServiceField.getText();
        String amountText = newAmountField.getText();

        if (desc.isEmpty() || amountText.isEmpty()) {
            showAlert("Error", "Please enter both service and amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            invoiceItems.add(new InvoiceItem(desc, amount));

            // Clear inputs and update total
            newServiceField.clear();
            newAmountField.clear();
            updateTotal();

        } catch (NumberFormatException e) {
            showAlert("Invalid Amount", "Please enter a valid number for amount.");
        }
    }

    private void updateTotal() {
        double total = invoiceItems.stream().mapToDouble(InvoiceItem::getAmount).sum();
        totalAmountLabel.setText(String.format("%.2f", total));
    }

    @FXML
    private void handleGenerate() {
        System.out.println("Generating invoice for: " + nameField.getText());
        System.out.println("Address: " + addressField.getText());
        System.out.println("Location: " + cityField.getText() + ", " + countryField.getText() + " " + postcodeField.getText());
        System.out.println("Total: " + totalAmountLabel.getText());
        System.out.println("Due: " + dueDatePicker.getValue());
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
