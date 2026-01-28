package demo.prorotypeinvocemaker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

public class PastInvoicesController {

    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> typeFilterBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private TableView<InvoiceRecord> invoiceTable;
    @FXML private TableColumn<InvoiceRecord, String> invoiceIdCol;
    @FXML private TableColumn<InvoiceRecord, String> typeCol;
    @FXML private TableColumn<InvoiceRecord, String> customerCol;
    @FXML private TableColumn<InvoiceRecord, String> dateCol;
    @FXML private TableColumn<InvoiceRecord, String> totalCol;

    private ObservableList<InvoiceRecord> allInvoices = FXCollections.observableArrayList();
    private FilteredList<InvoiceRecord> filteredInvoices;

    @FXML
    public void initialize() {
        // Setup table columns
        invoiceIdCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customer"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Setup type filter
        typeFilterBox.setItems(FXCollections.observableArrayList("All", "Company (PO)", "Person (FO)"));
        typeFilterBox.setValue("All");

        // Setup filtered list
        filteredInvoices = new FilteredList<>(allInvoices, p -> true);
        invoiceTable.setItems(filteredInvoices);

        // Add listeners for live filtering
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        typeFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        RefreshManager.setRefreshTask(this::loadInvoices);
        // Load invoices
        loadInvoices();
    }

    private void loadInvoices() {
        allInvoices.clear();

        String saveLocation = loadSaveLocation();
        if (saveLocation == null || saveLocation.isEmpty()) {
            return;
        }

        File folder = new File(saveLocation);
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (files == null) return;

        for (File file : files) {
            try {
                InvoiceRecord record = extractInvoiceData(file);
                if (record != null) {
                    allInvoices.add(record);
                }
            } catch (Exception e) {
                System.err.println("Error reading: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    private InvoiceRecord extractInvoiceData(File file) {
        try {
            // Read PDF and extract text
            PdfReader reader = new PdfReader(file);
            PdfDocument pdfDoc = new PdfDocument(reader);
            String text = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1));
            pdfDoc.close();

            // Parse invoice ID from filename (invoice_PO_20250128_143005_000.pdf)
            String filename = file.getName();
            String invoiceId = "Unknown";
            String type = "Unknown";
            LocalDate date = LocalDate.now();

            if (filename.contains("PO_")) {
                type = "Company";
                invoiceId = filename.replace("invoice_", "").replace(".pdf", "").replace("_", "-");
            } else if (filename.contains("FO_")) {
                type = "Person";
                invoiceId = filename.replace("invoice_", "").replace(".pdf", "").replace("_", "-");
            }

            // Extract customer name and total from PDF text
            String customer = extractCustomerName(text);
            String total = extractTotal(text);
            String dateStr = extractDate(text);

            try {
                date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e) {
                // Use file modification date as fallback
                date = LocalDate.ofEpochDay(file.lastModified() / (1000 * 60 * 60 * 24));
            }

            return new InvoiceRecord(invoiceId, type, customer, dateStr, total, file, date);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractCustomerName(String text) {
        // Look for "BILL TO:" and get next line
        if (text.contains("BILL TO:")) {
            String[] lines = text.split("\n");
            for (int i = 0; i < lines.length - 1; i++) {
                if (lines[i].contains("BILL TO:")) {
                    return lines[i + 1].trim();
                }
            }
        }
        return "Unknown Customer";
    }

    private String extractTotal(String text) {
        // Look for TOTAL and extract amount
        if (text.contains("TOTAL")) {
            String[] lines = text.split("\n");
            for (String line : lines) {
                if (line.contains("TOTAL") && (line.contains("£") || line.contains("$") || line.contains("€"))) {
                    String[] parts = line.split("TOTAL");
                    if (parts.length > 1) {
                        return parts[1].trim();
                    }
                }
            }
        }
        return "N/A";
    }

    private String extractDate(String text) {
        // Look for "Invoice Date:"
        if (text.contains("Invoice Date:")) {
            String[] lines = text.split("\n");
            for (String line : lines) {
                if (line.contains("Invoice Date:")) {
                    return line.replace("Invoice Date:", "").trim();
                }
            }
        }
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private void applyFilters() {
        filteredInvoices.setPredicate(invoice -> {
            // Search filter
            String searchText = searchField.getText().toLowerCase();
            if (!searchText.isEmpty()) {
                boolean matchesSearch = invoice.getInvoiceId().toLowerCase().contains(searchText) ||
                        invoice.getCustomer().toLowerCase().contains(searchText);
                if (!matchesSearch) return false;
            }

            // Type filter
            String typeFilter = typeFilterBox.getValue();
            if (!"All".equals(typeFilter)) {
                if (typeFilter.contains("PO") && !invoice.getType().equals("Company")) return false;
                if (typeFilter.contains("FO") && !invoice.getType().equals("Person")) return false;
            }

            // Date range filter
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate != null && invoice.getParsedDate().isBefore(startDate)) return false;
            if (endDate != null && invoice.getParsedDate().isAfter(endDate)) return false;

            return true;
        });
    }

    @FXML
    private void handleRefresh() {
        loadInvoices();
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        typeFilterBox.setValue("All");
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    @FXML
    private void handleOpenPdf() {
        InvoiceRecord selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an invoice to open.");
            return;
        }

        try {
            Desktop.getDesktop().open(selected.getFile());
        } catch (Exception e) {
            showAlert("Error", "Could not open PDF: " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenFolder() {
        String saveLocation = loadSaveLocation();
        if (saveLocation != null && !saveLocation.isEmpty()) {
            try {
                Desktop.getDesktop().open(new File(saveLocation));
            } catch (Exception e) {
                showAlert("Error", "Could not open folder: " + e.getMessage());
            }
        }
    }

    private String loadSaveLocation() {
        Properties properties = new Properties();
        File configFile = new File("company-details.properties");

        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                properties.load(in);
                return properties.getProperty("saveLocation", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
