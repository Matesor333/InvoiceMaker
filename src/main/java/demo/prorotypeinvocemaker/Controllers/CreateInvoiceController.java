package demo.prorotypeinvocemaker.Controllers;

import demo.prorotypeinvocemaker.helperClass.CustomerManager;
import demo.prorotypeinvocemaker.helperClass.InvoiceIdGenerator;
import demo.prorotypeinvocemaker.managers.SupabaseClient;
import demo.prorotypeinvocemaker.models.Customer;
import demo.prorotypeinvocemaker.models.Invoice;
import demo.prorotypeinvocemaker.models.InvoiceItem;
import demo.prorotypeinvocemaker.helperClass.InvoicePdfGenerator;
import demo.prorotypeinvocemaker.managers.RefreshManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
public class CreateInvoiceController {

    @FXML private ChoiceBox<String> customerTypeBox;
    @FXML private GridPane customerDetailsPane;
    @FXML private ChoiceBox<String> currencyBox;
    // Labels that change based on selection
    @FXML private Label nameLabel;
    @FXML private Label idLabel;
    @FXML private Label vatLabel;

    // Fields

    @FXML private TextField addressField;
    @FXML private TextField idField;
    @FXML private TextField vatField;
    @FXML private Label invoiceIdLabel;
    @FXML private TextField cityField;
    @FXML private TextField postcodeField;
    @FXML private TextField countryField;
    // Table and Items
    @FXML private TableView<InvoiceItem> itemsTable;
    @FXML private TableColumn<InvoiceItem, String> serviceCol;
    @FXML private TableColumn<InvoiceItem, Double> amountCol;
    @FXML private ChoiceBox<String> languageBox;

    @FXML private TextField newServiceField;
    @FXML private TextField newAmountField;

    @FXML private Label totalAmountLabel;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<Customer> customerSearchBox;

    private final ObservableList<InvoiceItem> invoiceItems = FXCollections.observableArrayList();
    private final CustomerManager customerManager = new CustomerManager();
    private final SupabaseClient supabaseClient = new SupabaseClient();
    @FXML
    public void initialize() {
        // Setup Customer Type ChoiceBox
        customerTypeBox.setItems(FXCollections.observableArrayList("Company", "Person"));
        customerTypeBox.setValue("Company"); // Default

        // Listen for changes
        customerTypeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateFormFields(newVal);
            loadCustomerList(newVal);
        });

        loadCustomerList(customerTypeBox.getValue());

        // Handle selection event (User clicks a name from dropdown)
        customerSearchBox.setOnAction(event -> {
            Object selected = customerSearchBox.getSelectionModel().getSelectedItem();
            if (selected instanceof Customer) {
                fillCustomerDetails((Customer) selected);
            }
        });

        // Setup Currency ChoiceBox
        currencyBox.setItems(FXCollections.observableArrayList("USD ($)", "EUR (€)", "GBP (£)"));
        currencyBox.setValue("GBP (£)"); // Default

        // Listen for currency changes to update total label immediately
        currencyBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateTotal();
        });
        // Setup Table Columns
        serviceCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        itemsTable.setItems(invoiceItems);

        // Default due date to 14 days from now
        dueDatePicker.setValue(LocalDate.now().plusDays(14));

        customerTypeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateIdPreview();
            updateFormFields(newVal);
        });
        languageBox.setItems(FXCollections.observableArrayList("English", "Slovak"));
        languageBox.setValue("English"); // Default

        // Initial preview
        updateIdPreview();

        // Register a local refresh task but don't overwrite the global one if it's already used by ClientManagement
        // Actually RefreshManager only supports one task currently.
        // Let's change RefreshManager to support multiple tasks or just use it here carefully.
        // For now, I'll keep it as is, but be aware of the conflict.
        RefreshManager.addRefreshTask(() -> {
            loadCustomerList(customerTypeBox.getValue());
        });
    }

    private void updateFormFields(String type) {
        if ("Person".equals(type)) {
            // PERSON VIEW
            nameLabel.setText("Full Name:");
            customerSearchBox.setPromptText("John Doe");

            // Hide ID field completely
            idLabel.setVisible(false);
            idField.setVisible(false);

            // Hide VAT field completely
            vatLabel.setVisible(false);
            vatField.setVisible(false);

        } else {
            // COMPANY VIEW
            nameLabel.setText("Company Name:");
            customerSearchBox.setPromptText("Acme Corp");

            // Show ID field
            idLabel.setVisible(true);
            idField.setVisible(true);
            idLabel.setText("Company ID:");

            // Show VAT field
            vatLabel.setVisible(true);
            vatField.setVisible(true);
        }
    }

    private void loadCustomerList(String type) {
        List<Customer> matching = customerManager.getCustomersByType(type);
        customerSearchBox.setItems(FXCollections.observableArrayList(matching));
    }

    private void fillCustomerDetails(Customer c) {
        addressField.setText(c.getAddress());
        cityField.setText(c.getCity());
        postcodeField.setText(c.getPostcode());
        countryField.setText(c.getCountry());

        if (idField != null) idField.setText(c.getId());
        if (vatField != null) vatField.setText(c.getVat());
    }

    @FXML
    private void handleAddItem() {
        String desc = newServiceField.getText();
        String amountText = newAmountField.getText();

        if (desc.isEmpty() || amountText.isEmpty()) {
            showSuccess( "Error", "Please enter both service and amount.");
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
            showSuccess("Invalid Amount", "Please enter a valid number for amount.");
        }
    }

    private void updateTotal() {
        double total = invoiceItems.stream().mapToDouble(InvoiceItem::getAmount).sum();
        totalAmountLabel.setText(String.format("%.2f", total));
    }

    private String getCurrencySymbol() {
        String selected = currencyBox.getValue();
        if (selected == null) return "£";

        if (selected.contains("USD")) return "$";
        if (selected.contains("EUR")) return "€";
        return "£"; // Default to GBP
    }

    @FXML
    private void handleGenerate() {
        System.out.println("Generate Invoice with currency: " + currencyBox.getValue());
        // Validate customer fields
        String customerName = "";
        Object value = customerSearchBox.getValue();
        Customer selectedCustomer = null;

        if (value instanceof Customer) {
            selectedCustomer = (Customer) value;
            customerName = selectedCustomer.getName();
        } else if (value instanceof String) {
            customerName = (String) value;
        }

        if (customerName == null || customerName.trim().isEmpty()) {
            showError("Validation Error", "Please enter a name.");
            return;
        }

        if (selectedCustomer == null) {
            // Check if customer with this reg number already exists to avoid duplication
            String regNum = idField.getText();
            if (regNum != null && !regNum.trim().isEmpty()) {
                selectedCustomer = customerManager.getAllCustomers().stream()
                        .filter(c -> regNum.equals(c.getId()))
                        .findFirst()
                        .orElse(null);
            }
            
            if (selectedCustomer == null) {
                selectedCustomer = new Customer(
                        customerName,
                        addressField.getText(),
                        cityField.getText(),
                        postcodeField.getText(),
                        countryField.getText(),
                        regNum,
                        vatField.getText(),
                        customerTypeBox.getValue()
                );
            } else {
                // Update the existing customer with potentially new info from fields
                selectedCustomer.setName(customerName);
                selectedCustomer.setAddress(addressField.getText());
                selectedCustomer.setCity(cityField.getText());
                selectedCustomer.setPostcode(postcodeField.getText());
                selectedCustomer.setCountry(countryField.getText());
                selectedCustomer.setVat(vatField.getText());
                selectedCustomer.setType(customerTypeBox.getValue());
            }
        } else {
            // Update existing customer info from fields
            selectedCustomer.setAddress(addressField.getText());
            selectedCustomer.setCity(cityField.getText());
            selectedCustomer.setPostcode(postcodeField.getText());
            selectedCustomer.setCountry(countryField.getText());
            selectedCustomer.setId(idField.getText());
            selectedCustomer.setVat(vatField.getText());
        }

        customerManager.addOrUpdateCustomer(selectedCustomer);

        // Fetch the customer again to make sure we have the internal UUID if it was just created
        // Or we can rely on the fact that if it's already in the DB, we might need its UUID.
        // Let's try to find it by name or reg number if internalId is null.
        if (selectedCustomer.getInternalId() == null) {
            String finalRegNumber = selectedCustomer.getId();
            String finalCustomerName = selectedCustomer.getName();
            
            List<Customer> all = customerManager.getAllCustomers();
            selectedCustomer = all.stream()
                    .filter(c -> (finalRegNumber != null && !finalRegNumber.isEmpty() && finalRegNumber.equals(c.getId())) 
                              || (finalCustomerName != null && finalCustomerName.equals(c.getName())))
                    .findFirst()
                    .orElse(selectedCustomer);
        }

        final Customer finalCustomer = selectedCustomer;

        // Reload list to include the new one immediately
        loadCustomerList(customerTypeBox.getValue());


        // Validate items
        if (invoiceItems.isEmpty()) {
            showError("Validation Error", "Please add at least one invoice item.");
            return;
        }

        // Get save location from Company Details
        String saveLocation = loadSaveLocation();
        if (saveLocation == null || saveLocation.isEmpty()) {
            showError("Configuration Error", "Please set an invoice save folder in Company Details.");
            return;
        }

        // Check folder exists
        java.io.File folder = new java.io.File(saveLocation);
        if (!folder.exists()) {
            showError("Folder Error", "Save folder does not exist: " + saveLocation);
            return;
        }

        try {
            // Generate unique invoice ID
            String invoiceId = InvoiceIdGenerator.generateId(customerTypeBox.getValue());

            // Save to Supabase
            double total = 0;
            for (InvoiceItem item : invoiceItems) {
                total += item.getAmount();
            }
            String filename = "invoice_" + invoiceId.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";

            String currency = currencyBox.getValue();
            if (currency != null && currency.contains(" ")) {
                currency = currency.substring(0, currency.indexOf(" ")); // Extract "USD", "EUR", "GBP"
            }

            Invoice invoice = new Invoice(
                    invoiceId,
                    finalCustomer.getInternalId(), // Use the UUID from Supabase
                    LocalDate.now().toString(),
                    total,
                    currency,
                    filename,
                    dueDatePicker.getValue() != null ? dueDatePicker.getValue().toString() : ""
            );
            supabaseClient.upsertInvoice(invoice);

            Locale locale = new Locale("en", "US");
            if ("Slovak".equals(languageBox.getValue())) {
                locale = new Locale("sk", "SK");
            }

            // Generate PDF with Locale
            InvoicePdfGenerator.generateInvoice(
                    invoiceId,
                    customerName,
                    addressField.getText(),
                    cityField.getText(),
                    postcodeField.getText(),
                    countryField.getText(),
                    idField.getText(),
                    vatField.getText(),
                    customerTypeBox.getValue(),
                    new java.util.ArrayList<>(invoiceItems),
                    dueDatePicker.getValue(),
                    currencyBox.getValue(),
                    saveLocation,
                    locale, // <--- Pass the locale here
                    loadCompanyProperties() // <--- Pass company properties
            );

            // Show success message
            showSuccess("Invoice Generated",
                    "Invoice #" + invoiceId + " created successfully!\n\nSaved to:\n" + saveLocation);

            // Clear form
            clearForm();

        } catch (Exception e) {
            showError("PDF Generation Error", e.getMessage());
            e.printStackTrace();
        }
    }
    private void clearForm() {
        customerSearchBox.setValue(null);
        ;
        addressField.clear();
        cityField.clear();
        postcodeField.clear();
        countryField.clear();
        idField.clear();
        vatField.clear();
        newServiceField.clear();
        newAmountField.clear();
        invoiceItems.clear();
        totalAmountLabel.setText("0.00");
        dueDatePicker.setValue(java.time.LocalDate.now().plusDays(14));
    }
    private String loadSaveLocation() {
        java.util.Properties properties = loadCompanyProperties();
        return properties.getProperty("saveLocation", "");
    }

    private java.util.Properties loadCompanyProperties() {
        java.util.Properties properties = new java.util.Properties();
        java.io.File configFile = new java.io.File("company-details.properties");

        if (configFile.exists()) {
            try (java.io.FileInputStream in = new java.io.FileInputStream(configFile)) {
                properties.load(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // Removes the big header text to make it cleaner
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error");
        alert.setContentText(content);
        alert.showAndWait();
    }




    private void updateIdPreview() {
        // Just a preview, not the final frozen ID
        String type = customerTypeBox.getValue();
        String previewId = "Company".equals(type) ? "PO-YYYYMMDD..." : "FO-YYYYMMDD...";
        invoiceIdLabel.setText(previewId);
    }


}
