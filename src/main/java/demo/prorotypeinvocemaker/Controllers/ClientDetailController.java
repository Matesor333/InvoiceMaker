package demo.prorotypeinvocemaker.Controllers;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import demo.prorotypeinvocemaker.helperClass.InvoiceRecord;
import demo.prorotypeinvocemaker.models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ClientDetailController {

    @FXML private Label nameLabel;
    @FXML private Label typeLabel;
    @FXML private Label addressLabel;
    @FXML private Label cityLabel;
    @FXML private Label postcodeLabel;
    @FXML private Label countryLabel;
    @FXML private Label idLabel;
    @FXML private Label vatLabel;
    @FXML private Label noteLabel;

    @FXML private TableView<InvoiceRecord> invoicesTable;
    @FXML private TableColumn<InvoiceRecord, String> invoiceIdColumn;
    @FXML private TableColumn<InvoiceRecord, String> dateColumn;
    @FXML private TableColumn<InvoiceRecord, String> totalColumn;

    @FXML private TableView<ServiceDetail> servicesTable;
    @FXML private TableColumn<ServiceDetail, String> serviceDateColumn;
    @FXML private TableColumn<ServiceDetail, String> serviceDescriptionColumn;
    @FXML private TableColumn<ServiceDetail, String> serviceAmountColumn;

    private Customer customer;
    private final ObservableList<InvoiceRecord> invoiceList = FXCollections.observableArrayList();
    private final ObservableList<ServiceDetail> serviceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        serviceDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        serviceDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        serviceAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        invoicesTable.setItems(invoiceList);
        servicesTable.setItems(serviceList);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        displayCustomerInfo();
        loadClientData();
    }

    private void displayCustomerInfo() {
        nameLabel.setText(customer.getName());
        typeLabel.setText(customer.getType());
        addressLabel.setText(customer.getAddress());
        cityLabel.setText(customer.getCity());
        postcodeLabel.setText(customer.getPostcode());
        countryLabel.setText(customer.getCountry());
        idLabel.setText(customer.getId());
        vatLabel.setText(customer.getVat());
        noteLabel.setText(customer.getNote());
    }

    @FXML
    private void handleEditClient() {
        showClientDialog(customer);
    }

    private void showClientDialog(Customer customer) {
        javafx.scene.control.Dialog<Customer> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Edit Client");

        javafx.scene.control.ButtonType saveButtonType = new javafx.scene.control.ButtonType("Save", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, javafx.scene.control.ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        javafx.scene.control.TextField nameField = new javafx.scene.control.TextField(customer.getName());
        javafx.scene.control.TextField addressField = new javafx.scene.control.TextField(customer.getAddress());
        javafx.scene.control.TextField cityField = new javafx.scene.control.TextField(customer.getCity());
        javafx.scene.control.TextField postcodeField = new javafx.scene.control.TextField(customer.getPostcode());
        javafx.scene.control.TextField countryField = new javafx.scene.control.TextField(customer.getCountry());
        javafx.scene.control.TextField idField = new javafx.scene.control.TextField(customer.getId());
        javafx.scene.control.TextField vatField = new javafx.scene.control.TextField(customer.getVat());
        javafx.scene.control.TextArea noteArea = new javafx.scene.control.TextArea(customer.getNote());
        noteArea.setPrefRowCount(3);
        javafx.scene.control.ComboBox<String> typeComboBox = new javafx.scene.control.ComboBox<>();
        typeComboBox.setItems(javafx.collections.FXCollections.observableArrayList("Company", "Person"));
        typeComboBox.setValue(customer.getType());

        grid.add(new javafx.scene.control.Label("Type:"), 0, 0);
        grid.add(typeComboBox, 1, 0);
        grid.add(new javafx.scene.control.Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new javafx.scene.control.Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new javafx.scene.control.Label("City:"), 0, 3);
        grid.add(cityField, 1, 3);
        grid.add(new javafx.scene.control.Label("Postcode:"), 0, 4);
        grid.add(postcodeField, 1, 4);
        grid.add(new javafx.scene.control.Label("Country:"), 0, 5);
        grid.add(countryField, 1, 5);
        grid.add(new javafx.scene.control.Label("ID/IČO:"), 0, 6);
        grid.add(idField, 1, 6);
        grid.add(new javafx.scene.control.Label("VAT/DIČ:"), 0, 7);
        grid.add(vatField, 1, 7);
        grid.add(new javafx.scene.control.Label("Note:"), 0, 8);
        grid.add(noteArea, 1, 8);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/demo/prorotypeinvocemaker/styles.css").toExternalForm());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Customer(
                        nameField.getText(),
                        addressField.getText(),
                        cityField.getText(),
                        postcodeField.getText(),
                        countryField.getText(),
                        idField.getText(),
                        vatField.getText(),
                        typeComboBox.getValue(),
                        noteArea.getText()
                );
            }
            return null;
        });

        java.util.Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(newCustomer -> {
            new demo.prorotypeinvocemaker.helperClass.CustomerManager().addOrUpdateCustomer(newCustomer);
            this.customer = newCustomer;
            displayCustomerInfo();
            demo.prorotypeinvocemaker.managers.RefreshManager.triggerRefresh();
        });
    }

    private void loadClientData() {
        String saveLocation = loadSaveLocation();
        File folder = new File(saveLocation);
        if (!folder.exists()) return;

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (files == null) return;

        for (File file : files) {
            try {
                InvoiceRecord record = extractInvoiceData(file);
                if (record != null && record.getCustomer().equalsIgnoreCase(customer.getName())) {
                    invoiceList.add(record);
                    extractAndAddServices(file, record.getDate());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private InvoiceRecord extractInvoiceData(File file) {
        try (PdfReader reader = new PdfReader(file);
             PdfDocument pdfDoc = new PdfDocument(reader)) {
            String text = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1));

            String filename = file.getName();
            String invoiceId = filename.replace("invoice_", "").replace(".pdf", "").replace("_", "-");
            String type = filename.contains("PO_") ? "Company" : "Person";

            String customerName = extractField(text, "BILL TO:");
            String total = extractTotal(text);
            String dateStr = extractField(text, "Invoice Date:");

            LocalDate parsedDate;
            try {
                parsedDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } catch (Exception e) {
                parsedDate = LocalDate.ofEpochDay(file.lastModified() / (1000 * 60 * 60 * 24));
            }

            return new InvoiceRecord(invoiceId, type, customerName, dateStr, total, file, parsedDate);
        } catch (Exception e) {
            return null;
        }
    }

    private void extractAndAddServices(File file, String date) {
        try (PdfReader reader = new PdfReader(file);
             PdfDocument pdfDoc = new PdfDocument(reader)) {
            String text = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1));
            String[] lines = text.split("\n");
            boolean start = false;
            for (String line : lines) {
                if (line.contains("Service") && line.contains("Amount")) {
                    start = true;
                    continue;
                }
                if (line.contains("TOTAL")) break;
                if (start) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty()) continue;
                    String[] parts = trimmed.split("\\s+");
                    if (parts.length >= 3) {
                        StringBuilder desc = new StringBuilder();
                        String amount = parts[parts.length - 2] + " " + parts[parts.length - 1]; // Symbol + Amount
                        for (int i = 0; i < parts.length - 2; i++) {
                            desc.append(parts[i]).append(" ");
                        }
                        serviceList.add(new ServiceDetail(date, desc.toString().trim(), amount));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractField(String text, String marker) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(marker)) {
                if (marker.equals("BILL TO:") && i + 1 < lines.length) {
                    return lines[i + 1].trim();
                }
                return lines[i].replace(marker, "").trim();
            }
        }
        return "Unknown";
    }

    private String extractTotal(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains("TOTAL") && (line.contains("£") || line.contains("$") || line.contains("€"))) {
                String[] parts = line.split("TOTAL");
                if (parts.length > 1) return parts[1].trim();
            }
        }
        return "N/A";
    }

    private String loadSaveLocation() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("company-details.properties")) {
            props.load(fis);
            return props.getProperty("saveLocation", System.getProperty("user.home"));
        } catch (Exception e) {
            return System.getProperty("user.home");
        }
    }

    public static class ServiceDetail {
        private final String date;
        private final String description;
        private final String amount;

        public ServiceDetail(String date, String description, String amount) {
            this.date = date;
            this.description = description;
            this.amount = amount;
        }

        public String getDate() { return date; }
        public String getDescription() { return description; }
        public String getAmount() { return amount; }
    }
}
